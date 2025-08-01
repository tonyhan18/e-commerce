-- 쿼리 튜닝을 위한 대용량 성능 테스트 데이터 생성 스크립트
-- 총 100만 건 이상의 데이터 생성

USE hhplus;

-- 1. 대용량 사용자 데이터 (100,000명)
INSERT INTO user (user_id, username) 
SELECT 
    numbers.n,
    CONCAT('사용자_', LPAD(numbers.n, 6, '0'))
FROM (
    SELECT 1 + ones.n + 10 * tens.n + 100 * hundreds.n + 1000 * thousands.n + 10000 * ten_thousands.n AS n
    FROM (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) ones,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) tens,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) hundreds,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) thousands,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) ten_thousands
    WHERE 1 + ones.n + 10 * tens.n + 100 * hundreds.n + 1000 * thousands.n + 10000 * ten_thousands.n <= 100000
) numbers;

-- 2. 대용량 상품 데이터 (50,000개)
INSERT INTO product (product_id, name, price, status, created_at, updated_at)
SELECT 
    numbers.n,
    CONCAT('상품_', LPAD(numbers.n, 5, '0'), '_', 
           CASE 
               WHEN numbers.n % 10 = 0 THEN 'iPhone'
               WHEN numbers.n % 10 = 1 THEN 'MacBook'
               WHEN numbers.n % 10 = 2 THEN 'iPad'
               WHEN numbers.n % 10 = 3 THEN 'Apple Watch'
               WHEN numbers.n % 10 = 4 THEN 'AirPods'
               WHEN numbers.n % 10 = 5 THEN 'Samsung'
               WHEN numbers.n % 10 = 6 THEN 'LG'
               WHEN numbers.n % 10 = 7 THEN 'Nike'
               WHEN numbers.n % 10 = 8 THEN 'Adidas'
               ELSE 'Sony'
           END),
    CASE 
        WHEN numbers.n % 10 = 0 THEN 1500000 + (numbers.n % 100000)
        WHEN numbers.n % 10 = 1 THEN 1800000 + (numbers.n % 200000)
        WHEN numbers.n % 10 = 2 THEN 800000 + (numbers.n % 100000)
        WHEN numbers.n % 10 = 3 THEN 500000 + (numbers.n % 50000)
        WHEN numbers.n % 10 = 4 THEN 300000 + (numbers.n % 30000)
        WHEN numbers.n % 10 = 5 THEN 1200000 + (numbers.n % 150000)
        WHEN numbers.n % 10 = 6 THEN 2500000 + (numbers.n % 300000)
        WHEN numbers.n % 10 = 7 THEN 150000 + (numbers.n % 20000)
        WHEN numbers.n % 10 = 8 THEN 80000 + (numbers.n % 10000)
        ELSE 400000 + (numbers.n % 50000)
    END,
    CASE 
        WHEN numbers.n % 100 = 0 THEN 'HOLD'
        WHEN numbers.n % 100 = 1 THEN 'STOP_SELLING'
        ELSE 'SELLING'
    END,
    DATE_SUB(NOW(), INTERVAL (numbers.n % 365) DAY),
    NOW()
FROM (
    SELECT 21 + ones.n + 10 * tens.n + 100 * hundreds.n + 1000 * thousands.n + 10000 * ten_thousands.n AS n
    FROM (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) ones,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) tens,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) hundreds,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) thousands,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) ten_thousands
    WHERE 21 + ones.n + 10 * tens.n + 100 * hundreds.n + 1000 * thousands.n + 10000 * ten_thousands.n <= 50020
) numbers;

-- 3. 대용량 재고 데이터
INSERT INTO stock (stock_id, product_id, quantity)
SELECT 
    numbers.n,
    numbers.n,
    CASE 
        WHEN numbers.n % 10 = 0 THEN 1000 + (numbers.n % 5000)
        WHEN numbers.n % 10 = 1 THEN 500 + (numbers.n % 2000)
        WHEN numbers.n % 10 = 2 THEN 2000 + (numbers.n % 8000)
        WHEN numbers.n % 10 = 3 THEN 800 + (numbers.n % 3000)
        WHEN numbers.n % 10 = 4 THEN 3000 + (numbers.n % 10000)
        WHEN numbers.n % 10 = 5 THEN 600 + (numbers.n % 2500)
        WHEN numbers.n % 10 = 6 THEN 1500 + (numbers.n % 6000)
        WHEN numbers.n % 10 = 7 THEN 400 + (numbers.n % 1500)
        WHEN numbers.n % 10 = 8 THEN 2500 + (numbers.n % 12000)
        ELSE 1200 + (numbers.n % 4000)
    END
FROM (
    SELECT 21 + ones.n + 10 * tens.n + 100 * hundreds.n + 1000 * thousands.n + 10000 * ten_thousands.n AS n
    FROM (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) ones,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) tens,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) hundreds,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) thousands,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) ten_thousands
    WHERE 21 + ones.n + 10 * tens.n + 100 * hundreds.n + 1000 * thousands.n + 10000 * ten_thousands.n <= 50020
) numbers;

-- 4. 대용량 쿠폰 데이터 (1,000개)
INSERT INTO coupon (coupon_id, name, discount_rate, quantity, status, expired_at)
SELECT 
    numbers.n,
    CONCAT('쿠폰_', LPAD(numbers.n, 4, '0'), '_', 
           CASE 
               WHEN numbers.n % 5 = 0 THEN '신규가입'
               WHEN numbers.n % 5 = 1 THEN '생일축하'
               WHEN numbers.n % 5 = 2 THEN '첫구매'
               WHEN numbers.n % 5 = 3 THEN 'VIP'
               ELSE '시즌할인'
           END),
    CASE 
        WHEN numbers.n % 10 = 0 THEN 0.1
        WHEN numbers.n % 10 = 1 THEN 0.15
        WHEN numbers.n % 10 = 2 THEN 0.2
        WHEN numbers.n % 10 = 3 THEN 0.25
        WHEN numbers.n % 10 = 4 THEN 0.05
        WHEN numbers.n % 10 = 5 THEN 0.08
        WHEN numbers.n % 10 = 6 THEN 0.12
        WHEN numbers.n % 10 = 7 THEN 0.18
        WHEN numbers.n % 10 = 8 THEN 0.22
        ELSE 0.3
    END,
    CASE 
        WHEN numbers.n % 10 = 0 THEN 1000 + (numbers.n % 5000)
        WHEN numbers.n % 10 = 1 THEN 500 + (numbers.n % 2000)
        WHEN numbers.n % 10 = 2 THEN 2000 + (numbers.n % 8000)
        WHEN numbers.n % 10 = 3 THEN 800 + (numbers.n % 3000)
        WHEN numbers.n % 10 = 4 THEN 3000 + (numbers.n % 10000)
        WHEN numbers.n % 10 = 5 THEN 600 + (numbers.n % 2500)
        WHEN numbers.n % 10 = 6 THEN 1500 + (numbers.n % 6000)
        WHEN numbers.n % 10 = 7 THEN 400 + (numbers.n % 1500)
        WHEN numbers.n % 10 = 8 THEN 2500 + (numbers.n % 12000)
        ELSE 1200 + (numbers.n % 4000)
    END,
    CASE 
        WHEN numbers.n % 100 = 0 THEN 'CANCELED'
        WHEN numbers.n % 100 = 1 THEN 'REGISTERED'
        ELSE 'PUBLISHABLE'
    END,
    DATE_ADD(NOW(), INTERVAL (numbers.n % 365) DAY)
FROM (
    SELECT 13 + ones.n + 10 * tens.n + 100 * hundreds.n AS n
    FROM (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) ones,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) tens,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) hundreds
    WHERE 13 + ones.n + 10 * tens.n + 100 * hundreds.n <= 1012
) numbers;

-- 5. 대용량 사용자 쿠폰 데이터 (500,000개)
INSERT INTO user_coupon (user_coupon_id, user_id, coupon_id, used_status, issued_at, used_at)
SELECT 
    numbers.n,
    (numbers.n % 100000) + 1,
    (numbers.n % 1000) + 1,
    CASE 
        WHEN numbers.n % 10 = 0 THEN 'USED'
        ELSE 'UNUSED'
    END,
    DATE_SUB(NOW(), INTERVAL (numbers.n % 365) DAY),
    CASE 
        WHEN numbers.n % 10 = 0 THEN DATE_SUB(NOW(), INTERVAL (numbers.n % 30) DAY)
        ELSE NULL
    END
FROM (
    SELECT 29 + ones.n + 10 * tens.n + 100 * hundreds.n + 1000 * thousands.n + 10000 * ten_thousands.n + 100000 * hundred_thousands.n AS n
    FROM (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) ones,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) tens,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) hundreds,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) thousands,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) ten_thousands,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) hundred_thousands
    WHERE 29 + ones.n + 10 * tens.n + 100 * hundreds.n + 1000 * thousands.n + 10000 * ten_thousands.n + 100000 * hundred_thousands.n <= 500029
) numbers;

-- 6. 대용량 잔액 데이터 (100,000개)
INSERT INTO balance (id, user_id, balance)
SELECT 
    numbers.n,
    numbers.n,
    CASE 
        WHEN numbers.n % 10 = 0 THEN 100000 + (numbers.n % 900000)
        WHEN numbers.n % 10 = 1 THEN 50000 + (numbers.n % 450000)
        WHEN numbers.n % 10 = 2 THEN 200000 + (numbers.n % 1800000)
        WHEN numbers.n % 10 = 3 THEN 75000 + (numbers.n % 675000)
        WHEN numbers.n % 10 = 4 THEN 300000 + (numbers.n % 2700000)
        WHEN numbers.n % 10 = 5 THEN 60000 + (numbers.n % 540000)
        WHEN numbers.n % 10 = 6 THEN 150000 + (numbers.n % 1350000)
        WHEN numbers.n % 10 = 7 THEN 80000 + (numbers.n % 720000)
        WHEN numbers.n % 10 = 8 THEN 250000 + (numbers.n % 2250000)
        ELSE 120000 + (numbers.n % 1080000)
    END
FROM (
    SELECT 21 + ones.n + 10 * tens.n + 100 * hundreds.n + 1000 * thousands.n + 10000 * ten_thousands.n AS n
    FROM (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) ones,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) tens,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) hundreds,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) thousands,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) ten_thousands
    WHERE 21 + ones.n + 10 * tens.n + 100 * hundreds.n + 1000 * thousands.n + 10000 * ten_thousands.n <= 100020
) numbers;

-- 7. 대용량 잔액 거래 내역 데이터 (1,000,000개)
INSERT INTO balance_transaction (id, balance_id, transaction_type, amount)
SELECT 
    numbers.n,
    (numbers.n % 100000) + 1,
    CASE 
        WHEN numbers.n % 2 = 0 THEN 'CHARGE'
        ELSE 'USE'
    END,
    CASE 
        WHEN numbers.n % 2 = 0 THEN 10000 + (numbers.n % 90000)
        ELSE 1000 + (numbers.n % 9000)
    END
FROM (
    SELECT 29 + ones.n + 10 * tens.n + 100 * hundreds.n + 1000 * thousands.n + 10000 * ten_thousands.n + 100000 * hundred_thousands.n AS n
    FROM (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) ones,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) tens,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) hundreds,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) thousands,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) ten_thousands,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) hundred_thousands
    WHERE 29 + ones.n + 10 * tens.n + 100 * hundreds.n + 1000 * thousands.n + 10000 * ten_thousands.n + 100000 * hundred_thousands.n <= 1000029
) numbers;

-- 8. 대용량 주문 데이터 (200,000개)
INSERT INTO orders (order_id, user_id, user_coupon_id, order_status, total_price, discount_price)
SELECT 
    numbers.n,
    (numbers.n % 100000) + 1,
    CASE 
        WHEN numbers.n % 5 = 0 THEN NULL
        ELSE (numbers.n % 500000) + 1
    END,
    CASE 
        WHEN numbers.n % 10 = 0 THEN 'CREATED'
        ELSE 'PAID'
    END,
    CASE 
        WHEN numbers.n % 10 = 0 THEN 50000 + (numbers.n % 450000)
        WHEN numbers.n % 10 = 1 THEN 100000 + (numbers.n % 900000)
        WHEN numbers.n % 10 = 2 THEN 150000 + (numbers.n % 1350000)
        WHEN numbers.n % 10 = 3 THEN 200000 + (numbers.n % 1800000)
        WHEN numbers.n % 10 = 4 THEN 250000 + (numbers.n % 2250000)
        WHEN numbers.n % 10 = 5 THEN 300000 + (numbers.n % 2700000)
        WHEN numbers.n % 10 = 6 THEN 350000 + (numbers.n % 3150000)
        WHEN numbers.n % 10 = 7 THEN 400000 + (numbers.n % 3600000)
        WHEN numbers.n % 10 = 8 THEN 450000 + (numbers.n % 4050000)
        ELSE 500000 + (numbers.n % 4500000)
    END,
    CASE 
        WHEN numbers.n % 5 = 0 THEN 0
        ELSE (numbers.n % 50000)
    END
FROM (
    SELECT 16 + ones.n + 10 * tens.n + 100 * hundreds.n + 1000 * thousands.n + 10000 * ten_thousands.n + 100000 * hundred_thousands.n AS n
    FROM (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) ones,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) tens,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) hundreds,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) thousands,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) ten_thousands,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2) hundred_thousands
    WHERE 16 + ones.n + 10 * tens.n + 100 * hundreds.n + 1000 * thousands.n + 10000 * ten_thousands.n + 100000 * hundred_thousands.n <= 200016
) numbers;

-- 9. 대용량 주문 상품 데이터 (600,000개)
INSERT INTO order_product (order_product_id, order_id, product_id, product_name, unit_price, quantity)
SELECT 
    numbers.n,
    (numbers.n % 200000) + 1,
    (numbers.n % 50000) + 1,
    CONCAT('상품_', LPAD((numbers.n % 50000) + 1, 5, '0')),
    CASE 
        WHEN numbers.n % 10 = 0 THEN 1500000 + (numbers.n % 100000)
        WHEN numbers.n % 10 = 1 THEN 1800000 + (numbers.n % 200000)
        WHEN numbers.n % 10 = 2 THEN 800000 + (numbers.n % 100000)
        WHEN numbers.n % 10 = 3 THEN 500000 + (numbers.n % 50000)
        WHEN numbers.n % 10 = 4 THEN 300000 + (numbers.n % 30000)
        WHEN numbers.n % 10 = 5 THEN 1200000 + (numbers.n % 150000)
        WHEN numbers.n % 10 = 6 THEN 2500000 + (numbers.n % 300000)
        WHEN numbers.n % 10 = 7 THEN 150000 + (numbers.n % 20000)
        WHEN numbers.n % 10 = 8 THEN 80000 + (numbers.n % 10000)
        ELSE 400000 + (numbers.n % 50000)
    END,
    CASE 
        WHEN numbers.n % 10 = 0 THEN 1
        WHEN numbers.n % 10 = 1 THEN 2
        WHEN numbers.n % 10 = 2 THEN 3
        WHEN numbers.n % 10 = 3 THEN 1
        WHEN numbers.n % 10 = 4 THEN 2
        WHEN numbers.n % 10 = 5 THEN 1
        WHEN numbers.n % 10 = 6 THEN 3
        WHEN numbers.n % 10 = 7 THEN 1
        WHEN numbers.n % 10 = 8 THEN 2
        ELSE 1
    END
FROM (
    SELECT 26 + ones.n + 10 * tens.n + 100 * hundreds.n + 1000 * thousands.n + 10000 * ten_thousands.n + 100000 * hundred_thousands.n AS n
    FROM (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) ones,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) tens,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) hundreds,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) thousands,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) ten_thousands,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) hundred_thousands
    WHERE 26 + ones.n + 10 * tens.n + 100 * hundreds.n + 1000 * thousands.n + 10000 * ten_thousands.n + 100000 * hundred_thousands.n <= 600026
) numbers;

-- 10. 대용량 결제 데이터 (200,000개)
INSERT INTO payment (payment_id, order_id, amount, payment_method, payment_status, paid_at)
SELECT 
    numbers.n,
    (numbers.n % 200000) + 1,
    CASE 
        WHEN numbers.n % 10 = 0 THEN 50000 + (numbers.n % 450000)
        WHEN numbers.n % 10 = 1 THEN 100000 + (numbers.n % 900000)
        WHEN numbers.n % 10 = 2 THEN 150000 + (numbers.n % 1350000)
        WHEN numbers.n % 10 = 3 THEN 200000 + (numbers.n % 1800000)
        WHEN numbers.n % 10 = 4 THEN 250000 + (numbers.n % 2250000)
        WHEN numbers.n % 10 = 5 THEN 300000 + (numbers.n % 2700000)
        WHEN numbers.n % 10 = 6 THEN 350000 + (numbers.n % 3150000)
        WHEN numbers.n % 10 = 7 THEN 400000 + (numbers.n % 3600000)
        WHEN numbers.n % 10 = 8 THEN 450000 + (numbers.n % 4050000)
        ELSE 500000 + (numbers.n % 4500000)
    END,
    CASE 
        WHEN numbers.n % 4 = 0 THEN 'CARD'
        WHEN numbers.n % 4 = 1 THEN 'CASH'
        WHEN numbers.n % 4 = 2 THEN 'VIRTUAL_ACCOUNT'
        ELSE 'UNKNOWN'
    END,
    CASE 
        WHEN numbers.n % 10 = 0 THEN 'READY'
        WHEN numbers.n % 10 = 1 THEN 'WAITING'
        WHEN numbers.n % 10 = 2 THEN 'FAILED'
        WHEN numbers.n % 10 = 3 THEN 'CANCELLED'
        ELSE 'COMPLETED'
    END,
    CASE 
        WHEN numbers.n % 10 IN (0, 1, 2, 3) THEN NULL
        ELSE DATE_SUB(NOW(), INTERVAL (numbers.n % 365) DAY)
    END
FROM (
    SELECT 16 + ones.n + 10 * tens.n + 100 * hundreds.n + 1000 * thousands.n + 10000 * ten_thousands.n + 100000 * hundred_thousands.n AS n
    FROM (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) ones,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) tens,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) hundreds,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) thousands,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) ten_thousands,
         (SELECT 0 n UNION SELECT 1 UNION SELECT 2) hundred_thousands
    WHERE 16 + ones.n + 10 * tens.n + 100 * hundreds.n + 1000 * thousands.n + 10000 * ten_thousands.n + 100000 * hundred_thousands.n <= 200016
) numbers;

-- 11. 인덱스 생성 (쿼리 튜닝을 위한)
CREATE INDEX IF NOT EXISTS idx_user_username ON user(username);
CREATE INDEX IF NOT EXISTS idx_product_status ON product(status);
CREATE INDEX IF NOT EXISTS idx_product_price ON product(price);
CREATE INDEX IF NOT EXISTS idx_stock_product_id ON stock(product_id);
CREATE INDEX IF NOT EXISTS idx_stock_quantity ON stock(quantity);
CREATE INDEX IF NOT EXISTS idx_coupon_status ON coupon(status);
CREATE INDEX IF NOT EXISTS idx_coupon_expired_at ON coupon(expired_at);
CREATE INDEX IF NOT EXISTS idx_user_coupon_user_id ON user_coupon(user_id);
CREATE INDEX IF NOT EXISTS idx_user_coupon_used_status ON user_coupon(used_status);
CREATE INDEX IF NOT EXISTS idx_balance_user_id ON balance(user_id);
CREATE INDEX IF NOT EXISTS idx_balance_transaction_balance_id ON balance_transaction(balance_id);
CREATE INDEX IF NOT EXISTS idx_balance_transaction_type ON balance_transaction(transaction_type);
CREATE INDEX IF NOT EXISTS idx_orders_user_id ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(order_status);
CREATE INDEX IF NOT EXISTS idx_order_product_order_id ON order_product(order_id);
CREATE INDEX IF NOT EXISTS idx_order_product_product_id ON order_product(product_id);
CREATE INDEX IF NOT EXISTS idx_payment_order_id ON payment(order_id);
CREATE INDEX IF NOT EXISTS idx_payment_status ON payment(payment_status);
CREATE INDEX IF NOT EXISTS idx_payment_method ON payment(payment_method);

-- 12. 복합 인덱스 (성능 최적화)
CREATE INDEX IF NOT EXISTS idx_product_status_price ON product(status, price);
CREATE INDEX IF NOT EXISTS idx_user_coupon_user_status ON user_coupon(user_id, used_status);
CREATE INDEX IF NOT EXISTS idx_orders_user_status ON orders(user_id, order_status);
CREATE INDEX IF NOT EXISTS idx_payment_order_status ON payment(order_id, payment_status);

-- 13. 통계 정보 업데이트
ANALYZE TABLE user, product, stock, coupon, user_coupon, balance, balance_transaction, orders, order_product, payment;

-- 14. 성능 테스트용 뷰 생성
CREATE OR REPLACE VIEW v_user_order_summary AS
SELECT 
    u.user_id,
    u.username,
    COUNT(o.order_id) as total_orders,
    SUM(o.total_price) as total_spent,
    AVG(o.total_price) as avg_order_value,
    MAX(o.created_at) as last_order_date
FROM user u
LEFT JOIN orders o ON u.user_id = o.user_id
GROUP BY u.user_id, u.username;

CREATE OR REPLACE VIEW v_product_sales_summary AS
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
GROUP BY p.product_id, p.name, p.price, p.status, s.quantity;

-- 15. 성능 테스트용 저장 프로시저
DELIMITER //
CREATE PROCEDURE GetUserOrderHistory(IN user_id_param BIGINT)
BEGIN
    SELECT 
        o.order_id,
        o.order_status,
        o.total_price,
        o.discount_price,
        COUNT(op.order_product_id) as item_count,
        o.created_at
    FROM orders o
    LEFT JOIN order_product op ON o.order_id = op.order_id
    WHERE o.user_id = user_id_param
    GROUP BY o.order_id, o.order_status, o.total_price, o.discount_price, o.created_at
    ORDER BY o.created_at DESC
    LIMIT 50;
END //
DELIMITER ;

-- 16. 데이터 통계 출력
SELECT '데이터 생성 완료' as status;
SELECT COUNT(*) as user_count FROM user;
SELECT COUNT(*) as product_count FROM product;
SELECT COUNT(*) as order_count FROM orders;
SELECT COUNT(*) as payment_count FROM payment;
SELECT COUNT(*) as user_coupon_count FROM user_coupon;
SELECT COUNT(*) as balance_transaction_count FROM balance_transaction; 