# 동시성 테스트 가이드

## 📋 개요

BalanceFacade와 UserCouponFacade에 대한 동시성 테스트를 작성했습니다. 이 테스트들은 실제 운영 환경에서 발생할 수 있는 동시성 이슈를 사전에 발견하고 해결하는 데 도움이 됩니다.

## 🎯 테스트 대상

### 1. BalanceFacade 동시성 테스트
- **동시 잔액 충전**: 여러 스레드가 동시에 같은 사용자의 잔액을 충전할 때 정합성 보장
- **동시 잔액 사용**: 잔액 부족 상황에서의 동시성 제어
- **트랜잭션 격리**: 읽기/쓰기 동시 실행 시 데이터 일관성

### 2. UserCouponFacade 동시성 테스트
- **중복 쿠폰 발급 방지**: 동일 사용자에게 같은 쿠폰이 중복 발급되지 않도록 제어
- **쿠폰 사용 정합성**: 쿠폰이 한 번만 사용되도록 보장
- **다중 사용자 환경**: 서로 다른 사용자들의 동시 쿠폰 발급

## 📊 테스트 시나리오

### BalanceFacade 테스트 시나리오

#### 1. 동시 잔액 충전 테스트
```java
@Test
@DisplayName("동시 잔액 충전 시 잔액 정합성 테스트")
void concurrentChargeBalanceTest()
```
- **목적**: 10개 스레드가 각각 100번씩 잔액 충전
- **예상 결과**: 총 1000번의 충전이 모두 반영되어야 함
- **동시성 이슈**: 잔액이 정확하지 않으면 동시성 제어 실패

#### 2. 동시 충전/조회 테스트
```java
@Test
@DisplayName("동시 잔액 충전과 조회 시 정합성 테스트")
void concurrentChargeAndGetBalanceTest()
```
- **목적**: 짝수 스레드는 충전, 홀수 스레드는 조회
- **예상 결과**: 조회된 잔액의 합계가 정확해야 함
- **동시성 이슈**: 읽기/쓰기 동시 실행 시 데이터 일관성

#### 3. 동시 잔액 사용 테스트
```java
@Test
@DisplayName("동시 잔액 사용 시 잔액 부족 예외 테스트")
void concurrentUseBalanceTest()
```
- **목적**: 초기 잔액 10,000원에서 동시에 사용
- **예상 결과**: 잔액이 음수가 되면 안 됨
- **동시성 이슈**: 잔액 부족 상황에서의 동시성 제어

#### 4. 트랜잭션 격리 테스트
```java
@Test
@DisplayName("동시 잔액 충전 시 트랜잭션 격리 수준 테스트")
void transactionIsolationTest()
```
- **목적**: 각 스레드가 잔액 조회 후 충전
- **예상 결과**: 모든 충전이 정확히 반영되어야 함
- **동시성 이슈**: 트랜잭션 격리 수준 검증

### UserCouponFacade 테스트 시나리오

#### 1. 중복 쿠폰 발급 방지 테스트
```java
@Test
@DisplayName("동시 쿠폰 발급 시 중복 발급 방지 테스트")
void concurrentPublishUserCouponTest()
```
- **목적**: 동일 사용자에게 같은 쿠폰을 여러 번 발급 시도
- **예상 결과**: 1개의 쿠폰만 발급되어야 함
- **동시성 이슈**: 중복 발급 방지 메커니즘 검증

#### 2. 동시 발급/조회 테스트
```java
@Test
@DisplayName("동시 쿠폰 발급과 조회 시 정합성 테스트")
void concurrentPublishAndGetUserCouponsTest()
```
- **목적**: 짝수 스레드는 발급, 홀수 스레드는 조회
- **예상 결과**: 최종적으로 1개의 쿠폰만 존재
- **동시성 이슈**: 읽기/쓰기 동시 실행 시 정합성

#### 3. 다중 사용자 동시 발급 테스트
```java
@Test
@DisplayName("다른 사용자에게 동시 쿠폰 발급 시 정합성 테스트")
void concurrentPublishToDifferentUsersTest()
```
- **목적**: 서로 다른 사용자들에게 동시에 쿠폰 발급
- **예상 결과**: 각 사용자는 1개의 쿠폰만 가져야 함
- **동시성 이슈**: 사용자별 격리 검증

#### 4. 쿠폰 사용 정합성 테스트
```java
@Test
@DisplayName("동시 쿠폰 사용 시 정합성 테스트")
void concurrentUseUserCouponTest()
```
- **목적**: 동일한 쿠폰을 여러 번 사용 시도
- **예상 결과**: 쿠폰은 한 번만 사용되어야 함
- **동시성 이슈**: 쿠폰 사용 상태 변경의 동시성 제어

## 🔧 테스트 실행 방법

### 1. 개별 테스트 실행
```bash
# BalanceFacade 동시성 테스트
./gradlew test --tests BalanceFacadeConcurrencyTest

# UserCouponFacade 동시성 테스트
./gradlew test --tests UserCouponFacadeConcurrencyTest
```

### 2. 전체 동시성 테스트 실행
```bash
./gradlew test --tests "*ConcurrencyTest"
```

### 3. 특정 테스트 메서드 실행
```bash
./gradlew test --tests "BalanceFacadeConcurrencyTest.concurrentChargeBalanceTest"
```

## 📈 테스트 결과 해석

### 성공적인 테스트 결과
```
=== 동시성 테스트 결과 ===
성공한 충전 횟수: 1000
실패한 충전 횟수: 0
예상 잔액: 1000000
실제 잔액: 1000000
잔액 차이: 0
```

### 실패한 테스트 결과 (동시성 이슈)
```
=== 동시성 테스트 결과 ===
성공한 충전 횟수: 1000
실패한 충전 횟수: 0
예상 잔액: 1000000
실제 잔액: 950000
잔액 차이: 50000  // 동시성 이슈 발생
```

## ⚠️ 동시성 이슈 해결 방안

### 1. 데이터베이스 레벨 해결
```sql
-- Pessimistic Lock (비관적 락)
SELECT * FROM balance WHERE user_id = ? FOR UPDATE;

-- Optimistic Lock (낙관적 락)
UPDATE balance SET balance = ?, version = version + 1 
WHERE user_id = ? AND version = ?
```

### 2. 애플리케이션 레벨 해결
```java
// @Transactional(isolation = Isolation.SERIALIZABLE)
@Transactional(isolation = Isolation.REPEATABLE_READ)
public void chargeBalance(BalanceCriteria.Charge criteria) {
    // 동시성 제어 로직
}

// 분산 락 사용
@Lock(LockModeType.PESSIMISTIC_WRITE)
public Balance findByUserId(Long userId) {
    // 락을 사용한 조회
}
```

### 3. 인덱스 최적화
```sql
-- 사용자별 잔액 조회 최적화
CREATE INDEX idx_balance_user_id ON balance(user_id);

-- 쿠폰 중복 발급 방지
CREATE UNIQUE INDEX idx_user_coupon_unique ON user_coupon(user_id, coupon_id);
```

## 🎯 권장사항

### 1. 테스트 환경 설정
- **데이터베이스**: 실제 운영과 동일한 설정 사용
- **트랜잭션 격리 수준**: REPEATABLE_READ 이상 권장
- **동시 사용자 수**: 실제 예상 사용자 수의 2배로 테스트

### 2. 모니터링 지표
- **성공률**: 100% 달성 목표
- **응답 시간**: 동시성 제어로 인한 지연 모니터링
- **데이터 정합성**: 잔액, 쿠폰 개수 등 핵심 지표 검증

### 3. 정기적인 테스트
- **개발 단계**: 새로운 기능 추가 시 동시성 테스트 필수
- **배포 전**: 운영 배포 전 동시성 테스트 실행
- **정기 점검**: 월 1회 전체 동시성 테스트 실행

## 📊 성능 최적화 팁

### 1. 락 최적화
```java
// 락 범위 최소화
@Transactional
public void chargeBalance(BalanceCriteria.Charge criteria) {
    // 락 획득
    Balance balance = balanceRepository.findByUserIdForUpdate(criteria.getUserId());
    
    // 빠른 연산
    balance.charge(criteria.getAmount());
    
    // 락 해제
    balanceRepository.save(balance);
}
```

### 2. 배치 처리
```java
// 배치로 처리하여 락 횟수 감소
@Transactional
public void chargeBalanceBatch(List<BalanceCriteria.Charge> criteriaList) {
    // 한 번의 트랜잭션으로 여러 충전 처리
}
```

### 3. 캐시 활용
```java
// Redis 캐시를 활용한 동시성 제어
@Cacheable(value = "balance", key = "#userId")
public Balance getBalance(Long userId) {
    return balanceRepository.findByUserId(userId);
}
```
