-- 인덱스 분석 및 최적화 스크립트
-- 현재 인덱스 현황을 확인하고 불필요한 인덱스를 제거합니다.

USE hhplus;

-- 1. 현재 인덱스 현황 확인
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

-- 2. 테이블별 인덱스 개수 확인
SELECT 
    table_name,
    COUNT(DISTINCT index_name) as index_count,
    SUM(CASE WHEN index_name = 'PRIMARY' THEN 1 ELSE 0 END) as primary_keys,
    SUM(CASE WHEN index_name != 'PRIMARY' THEN 1 ELSE 0 END) as secondary_indexes
FROM information_schema.statistics 
WHERE table_schema = 'hhplus'
GROUP BY table_name
ORDER BY index_count DESC;

-- 3. 인덱스 크기 확인
SELECT 
    table_name,
    index_name,
    ROUND(SUM(index_length) / 1024 / 1024, 2) as index_size_mb
FROM information_schema.tables t
JOIN information_schema.statistics s ON t.table_name = s.table_name
WHERE t.table_schema = 'hhplus' AND s.table_schema = 'hhplus'
GROUP BY table_name, index_name
ORDER BY index_size_mb DESC;

-- 4. 불필요한 인덱스 제거 (성능 테스트 전에 정리)
-- 주의: 실제 운영 환경에서는 신중하게 제거해야 합니다.

-- 4-1. 중복되거나 사용되지 않는 인덱스 제거
-- 예시: 단일 컬럼 인덱스가 복합 인덱스의 첫 번째 컬럼과 중복되는 경우

-- 4-2. 사용 빈도가 낮은 인덱스 제거
-- 실제 쿼리 패턴을 분석한 후 결정

-- 5. 기본 인덱스만 남기고 나머지 제거 (테스트용)
-- 실제 운영에서는 이 부분을 주석 처리하고 필요한 인덱스만 선택적으로 제거

-- Primary Key는 유지하고 Secondary Index만 제거
-- (실제로는 쿼리 패턴 분석 후 결정)

-- 6. 인덱스 제거 후 테이블 통계 업데이트
ANALYZE TABLE user, product, stock, coupon, user_coupon, balance, balance_transaction, orders, order_product, payment;

-- 7. 인덱스 제거 후 현황 확인
SELECT 
    table_name,
    COUNT(DISTINCT index_name) as remaining_indexes
FROM information_schema.statistics 
WHERE table_schema = 'hhplus'
GROUP BY table_name
ORDER BY table_name;

-- 8. 테이블 크기 변화 확인
SELECT 
    table_name,
    ROUND(data_length / 1024 / 1024, 2) as data_size_mb,
    ROUND(index_length / 1024 / 1024, 2) as index_size_mb,
    ROUND((data_length + index_length) / 1024 / 1024, 2) as total_size_mb
FROM information_schema.tables 
WHERE table_schema = 'hhplus'
ORDER BY total_size_mb DESC; 