-- MySQL 초기 데이터 생성 스크립트
-- e-commerce 시스템의 모든 도메인에 대한 샘플 데이터

USE hhplus;

-- 테이블 생성 DDL
CREATE TABLE IF NOT EXISTS user (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS product (
    product_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL
);

CREATE TABLE IF NOT EXISTS stock (
    stock_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL
);

CREATE TABLE IF NOT EXISTS coupon (
    coupon_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    discount_rate DOUBLE NOT NULL,
    quantity INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    expired_at DATETIME(6) NOT NULL
);

CREATE TABLE IF NOT EXISTS user_coupon (
    user_coupon_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    coupon_id BIGINT NOT NULL,
    used_status VARCHAR(50) NOT NULL,
    issued_at DATETIME(6) NOT NULL,
    used_at DATETIME(6) NULL
);

CREATE TABLE IF NOT EXISTS balance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    balance BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS balance_transaction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    balance_id BIGINT NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    amount BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS orders (
    order_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    user_coupon_id BIGINT NULL,
    order_status VARCHAR(50) NOT NULL,
    total_price BIGINT NOT NULL,
    discount_price BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS order_product (
    order_product_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    unit_price BIGINT NOT NULL,
    quantity INT NOT NULL
);

CREATE TABLE IF NOT EXISTS payment (
    payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    amount BIGINT NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    payment_status VARCHAR(50) NOT NULL,
    paid_at DATETIME(6) NULL
);

-- 1. 사용자 데이터
INSERT INTO user (user_id, username) VALUES
(1, '김철수'),
(2, '이영희'),
(3, '박민수'),
(4, '정수진'),
(5, '최동욱'),
(6, '한미영'),
(7, '송태호'),
(8, '윤지영'),
(9, '강현우'),
(10, '임서연');

-- 2. 상품 데이터
INSERT INTO product (product_id, name, price, status, created_at, updated_at) VALUES
(1, 'iPhone 15 Pro', 1500000, 'SELLING', NOW(), NOW()),
(2, 'MacBook Air M2', 1800000, 'SELLING', NOW(), NOW()),
(3, 'iPad Air', 800000, 'SELLING', NOW(), NOW()),
(4, 'Apple Watch Series 9', 500000, 'SELLING', NOW(), NOW()),
(5, 'AirPods Pro', 300000, 'SELLING', NOW(), NOW()),
(6, 'Samsung Galaxy S24', 1200000, 'SELLING', NOW(), NOW()),
(7, 'LG OLED TV 65인치', 2500000, 'SELLING', NOW(), NOW()),
(8, 'Nike 운동화', 150000, 'SELLING', NOW(), NOW()),
(9, 'Adidas 트레이닝복', 80000, 'SELLING', NOW(), NOW()),
(10, 'Sony 헤드폰', 400000, 'SELLING', NOW(), NOW()),
(11, 'Canon 카메라', 1200000, 'HOLD', NOW(), NOW()),
(12, 'Dell 노트북', 1000000, 'STOP_SELLING', NOW(), NOW());

-- 3. 재고 데이터
INSERT INTO stock (stock_id, product_id, quantity) VALUES
(1, 1, 50),
(2, 2, 30),
(3, 3, 100),
(4, 4, 75),
(5, 5, 200),
(6, 6, 40),
(7, 7, 15),
(8, 8, 300),
(9, 9, 500),
(10, 10, 60),
(11, 11, 0),
(12, 12, 0);

-- 4. 쿠폰 데이터
INSERT INTO coupon (coupon_id, name, discount_rate, quantity, status, expired_at) VALUES
(1, '신규 가입 쿠폰', 0.1, 1000, 'PUBLISHABLE', DATE_ADD(NOW(), INTERVAL 30 DAY)),
(2, '생일 축하 쿠폰', 0.15, 500, 'PUBLISHABLE', DATE_ADD(NOW(), INTERVAL 60 DAY)),
(3, '첫 구매 쿠폰', 0.2, 200, 'PUBLISHABLE', DATE_ADD(NOW(), INTERVAL 90 DAY)),
(4, 'VIP 쿠폰', 0.25, 100, 'PUBLISHABLE', DATE_ADD(NOW(), INTERVAL 120 DAY)),
(5, '시즌 할인 쿠폰', 0.05, 2000, 'PUBLISHABLE', DATE_ADD(NOW(), INTERVAL 45 DAY)),
(6, '만료된 쿠폰', 0.1, 100, 'REGISTERED', DATE_SUB(NOW(), INTERVAL 10 DAY)),
(7, '취소된 쿠폰', 0.1, 50, 'CANCELED', DATE_ADD(NOW(), INTERVAL 30 DAY));

-- 5. 사용자 쿠폰 데이터
INSERT INTO user_coupon (user_coupon_id, user_id, coupon_id, used_status, issued_at, used_at) VALUES
(1, 1, 1, 'UNUSED', NOW(), NULL),
(2, 1, 2, 'USED', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
(3, 2, 1, 'UNUSED', NOW(), NULL),
(4, 2, 3, 'UNUSED', NOW(), NULL),
(5, 3, 1, 'UNUSED', NOW(), NULL),
(6, 3, 4, 'USED', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY)),
(7, 4, 2, 'UNUSED', NOW(), NULL),
(8, 5, 1, 'UNUSED', NOW(), NULL),
(9, 5, 5, 'UNUSED', NOW(), NULL),
(10, 6, 1, 'UNUSED', NOW(), NULL);

-- 6. 잔액 데이터
INSERT INTO balance (id, user_id, balance) VALUES
(1, 1, 500000),
(2, 2, 1000000),
(3, 3, 250000),
(4, 4, 750000),
(5, 5, 300000),
(6, 6, 1200000),
(7, 7, 800000),
(8, 8, 450000),
(9, 9, 900000),
(10, 10, 600000);

-- 7. 잔액 거래 내역 데이터
INSERT INTO balance_transaction (id, balance_id, transaction_type, amount) VALUES
(1, 1, 'CHARGE', 500000),
(2, 2, 'CHARGE', 1000000),
(3, 2, 'USE', 150000),
(4, 3, 'CHARGE', 250000),
(5, 4, 'CHARGE', 750000),
(6, 4, 'USE', 200000),
(7, 5, 'CHARGE', 300000),
(8, 6, 'CHARGE', 1200000),
(9, 6, 'USE', 300000),
(10, 7, 'CHARGE', 800000),
(11, 8, 'CHARGE', 450000),
(12, 9, 'CHARGE', 900000),
(13, 10, 'CHARGE', 600000);

-- 8. 주문 데이터
INSERT INTO orders (order_id, user_id, user_coupon_id, order_status, total_price, discount_price) VALUES
(1, 1, 2, 'PAID', 1350000, 150000),
(2, 2, 6, 'PAID', 800000, 160000),
(3, 3, NULL, 'CREATED', 1500000, 0),
(4, 4, NULL, 'PAID', 300000, 0),
(5, 5, 9, 'CREATED', 1200000, 60000);

-- 9. 주문 상품 데이터
INSERT INTO order_product (order_product_id, order_id, product_id, product_name, unit_price, quantity) VALUES
(1, 1, 1, 'iPhone 15 Pro', 1500000, 1),
(2, 2, 2, 'MacBook Air M2', 1800000, 1),
(3, 2, 5, 'AirPods Pro', 300000, 1),
(4, 3, 7, 'LG OLED TV 65인치', 2500000, 1),
(5, 4, 8, 'Nike 운동화', 150000, 2),
(6, 5, 3, 'iPad Air', 800000, 1),
(7, 5, 4, 'Apple Watch Series 9', 500000, 1);

-- 10. 결제 데이터
INSERT INTO payment (payment_id, order_id, amount, payment_method, payment_status, paid_at) VALUES
(1, 1, 1350000, 'CARD', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(2, 2, 800000, 'CARD', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(3, 3, 1500000, 'UNKNOWN', 'READY', NULL),
(4, 4, 300000, 'CASH', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(5, 5, 1200000, 'VIRTUAL_ACCOUNT', 'WAITING', NULL);

-- 추가 샘플 데이터 (더 많은 시나리오)

-- 11. 추가 사용자 쿠폰 (다양한 상태)
INSERT INTO user_coupon (user_coupon_id, user_id, coupon_id, used_status, issued_at, used_at) VALUES
(11, 7, 1, 'UNUSED', NOW(), NULL),
(12, 7, 5, 'UNUSED', NOW(), NULL),
(13, 8, 1, 'UNUSED', NOW(), NULL),
(14, 8, 2, 'USED', DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY)),
(15, 9, 1, 'UNUSED', NOW(), NULL),
(16, 9, 3, 'UNUSED', NOW(), NULL),
(17, 10, 1, 'UNUSED', NOW(), NULL),
(18, 10, 4, 'UNUSED', NOW(), NULL);

-- 12. 추가 주문 (다양한 상태와 쿠폰 사용)
INSERT INTO orders (order_id, user_id, user_coupon_id, order_status, total_price, discount_price) VALUES
(6, 6, NULL, 'PAID', 500000, 0),
(7, 7, 11, 'CREATED', 900000, 90000),
(8, 8, 14, 'PAID', 1200000, 180000),
(9, 9, 16, 'CREATED', 2000000, 400000),
(10, 10, 18, 'PAID', 800000, 200000);

-- 13. 추가 주문 상품
INSERT INTO order_product (order_product_id, order_id, product_id, product_name, unit_price, quantity) VALUES
(8, 6, 4, 'Apple Watch Series 9', 500000, 1),
(9, 7, 6, 'Samsung Galaxy S24', 1200000, 1),
(10, 7, 10, 'Sony 헤드폰', 400000, 1),
(11, 8, 1, 'iPhone 15 Pro', 1500000, 1),
(12, 9, 2, 'MacBook Air M2', 1800000, 1),
(13, 9, 3, 'iPad Air', 800000, 1),
(14, 10, 5, 'AirPods Pro', 300000, 1),
(15, 10, 8, 'Nike 운동화', 150000, 2),
(16, 10, 9, 'Adidas 트레이닝복', 80000, 1);

-- 14. 추가 결제 데이터
INSERT INTO payment (payment_id, order_id, amount, payment_method, payment_status, paid_at) VALUES
(6, 6, 500000, 'CARD', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(7, 7, 900000, 'UNKNOWN', 'READY', NULL),
(8, 8, 1200000, 'CARD', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 12 DAY)),
(9, 9, 2000000, 'VIRTUAL_ACCOUNT', 'WAITING', NULL),
(10, 10, 800000, 'CASH', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 1 DAY));

-- 15. 추가 잔액 거래 내역 (더 많은 거래 기록)
INSERT INTO balance_transaction (id, balance_id, transaction_type, amount) VALUES
(14, 1, 'USE', 1350000),
(15, 2, 'CHARGE', 50000),
(16, 3, 'USE', 1500000),
(17, 4, 'CHARGE', 100000),
(18, 5, 'USE', 1200000),
(19, 6, 'CHARGE', 200000),
(20, 7, 'USE', 900000),
(21, 8, 'CHARGE', 300000),
(22, 9, 'USE', 2000000),
(23, 10, 'CHARGE', 400000);

-- 16. 재고 업데이트 (주문에 따른 재고 감소 반영)
UPDATE stock SET quantity = quantity - 1 WHERE product_id IN (1, 2, 4, 5, 6, 7, 8, 9, 10);
UPDATE stock SET quantity = quantity - 2 WHERE product_id IN (8, 9);

-- 17. 쿠폰 수량 업데이트 (발급된 쿠폰 수량 감소)
UPDATE coupon SET quantity = quantity - 18 WHERE coupon_id IN (1, 2, 3, 4, 5);

-- 18. 추가 상품 (다양한 카테고리)
INSERT INTO product (product_id, name, price, status, created_at, updated_at) VALUES
(13, 'PlayStation 5', 600000, 'SELLING', NOW(), NOW()),
(14, 'Xbox Series X', 500000, 'SELLING', NOW(), NOW()),
(15, 'Nintendo Switch', 350000, 'SELLING', NOW(), NOW()),
(16, 'DJI 드론', 800000, 'SELLING', NOW(), NOW()),
(17, 'GoPro 카메라', 400000, 'SELLING', NOW(), NOW()),
(18, 'Bose 스피커', 300000, 'SELLING', NOW(), NOW()),
(19, 'Dyson 청소기', 700000, 'SELLING', NOW(), NOW()),
(20, 'Philips 전동칫솔', 150000, 'SELLING', NOW(), NOW());

-- 19. 추가 재고
INSERT INTO stock (stock_id, product_id, quantity) VALUES
(13, 13, 25),
(14, 14, 30),
(15, 15, 80),
(16, 16, 15),
(17, 17, 40),
(18, 18, 60),
(19, 19, 35),
(20, 20, 200);

-- 20. 추가 쿠폰 (다양한 할인율)
INSERT INTO coupon (coupon_id, name, discount_rate, quantity, status, expired_at) VALUES
(8, '게임 할인 쿠폰', 0.1, 300, 'PUBLISHABLE', DATE_ADD(NOW(), INTERVAL 45 DAY)),
(9, '전자제품 할인 쿠폰', 0.08, 500, 'PUBLISHABLE', DATE_ADD(NOW(), INTERVAL 60 DAY)),
(10, '생활용품 할인 쿠폰', 0.05, 1000, 'PUBLISHABLE', DATE_ADD(NOW(), INTERVAL 30 DAY)),
(11, '프리미엄 쿠폰', 0.3, 50, 'PUBLISHABLE', DATE_ADD(NOW(), INTERVAL 90 DAY)),
(12, '친구 추천 쿠폰', 0.12, 200, 'PUBLISHABLE', DATE_ADD(NOW(), INTERVAL 40 DAY));

-- 21. 추가 사용자 (더 많은 사용자)
INSERT INTO user (user_id, username) VALUES
(11, '박지성'),
(12, '김연아'),
(13, '손흥민'),
(14, '이강인'),
(15, '김민재'),
(16, '황희찬'),
(17, '조규성'),
(18, '이재성'),
(19, '정우영'),
(20, '구자철');

-- 22. 추가 잔액
INSERT INTO balance (id, user_id, balance) VALUES
(11, 11, 350000),
(12, 12, 950000),
(13, 13, 1800000),
(14, 14, 420000),
(15, 15, 780000),
(16, 16, 1100000),
(17, 17, 650000),
(18, 18, 880000),
(19, 19, 520000),
(20, 20, 1400000);

-- 23. 추가 사용자 쿠폰 (새로운 사용자들)
INSERT INTO user_coupon (user_coupon_id, user_id, coupon_id, used_status, issued_at, used_at) VALUES
(19, 11, 1, 'UNUSED', NOW(), NULL),
(20, 11, 8, 'UNUSED', NOW(), NULL),
(21, 12, 1, 'UNUSED', NOW(), NULL),
(22, 12, 9, 'UNUSED', NOW(), NULL),
(23, 13, 1, 'UNUSED', NOW(), NULL),
(24, 13, 11, 'UNUSED', NOW(), NULL),
(25, 14, 1, 'UNUSED', NOW(), NULL),
(26, 14, 10, 'UNUSED', NOW(), NULL),
(27, 15, 1, 'UNUSED', NOW(), NULL),
(28, 15, 12, 'UNUSED', NOW(), NULL);

-- 24. 추가 주문 (새로운 사용자들의 주문)
INSERT INTO orders (order_id, user_id, user_coupon_id, order_status, total_price, discount_price) VALUES
(11, 11, 20, 'PAID', 540000, 60000),
(12, 12, 22, 'CREATED', 1150000, 92000),
(13, 13, 24, 'PAID', 2100000, 630000),
(14, 14, 26, 'CREATED', 450000, 22500),
(15, 15, 28, 'PAID', 1050000, 126000);

-- 25. 추가 주문 상품
INSERT INTO order_product (order_product_id, order_id, product_id, product_name, unit_price, quantity) VALUES
(17, 11, 13, 'PlayStation 5', 600000, 1),
(18, 12, 14, 'Xbox Series X', 500000, 1),
(19, 12, 15, 'Nintendo Switch', 350000, 1),
(20, 12, 16, 'DJI 드론', 800000, 1),
(21, 13, 17, 'GoPro 카메라', 400000, 1),
(22, 13, 18, 'Bose 스피커', 300000, 1),
(23, 13, 19, 'Dyson 청소기', 700000, 1),
(24, 14, 20, 'Philips 전동칫솔', 150000, 3),
(25, 15, 1, 'iPhone 15 Pro', 1500000, 1);

-- 26. 추가 결제
INSERT INTO payment (payment_id, order_id, amount, payment_method, payment_status, paid_at) VALUES
(11, 11, 540000, 'CARD', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(12, 12, 1150000, 'VIRTUAL_ACCOUNT', 'WAITING', NULL),
(13, 13, 2100000, 'CARD', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(14, 14, 450000, 'UNKNOWN', 'READY', NULL),
(15, 15, 1050000, 'CASH', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 3 DAY));

-- 27. 추가 잔액 거래 내역
INSERT INTO balance_transaction (id, balance_id, transaction_type, amount) VALUES
(24, 11, 'USE', 540000),
(25, 12, 'CHARGE', 100000),
(26, 13, 'USE', 2100000),
(27, 14, 'CHARGE', 50000),
(28, 15, 'USE', 1050000);

-- 28. 재고 추가 감소
UPDATE stock SET quantity = quantity - 1 WHERE product_id IN (13, 14, 15, 16, 17, 18, 19, 1);
UPDATE stock SET quantity = quantity - 2 WHERE product_id = 20;

-- 29. 쿠폰 수량 추가 감소
UPDATE coupon SET quantity = quantity - 10 WHERE coupon_id IN (1, 8, 9, 10, 11, 12);

-- 30. 최종 통계 데이터 (시스템 전체 현황)
-- 총 사용자: 20명
-- 총 상품: 20개 (판매중: 18개, 보류: 1개, 중지: 1개)
-- 총 쿠폰: 12개 (발급가능: 10개, 등록: 1개, 취소: 1개)
-- 총 주문: 15건 (결제완료: 10건, 주문생성: 5건)
-- 총 결제: 15건 (완료: 10건, 대기: 3건, 준비: 2건)
-- 총 잔액 거래: 28건 (충전: 15건, 사용: 13건) 