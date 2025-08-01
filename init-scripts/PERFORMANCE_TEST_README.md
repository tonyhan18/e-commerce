# 쿼리 튜닝 성능 테스트 가이드

이 문서는 대용량 데이터를 활용한 쿼리 튜닝 및 성능 테스트 방법을 설명합니다.

## 📊 생성된 데이터 현황

### 대용량 데이터 스크립트 (`02-performance-data.sql`)
- **사용자**: 100,000명
- **상품**: 50,000개
- **쿠폰**: 1,000개
- **사용자 쿠폰**: 500,000개
- **잔액**: 100,000개
- **잔액 거래**: 1,000,000개
- **주문**: 200,000개
- **주문 상품**: 600,000개
- **결제**: 200,000개

**총 레코드 수**: 약 2,750,000개

## 🚀 성능 테스트 실행 방법

### 1. 대용량 데이터 생성
```bash
# MySQL 접속
docker exec -it e-commerce-mysql mysql -u application -papplication hhplus

# 대용량 데이터 생성
source /docker-entrypoint-initdb.d/02-performance-data.sql
```

### 2. 성능 테스트 쿼리 실행
```bash
# 성능 테스트 쿼리 실행
source /docker-entrypoint-initdb.d/03-query-tuning-tests.sql
```

## 📈 쿼리 튜닝 테스트 시나리오

### 1. JOIN 성능 테스트
```sql
-- 사용자별 주문 통계 (LEFT JOIN)
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
```

### 2. 복합 인덱스 성능 테스트
```sql
-- 상품별 판매 통계 (복합 JOIN)
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
ORDER BY total_revenue DESC;
```

### 3. 날짜 기반 집계 성능 테스트
```sql
-- 월별 매출 통계
SELECT 
    YEAR(o.created_at) as year,
    MONTH(o.created_at) as month,
    COUNT(o.order_id) as total_orders,
    SUM(o.total_price) as total_revenue
FROM orders o
WHERE o.order_status = 'PAID'
    AND o.created_at >= DATE_SUB(NOW(), INTERVAL 12 MONTH)
GROUP BY YEAR(o.created_at), MONTH(o.created_at)
ORDER BY year DESC, month DESC;
```

### 4. 윈도우 함수 성능 테스트
```sql
-- 잔액 거래 내역 분석
SELECT 
    bt.balance_id,
    bt.transaction_type,
    bt.amount,
    SUM(CASE WHEN bt.transaction_type = 'CHARGE' THEN bt.amount ELSE -bt.amount END) 
        OVER (PARTITION BY bt.balance_id ORDER BY bt.id) as running_balance
FROM balance_transaction bt
WHERE bt.balance_id BETWEEN 1 AND 100
ORDER BY bt.balance_id, bt.id;
```

## 🔍 성능 분석 도구

### 1. EXPLAIN 분석
```sql
-- 쿼리 실행 계획 확인
EXPLAIN FORMAT=JSON
SELECT 
    u.user_id,
    u.username,
    COUNT(o.order_id) as total_orders
FROM user u
LEFT JOIN orders o ON u.user_id = o.user_id
WHERE u.user_id BETWEEN 1 AND 1000
GROUP BY u.user_id, u.username;
```

### 2. 실행 시간 측정
```sql
-- 쿼리 실행 시간 측정
SET profiling = 1;
-- 쿼리 실행
SHOW PROFILES;
SHOW PROFILE FOR QUERY 1;
```

### 3. 인덱스 사용 현황 확인
```sql
-- 인덱스 사용 현황
SELECT 
    table_name,
    index_name,
    column_name,
    cardinality
FROM information_schema.statistics 
WHERE table_schema = 'hhplus'
ORDER BY table_name, index_name;
```

## 📊 성능 최적화 팁

### 1. 인덱스 최적화
- **단일 컬럼 인덱스**: 자주 조회되는 컬럼
- **복합 인덱스**: WHERE 조건에서 자주 사용되는 조합
- **커버링 인덱스**: SELECT 컬럼을 모두 포함하는 인덱스

### 2. 쿼리 최적화
- **불필요한 JOIN 제거**: 필요한 테이블만 조인
- **서브쿼리 최적화**: EXISTS vs IN vs JOIN
- **집계 함수 최적화**: GROUP BY 컬럼 순서 고려

### 3. 데이터 타입 최적화
- **적절한 데이터 타입**: VARCHAR 길이 최적화
- **NULL 처리**: NOT NULL 제약 조건 활용
- **인덱스 크기**: 불필요한 컬럼 제외

## 🎯 튜닝 체크리스트

### 쿼리 튜닝 전
- [ ] EXPLAIN으로 실행 계획 확인
- [ ] 인덱스 사용 현황 점검
- [ ] 테이블 통계 정보 업데이트
- [ ] 쿼리 실행 시간 측정

### 쿼리 튜닝 후
- [ ] 인덱스 추가/수정 효과 확인
- [ ] 쿼리 구조 개선 효과 측정
- [ ] 전체 시스템 성능 영향 평가
- [ ] 다른 쿼리에 미치는 영향 확인

## 📝 성능 테스트 결과 기록

### 테스트 환경
- **MySQL 버전**: 8.0.36
- **데이터 크기**: 약 2.7M 레코드
- **시스템 사양**: [기록 필요]

### 테스트 결과 예시
```
테스트 쿼리: 사용자별 주문 통계
- 튜닝 전: 2.5초
- 튜닝 후: 0.3초
- 개선율: 88%
- 적용된 최적화: 복합 인덱스 추가
```

## 🔧 고급 튜닝 기법

### 1. 파티셔닝
```sql
-- 날짜별 파티셔닝 예시
ALTER TABLE orders PARTITION BY RANGE (YEAR(created_at)) (
    PARTITION p2023 VALUES LESS THAN (2024),
    PARTITION p2024 VALUES LESS THAN (2025),
    PARTITION p2025 VALUES LESS THAN (2026)
);
```

### 2. 뷰 최적화
```sql
-- 자주 사용되는 복잡한 쿼리를 뷰로 생성
CREATE OR REPLACE VIEW v_user_order_summary AS
SELECT 
    u.user_id,
    u.username,
    COUNT(o.order_id) as total_orders,
    SUM(o.total_price) as total_spent
FROM user u
LEFT JOIN orders o ON u.user_id = o.user_id
GROUP BY u.user_id, u.username;
```

### 3. 저장 프로시저 활용
```sql
-- 성능 테스트용 저장 프로시저
CALL PerformanceTest();
```

## ⚠️ 주의사항

1. **테스트 환경에서만 실행**: 대용량 데이터 생성은 개발/테스트 환경에서만 실행
2. **백업 필수**: 중요한 데이터는 반드시 백업 후 테스트
3. **시스템 리소스 모니터링**: CPU, 메모리, 디스크 사용량 확인
4. **점진적 테스트**: 작은 데이터부터 시작하여 점진적으로 확대

## 📚 참고 자료

- [MySQL Performance Tuning](https://dev.mysql.com/doc/refman/8.0/en/optimization.html)
- [MySQL Index Optimization](https://dev.mysql.com/doc/refman/8.0/en/mysql-indexes.html)
- [MySQL Query Optimization](https://dev.mysql.com/doc/refman/8.0/en/query-optimization.html) 