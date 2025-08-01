-- 쿼리 튜닝을 위한 성능 테스트 쿼리 모음
-- 다양한 시나리오의 쿼리 성능을 테스트할 수 있습니다.

USE hhplus;

-- 1. 사용자별 주문 통계 (JOIN 성능 테스트)
-- 실행 시간 측정: EXPLAIN ANALYZE
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

-- 2. 상품별 판매 통계 (복잡한 JOIN 및 집계)
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

-- 3. 월별 매출 통계 (날짜 기반 집계)
SELECT 
    YEAR(o.created_at) as year,
    MONTH(o.created_at) as month,
    COUNT(o.order_id) as total_orders,
    SUM(o.total_price) as total_revenue,
    AVG(o.total_price) as avg_order_value
FROM orders o
WHERE o.order_status = 'PAID'
    AND o.created_at >= DATE_SUB(NOW(), INTERVAL 12 MONTH)
GROUP BY YEAR(o.created_at), MONTH(o.created_at)
ORDER BY year DESC, month DESC;

-- 4. 쿠폰 사용 통계 (서브쿼리 성능 테스트)
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

-- 5. 잔액 거래 내역 분석 (윈도우 함수 성능 테스트)
SELECT 
    bt.id,
    bt.balance_id,
    bt.transaction_type,
    bt.amount,
    b.balance as current_balance,
    SUM(CASE WHEN bt.transaction_type = 'CHARGE' THEN bt.amount ELSE -bt.amount END) 
        OVER (PARTITION BY bt.balance_id ORDER BY bt.id) as running_balance
FROM balance_transaction bt
JOIN balance b ON bt.balance_id = b.id
WHERE bt.balance_id BETWEEN 1 AND 100
ORDER BY bt.balance_id, bt.id;

-- 6. 결제 방법별 통계 (CASE WHEN 성능 테스트)
SELECT 
    payment_method,
    COUNT(*) as total_payments,
    SUM(amount) as total_amount,
    AVG(amount) as avg_amount,
    COUNT(CASE WHEN payment_status = 'COMPLETED' THEN 1 END) as completed_payments,
    COUNT(CASE WHEN payment_status = 'FAILED' THEN 1 END) as failed_payments
FROM payment
WHERE paid_at IS NOT NULL
GROUP BY payment_method
ORDER BY total_amount DESC;

-- 7. 사용자별 쿠폰 사용 패턴 (복합 조건 쿼리)
SELECT 
    u.user_id,
    u.username,
    COUNT(uc.user_coupon_id) as total_coupons,
    COUNT(CASE WHEN uc.used_status = 'USED' THEN 1 END) as used_coupons,
    COUNT(CASE WHEN uc.used_status = 'UNUSED' THEN 1 END) as unused_coupons,
    COUNT(CASE WHEN c.expired_at < NOW() THEN 1 END) as expired_coupons
FROM user u
LEFT JOIN user_coupon uc ON u.user_id = uc.user_id
LEFT JOIN coupon c ON uc.coupon_id = c.coupon_id
WHERE u.user_id BETWEEN 1 AND 500
GROUP BY u.user_id, u.username
HAVING total_coupons > 0
ORDER BY used_coupons DESC;

-- 8. 상품 카테고리별 매출 분석 (문자열 함수 성능 테스트)
SELECT 
    CASE 
        WHEN p.name LIKE '%iPhone%' THEN 'iPhone'
        WHEN p.name LIKE '%MacBook%' THEN 'MacBook'
        WHEN p.name LIKE '%iPad%' THEN 'iPad'
        WHEN p.name LIKE '%Apple Watch%' THEN 'Apple Watch'
        WHEN p.name LIKE '%AirPods%' THEN 'AirPods'
        WHEN p.name LIKE '%Samsung%' THEN 'Samsung'
        WHEN p.name LIKE '%LG%' THEN 'LG'
        WHEN p.name LIKE '%Nike%' THEN 'Nike'
        WHEN p.name LIKE '%Adidas%' THEN 'Adidas'
        WHEN p.name LIKE '%Sony%' THEN 'Sony'
        ELSE '기타'
    END as category,
    COUNT(DISTINCT p.product_id) as product_count,
    SUM(op.unit_price * op.quantity) as total_revenue,
    AVG(op.unit_price) as avg_price
FROM product p
LEFT JOIN order_product op ON p.product_id = op.product_id
WHERE p.status = 'SELLING'
GROUP BY category
ORDER BY total_revenue DESC;

-- 9. 시간대별 주문 분석 (시간 함수 성능 테스트)
SELECT 
    HOUR(o.created_at) as hour,
    COUNT(o.order_id) as order_count,
    SUM(o.total_price) as total_revenue,
    AVG(o.total_price) as avg_order_value
FROM orders o
WHERE o.created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)
GROUP BY HOUR(o.created_at)
ORDER BY hour;

-- 10. 재고 부족 상품 조회 (서브쿼리 및 EXISTS 성능 테스트)
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
    AND EXISTS (
        SELECT 1 FROM orders o 
        WHERE o.order_status = 'PAID' 
        AND o.created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)
    )
GROUP BY p.product_id, p.name, p.price, s.quantity
ORDER BY current_stock ASC;

-- 11. 고객 세그먼트 분석 (복합 집계 함수)
SELECT 
    CASE 
        WHEN total_spent >= 1000000 THEN 'VIP'
        WHEN total_spent >= 500000 THEN 'Gold'
        WHEN total_spent >= 100000 THEN 'Silver'
        ELSE 'Bronze'
    END as customer_segment,
    COUNT(*) as customer_count,
    AVG(total_spent) as avg_spent,
    SUM(total_spent) as total_segment_revenue
FROM (
    SELECT 
        u.user_id,
        SUM(o.total_price) as total_spent
    FROM user u
    LEFT JOIN orders o ON u.user_id = o.user_id
    WHERE o.order_status = 'PAID'
    GROUP BY u.user_id
) customer_stats
GROUP BY customer_segment
ORDER BY avg_spent DESC;

-- 12. 쿼리 성능 측정을 위한 저장 프로시저
DELIMITER //
CREATE PROCEDURE PerformanceTest()
BEGIN
    DECLARE start_time TIMESTAMP;
    DECLARE end_time TIMESTAMP;
    DECLARE execution_time DECIMAL(10,3);
    
    SET start_time = NOW();
    
    -- 테스트 쿼리 실행
    SELECT COUNT(*) as total_users FROM user;
    SELECT COUNT(*) as total_orders FROM orders;
    SELECT COUNT(*) as total_products FROM product;
    
    SET end_time = NOW();
    SET execution_time = TIMESTAMPDIFF(MICROSECOND, start_time, end_time) / 1000000;
    
    SELECT CONCAT('실행 시간: ', execution_time, '초') as execution_time;
END //
DELIMITER ;

-- 13. 인덱스 사용 현황 확인
SELECT 
    table_name,
    index_name,
    column_name,
    cardinality
FROM information_schema.statistics 
WHERE table_schema = 'hhplus'
ORDER BY table_name, index_name;

-- 14. 테이블 크기 및 통계 정보
SELECT 
    table_name,
    table_rows,
    data_length,
    index_length,
    (data_length + index_length) as total_size
FROM information_schema.tables 
WHERE table_schema = 'hhplus'
ORDER BY total_size DESC;

-- 15. 느린 쿼리 로그 활성화 (MySQL 설정)
-- SET GLOBAL slow_query_log = 'ON';
-- SET GLOBAL long_query_time = 1;
-- SET GLOBAL log_queries_not_using_indexes = 'ON';

-- 16. 성능 테스트 결과 요약
SELECT '쿼리 튜닝 테스트 준비 완료' as status;
SELECT COUNT(*) as total_records FROM (
    SELECT 1 FROM user UNION ALL
    SELECT 1 FROM product UNION ALL
    SELECT 1 FROM orders UNION ALL
    SELECT 1 FROM payment UNION ALL
    SELECT 1 FROM user_coupon UNION ALL
    SELECT 1 FROM balance_transaction
) all_tables; 