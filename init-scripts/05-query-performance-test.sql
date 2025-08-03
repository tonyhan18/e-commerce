-- 실제 쿼리 성능 테스트 및 인덱스 필요성 분석
-- 각 쿼리의 실행 계획을 분석하여 필요한 인덱스를 식별합니다.

USE hhplus;

-- 1. 성능 측정 설정
SET profiling = 1;
SET profiling_history_size = 100;

-- 2. 실제 비즈니스 쿼리 성능 테스트

-- 테스트 1: 사용자별 주문 통계 (가장 빈번한 쿼리)
-- 실행 전 EXPLAIN 분석
EXPLAIN FORMAT=JSON
SELECT 
    u.user_id,
    u.username,
    COUNT(o.order_id) as total_orders,
    SUM(o.total_price) as total_spent,
    AVG(o.total_price) as avg_order_value
FROM user u
LEFT JOIN orders o ON u.user_id = o.user_id
WHERE u.user_id BETWEEN 1 AND 1000
GROUP BY u.user_id, u.username
ORDER BY total_spent DESC
LIMIT 100;

-- 실제 실행 및 시간 측정
SELECT 
    u.user_id,
    u.username,
    COUNT(o.order_id) as total_orders,
    SUM(o.total_price) as total_spent,
    AVG(o.total_price) as avg_order_value
FROM user u
LEFT JOIN orders o ON u.user_id = o.user_id
WHERE u.user_id BETWEEN 1 AND 1000
GROUP BY u.user_id, u.username
ORDER BY total_spent DESC
LIMIT 100;

-- 테스트 2: 상품별 판매 통계
EXPLAIN FORMAT=JSON
SELECT 
    p.product_id,
    p.name,
    p.price,
    p.status,
    s.quantity as current_stock,
    COUNT(op.order_product_id) as total_sales,
    SUM(op.quantity) as total_quantity_sold,
    SUM(op.unit_price * op.quantity) as total_revenue
FROM product p
LEFT JOIN stock s ON p.product_id = s.product_id
LEFT JOIN order_product op ON p.product_id = op.product_id
WHERE p.status = 'SELLING'
GROUP BY p.product_id, p.name, p.price, p.status, s.quantity
HAVING total_revenue > 1000000
ORDER BY total_revenue DESC
LIMIT 50;

SELECT 
    p.product_id,
    p.name,
    p.price,
    p.status,
    s.quantity as current_stock,
    COUNT(op.order_product_id) as total_sales,
    SUM(op.quantity) as total_quantity_sold,
    SUM(op.unit_price * op.quantity) as total_revenue
FROM product p
LEFT JOIN stock s ON p.product_id = s.product_id
LEFT JOIN order_product op ON p.product_id = op.product_id
WHERE p.status = 'SELLING'
GROUP BY p.product_id, p.name, p.price, p.status, s.quantity
HAVING total_revenue > 1000000
ORDER BY total_revenue DESC
LIMIT 50;

-- 테스트 3: 결제 상태별 통계
EXPLAIN FORMAT=JSON
SELECT 
    payment_method,
    payment_status,
    COUNT(*) as payment_count,
    SUM(amount) as total_amount,
    AVG(amount) as avg_amount
FROM payment
WHERE paid_at IS NOT NULL
GROUP BY payment_method, payment_status
ORDER BY total_amount DESC;

SELECT 
    payment_method,
    payment_status,
    COUNT(*) as payment_count,
    SUM(amount) as total_amount,
    AVG(amount) as avg_amount
FROM payment
WHERE paid_at IS NOT NULL
GROUP BY payment_method, payment_status
ORDER BY total_amount DESC;

-- 테스트 4: 쿠폰 사용 현황
EXPLAIN FORMAT=JSON
SELECT 
    c.coupon_id,
    c.name,
    c.discount_rate,
    COUNT(uc.user_coupon_id) as total_issued,
    SUM(CASE WHEN uc.used_status = 'USED' THEN 1 ELSE 0 END) as total_used,
    SUM(CASE WHEN uc.used_status = 'UNUSED' THEN 1 ELSE 0 END) as total_unused
FROM coupon c
LEFT JOIN user_coupon uc ON c.coupon_id = uc.coupon_id
WHERE c.status = 'PUBLISHABLE'
GROUP BY c.coupon_id, c.name, c.discount_rate
ORDER BY total_issued DESC;

SELECT 
    c.coupon_id,
    c.name,
    c.discount_rate,
    COUNT(uc.user_coupon_id) as total_issued,
    SUM(CASE WHEN uc.used_status = 'USED' THEN 1 ELSE 0 END) as total_used,
    SUM(CASE WHEN uc.used_status = 'UNUSED' THEN 1 ELSE 0 END) as total_unused
FROM coupon c
LEFT JOIN user_coupon uc ON c.coupon_id = uc.coupon_id
WHERE c.status = 'PUBLISHABLE'
GROUP BY c.coupon_id, c.name, c.discount_rate
ORDER BY total_issued DESC;

-- 테스트 5: 재고 부족 상품 조회
EXPLAIN FORMAT=JSON
SELECT 
    p.product_id,
    p.name,
    p.price,
    s.quantity as current_stock,
    COUNT(op.order_product_id) as total_orders
FROM product p
JOIN stock s ON p.product_id = s.product_id
LEFT JOIN order_product op ON p.product_id = op.product_id
WHERE p.status = 'SELLING'
    AND s.quantity < 100
GROUP BY p.product_id, p.name, p.price, s.quantity
ORDER BY current_stock ASC;

SELECT 
    p.product_id,
    p.name,
    p.price,
    s.quantity as current_stock,
    COUNT(op.order_product_id) as total_orders
FROM product p
JOIN stock s ON p.product_id = s.product_id
LEFT JOIN order_product op ON p.product_id = op.product_id
WHERE p.status = 'SELLING'
    AND s.quantity < 100
GROUP BY p.product_id, p.name, p.price, s.quantity
ORDER BY current_stock ASC;

-- 테스트 6: 사용자별 쿠폰 사용 패턴
EXPLAIN FORMAT=JSON
SELECT 
    u.user_id,
    u.username,
    COUNT(uc.user_coupon_id) as total_coupons,
    COUNT(CASE WHEN uc.used_status = 'USED' THEN 1 END) as used_coupons,
    COUNT(CASE WHEN uc.used_status = 'UNUSED' THEN 1 END) as unused_coupons
FROM user u
LEFT JOIN user_coupon uc ON u.user_id = uc.user_id
WHERE u.user_id BETWEEN 1 AND 500
GROUP BY u.user_id, u.username
HAVING total_coupons > 0
ORDER BY used_coupons DESC;

SELECT 
    u.user_id,
    u.username,
    COUNT(uc.user_coupon_id) as total_coupons,
    COUNT(CASE WHEN uc.used_status = 'USED' THEN 1 END) as used_coupons,
    COUNT(CASE WHEN uc.used_status = 'UNUSED' THEN 1 END) as unused_coupons
FROM user u
LEFT JOIN user_coupon uc ON u.user_id = uc.user_id
WHERE u.user_id BETWEEN 1 AND 500
GROUP BY u.user_id, u.username
HAVING total_coupons > 0
ORDER BY used_coupons DESC;

-- 테스트 7: 잔액 거래 내역 분석
EXPLAIN FORMAT=JSON
SELECT 
    bt.balance_id,
    bt.transaction_type,
    bt.amount,
    b.balance as current_balance
FROM balance_transaction bt
JOIN balance b ON bt.balance_id = b.id
WHERE bt.balance_id BETWEEN 1 AND 100
ORDER BY bt.balance_id, bt.id
LIMIT 1000;

SELECT 
    bt.balance_id,
    bt.transaction_type,
    bt.amount,
    b.balance as current_balance
FROM balance_transaction bt
JOIN balance b ON bt.balance_id = b.id
WHERE bt.balance_id BETWEEN 1 AND 100
ORDER BY bt.balance_id, bt.id
LIMIT 1000;

-- 3. 성능 프로파일 결과 확인
SHOW PROFILES;

-- 4. 각 쿼리의 상세 성능 정보
SELECT 
    query_id,
    duration,
    query
FROM information_schema.profiling
WHERE query_id IN (SELECT query_id FROM information_schema.profiling GROUP BY query_id ORDER BY MAX(duration) DESC LIMIT 10)
ORDER BY query_id, seq;

-- 5. 느린 쿼리 식별 (1초 이상)
SELECT 
    query_id,
    duration,
    SUBSTRING(query, 1, 100) as query_preview
FROM information_schema.profiling
WHERE duration > 1
ORDER BY duration DESC;

-- 6. 인덱스 사용 현황 분석
SELECT 
    table_name,
    index_name,
    column_name,
    cardinality,
    CASE 
        WHEN cardinality > 10000 THEN 'High Selectivity'
        WHEN cardinality > 1000 THEN 'Medium Selectivity'
        ELSE 'Low Selectivity'
    END as selectivity
FROM information_schema.statistics 
WHERE table_schema = 'hhplus'
ORDER BY cardinality DESC;

-- 7. 테이블별 스캔 패턴 분석
SELECT 
    table_name,
    table_rows,
    ROUND(data_length / 1024 / 1024, 2) as data_size_mb,
    ROUND(index_length / 1024 / 1024, 2) as index_size_mb
FROM information_schema.tables 
WHERE table_schema = 'hhplus'
ORDER BY table_rows DESC; 