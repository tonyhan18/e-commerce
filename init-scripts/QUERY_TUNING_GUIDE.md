# 쿼리 튜닝 테스트 실행 가이드

이 가이드는 실제 쿼리 성능을 테스트하고 필요한 인덱스만 추가하는 방법을 단계별로 설명합니다.

## 🎯 목표
- 실제 쿼리 성능 측정
- 불필요한 인덱스 제거
- 필요한 인덱스만 추가
- 성능 개선 효과 검증

## 📋 실행 순서

### 1단계: 현재 인덱스 현황 분석
```bash
# MySQL 접속
docker exec -it e-commerce-mysql mysql -u application -papplication hhplus

# 현재 인덱스 현황 확인
source /docker-entrypoint-initdb.d/04-index-analysis.sql
```

**확인 사항:**
- 각 테이블의 인덱스 개수
- 인덱스 크기 및 사용률
- 중복되거나 불필요한 인덱스 식별

### 2단계: 실제 쿼리 성능 테스트
```bash
# 성능 테스트 실행
source /docker-entrypoint-initdb.d/05-query-performance-test.sql
```

**테스트 쿼리:**
1. 사용자별 주문 통계 (가장 빈번한 쿼리)
2. 상품별 판매 통계 (복잡한 JOIN)
3. 결제 상태별 통계 (집계 쿼리)
4. 쿠폰 사용 현황 (서브쿼리)
5. 재고 부족 상품 조회 (조건부 쿼리)
6. 사용자별 쿠폰 사용 패턴 (복합 조건)
7. 잔액 거래 내역 분석 (윈도우 함수)

**분석 포인트:**
- 각 쿼리의 실행 시간
- EXPLAIN 결과 분석
- 인덱스 사용 현황
- 느린 쿼리 식별

### 3단계: 인덱스 최적화
```bash
# 필요한 인덱스만 추가
source /docker-entrypoint-initdb.d/06-optimized-indexes.sql
```

**최적화 원칙:**
1. **실제 사용되는 쿼리에만 인덱스 추가**
2. **복합 인덱스 우선 사용**
3. **커버링 인덱스 활용**
4. **불필요한 인덱스 제거**

## 📊 성능 측정 방법

### 1. 쿼리 실행 시간 측정
```sql
-- 성능 측정 설정
SET profiling = 1;
SET profiling_history_size = 100;

-- 쿼리 실행
SELECT ... FROM ... WHERE ...;

-- 실행 시간 확인
SHOW PROFILES;
SHOW PROFILE FOR QUERY 1;
```

### 2. EXPLAIN 분석
```sql
-- 실행 계획 확인
EXPLAIN FORMAT=JSON
SELECT ... FROM ... WHERE ...;
```

**중요한 지표:**
- `type`: index, range, ref, all 등
- `rows`: 스캔할 행 수
- `key`: 사용할 인덱스
- `Extra`: 추가 정보 (Using index, Using where 등)

### 3. 인덱스 사용 현황
```sql
-- 인덱스 사용률 확인
SELECT 
    table_name,
    index_name,
    column_name,
    cardinality
FROM information_schema.statistics 
WHERE table_schema = 'hhplus';
```

## 🎯 최적화 체크리스트

### 튜닝 전
- [ ] 현재 인덱스 현황 파악
- [ ] 실제 쿼리 패턴 분석
- [ ] 성능 측정 기준 설정
- [ ] 느린 쿼리 식별

### 튜닝 중
- [ ] EXPLAIN으로 실행 계획 분석
- [ ] 인덱스 사용률 확인
- [ ] 복합 인덱스 고려
- [ ] 커버링 인덱스 적용

### 튜닝 후
- [ ] 성능 개선 효과 측정
- [ ] 인덱스 크기 변화 확인
- [ ] 다른 쿼리에 미치는 영향 평가
- [ ] 전체 시스템 성능 검증

## 📈 성능 개선 예상 효과

### 1. 사용자별 주문 통계 쿼리
- **튜닝 전**: 2.5초 (전체 테이블 스캔)
- **튜닝 후**: 0.3초 (인덱스 스캔)
- **개선율**: 88%

### 2. 상품별 판매 통계 쿼리
- **튜닝 전**: 1.8초 (복잡한 JOIN)
- **튜닝 후**: 0.4초 (최적화된 인덱스)
- **개선율**: 78%

### 3. 결제 상태별 통계 쿼리
- **튜닝 전**: 1.2초 (집계 연산)
- **튜닝 후**: 0.2초 (커버링 인덱스)
- **개선율**: 83%

## 🔧 인덱스 최적화 팁

### 1. 복합 인덱스 설계
```sql
-- 잘못된 예: 단일 인덱스 여러 개
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(order_status);

-- 올바른 예: 복합 인덱스
CREATE INDEX idx_orders_user_status ON orders(user_id, order_status);
```

### 2. 커버링 인덱스 활용
```sql
-- SELECT 컬럼을 모두 포함하는 인덱스
CREATE INDEX idx_orders_covering ON orders(user_id, total_price, order_status);
```

### 3. 인덱스 순서 최적화
```sql
-- WHERE 조건 순서에 맞춰 인덱스 설계
-- WHERE user_id = ? AND status = ? ORDER BY total_price DESC
CREATE INDEX idx_orders_user_status_price ON orders(user_id, status, total_price DESC);
```

## ⚠️ 주의사항

### 1. 인덱스 제거 시 주의
- 실제 운영 환경에서는 신중하게 제거
- 사용 빈도가 낮은 인덱스부터 제거
- 다른 쿼리에 미치는 영향 확인

### 2. 인덱스 추가 시 주의
- 너무 많은 인덱스는 INSERT/UPDATE 성능 저하
- 인덱스 크기와 메모리 사용량 고려
- 정기적인 인덱스 사용률 모니터링

### 3. 성능 테스트 시 주의
- 테스트 환경에서만 실행
- 충분한 데이터로 테스트
- 다양한 쿼리 패턴으로 검증

## 📝 결과 기록 템플릿

### 테스트 환경
- **MySQL 버전**: 8.0.36
- **데이터 크기**: [레코드 수]
- **시스템 사양**: [CPU, 메모리, 디스크]

### 테스트 결과
```
쿼리: [쿼리명]
- 튜닝 전: [실행시간]
- 튜닝 후: [실행시간]
- 개선율: [%]
- 적용된 최적화: [인덱스명]
```

### 인덱스 현황
```
테이블: [테이블명]
- 인덱스 개수: [개수]
- 인덱스 크기: [MB]
- 데이터 대비 비율: [%]
```

## 🚀 다음 단계

1. **정기적인 성능 모니터링**
2. **쿼리 패턴 변화에 따른 인덱스 조정**
3. **새로운 비즈니스 요구사항 반영**
4. **성능 최적화 지속 개선** 