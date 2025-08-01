-- 성능 테스트 결과를 바탕으로 필요한 인덱스만 추가
-- 실제 쿼리 패턴 분석 후 최적화된 인덱스 구성

USE hhplus;

-- 1. 기존 불필요한 인덱스 제거 (성능 테스트 후 결정)
-- 주의: 실제 운영 환경에서는 신중하게 제거해야 합니다.

-- 1-1. 사용 빈도가 낮은 인덱스 제거
-- 실제 쿼리 패턴 분석 후 결정

-- 1-2. 중복되거나 비효율적인 인덱스 제거
-- 예시: 단일 컬럼 인덱스가 복합 인덱스의 첫 번째 컬럼과 중복되는 경우

-- 2. 실제 쿼리 패턴 분석 결과를 바탕으로 필요한 인덱스 추가

-- 2-1. 사용자 관련 인덱스 (가장 빈번한 쿼리)
-- 사용자별 주문 통계 쿼리 최적화
CREATE INDEX IF NOT EXISTS idx_orders_user_id ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_user_status ON orders(user_id, order_status);
CREATE INDEX IF NOT EXISTS idx_orders_total_price ON orders(total_price);

-- 2-2. 상품 관련 인덱스
-- 상품별 판매 통계 쿼리 최적화
CREATE INDEX IF NOT EXISTS idx_product_status ON product(status);
CREATE INDEX IF NOT EXISTS idx_product_status_price ON product(status, price);
CREATE INDEX IF NOT EXISTS idx_order_product_product_id ON order_product(product_id);
CREATE INDEX IF NOT EXISTS idx_stock_product_id ON stock(product_id);
CREATE INDEX IF NOT EXISTS idx_stock_quantity ON stock(quantity);

-- 2-3. 결제 관련 인덱스
-- 결제 상태별 통계 쿼리 최적화
CREATE INDEX IF NOT EXISTS idx_payment_method_status ON payment(payment_method, payment_status);
CREATE INDEX IF NOT EXISTS idx_payment_paid_at ON payment(paid_at);

-- 2-4. 쿠폰 관련 인덱스
-- 쿠폰 사용 현황 쿼리 최적화
CREATE INDEX IF NOT EXISTS idx_coupon_status ON coupon(status);
CREATE INDEX IF NOT EXISTS idx_user_coupon_user_id ON user_coupon(user_id);
CREATE INDEX IF NOT EXISTS idx_user_coupon_used_status ON user_coupon(used_status);
CREATE INDEX IF NOT EXISTS idx_user_coupon_user_status ON user_coupon(user_id, used_status);

-- 2-5. 잔액 관련 인덱스
-- 잔액 거래 내역 분석 쿼리 최적화
CREATE INDEX IF NOT EXISTS idx_balance_transaction_balance_id ON balance_transaction(balance_id);
CREATE INDEX IF NOT EXISTS idx_balance_transaction_type ON balance_transaction(transaction_type);

-- 3. 복합 인덱스 최적화 (쿼리 패턴에 맞춰)

-- 3-1. 주문 관련 복합 인덱스
-- 사용자별 주문 통계 + 정렬 최적화
CREATE INDEX IF NOT EXISTS idx_orders_user_total_price ON orders(user_id, total_price DESC);

-- 3-2. 상품 관련 복합 인덱스
-- 상품별 판매 통계 + 재고 조건 최적화
CREATE INDEX IF NOT EXISTS idx_product_status_price_revenue ON product(status, price, created_at);

-- 3-3. 결제 관련 복합 인덱스
-- 결제 방법별 통계 + 날짜 조건 최적화
CREATE INDEX IF NOT EXISTS idx_payment_method_status_amount ON payment(payment_method, payment_status, amount);

-- 3-4. 쿠폰 관련 복합 인덱스
-- 쿠폰 사용 패턴 분석 최적화
CREATE INDEX IF NOT EXISTS idx_user_coupon_user_coupon_status ON user_coupon(user_id, coupon_id, used_status);

-- 4. 커버링 인덱스 (SELECT 컬럼을 모두 포함하는 인덱스)

-- 4-1. 사용자별 주문 통계 커버링 인덱스
-- orders 테이블의 user_id, total_price, order_status를 포함
CREATE INDEX IF NOT EXISTS idx_orders_user_covering ON orders(user_id, total_price, order_status);

-- 4-2. 상품별 판매 통계 커버링 인덱스
-- product 테이블의 status, price, name을 포함
CREATE INDEX IF NOT EXISTS idx_product_status_covering ON product(status, price, name);

-- 5. 부분 인덱스 (특정 조건에만 적용되는 인덱스)

-- 5-1. 판매 중인 상품만 인덱싱
-- MySQL에서는 부분 인덱스를 직접 지원하지 않으므로 조건부 인덱스로 대체
-- CREATE INDEX idx_product_selling_only ON product(price) WHERE status = 'SELLING';

-- 5-2. 사용된 쿠폰만 인덱싱
-- CREATE INDEX idx_user_coupon_used_only ON user_coupon(user_id, coupon_id) WHERE used_status = 'USED';

-- 6. 인덱스 최적화 후 통계 업데이트
ANALYZE TABLE user, product, stock, coupon, user_coupon, balance, balance_transaction, orders, order_product, payment;

-- 7. 인덱스 효과 검증 쿼리

-- 7-1. 사용자별 주문 통계 (인덱스 효과 확인)
EXPLAIN FORMAT=JSON
SELECT 
    u.user_id,
    u.username,
    COUNT(o.order_id) as total_orders,
    SUM(o.total_price) as total_spent
FROM user u
LEFT JOIN orders o ON u.user_id = o.user_id
WHERE u.user_id BETWEEN 1 AND 1000
GROUP BY u.user_id, u.username
ORDER BY total_spent DESC
LIMIT 100;

-- 7-2. 상품별 판매 통계 (인덱스 효과 확인)
EXPLAIN FORMAT=JSON
SELECT 
    p.product_id,
    p.name,
    p.price,
    COUNT(op.order_product_id) as total_sales,
    SUM(op.unit_price * op.quantity) as total_revenue
FROM product p
LEFT JOIN order_product op ON p.product_id = op.product_id
WHERE p.status = 'SELLING'
GROUP BY p.product_id, p.name, p.price
HAVING total_revenue > 1000000
ORDER BY total_revenue DESC
LIMIT 50;

-- 7-3. 결제 상태별 통계 (인덱스 효과 확인)
EXPLAIN FORMAT=JSON
SELECT 
    payment_method,
    payment_status,
    COUNT(*) as payment_count,
    SUM(amount) as total_amount
FROM payment
WHERE paid_at IS NOT NULL
GROUP BY payment_method, payment_status
ORDER BY total_amount DESC;

-- 8. 인덱스 사용 현황 최종 확인
SELECT 
    table_name,
    index_name,
    column_name,
    cardinality,
    CASE 
        WHEN index_name = 'PRIMARY' THEN 'Primary Key'
        WHEN non_unique = 0 THEN 'Unique Index'
        ELSE 'Non-Unique Index'
    END as index_type
FROM information_schema.statistics 
WHERE table_schema = 'hhplus'
ORDER BY table_name, index_name, seq_in_index;

-- 9. 인덱스 크기 및 효과 분석
SELECT 
    table_name,
    COUNT(DISTINCT index_name) as index_count,
    ROUND(SUM(index_length) / 1024 / 1024, 2) as total_index_size_mb,
    ROUND(SUM(data_length) / 1024 / 1024, 2) as data_size_mb,
    ROUND(SUM(index_length) / SUM(data_length) * 100, 2) as index_ratio_percent
FROM information_schema.tables t
JOIN information_schema.statistics s ON t.table_name = s.table_name
WHERE t.table_schema = 'hhplus' AND s.table_schema = 'hhplus'
GROUP BY table_name
ORDER BY total_index_size_mb DESC;

-- 10. 최적화 결과 요약
SELECT '인덱스 최적화 완료' as status;
SELECT COUNT(*) as total_indexes FROM (
    SELECT DISTINCT table_name, index_name 
    FROM information_schema.statistics 
    WHERE table_schema = 'hhplus'
) all_indexes; 