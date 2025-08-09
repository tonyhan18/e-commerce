# 동시성 제어를 위한 Database 락 전략 분석 및 구현 보고서

## 🖼️ 1. 배경

### 1.1 프로젝트 개요
- **프로젝트명**: E-commerce 플랫폼
- **기술 스택**: Spring Boot, JPA/Hibernate, MySQL
- **주요 기능**: 상품 관리, 재고 관리, 주문 처리, 쿠폰 발급, 잔액 관리

### 1.2 동시성 이슈 발생 배경
현대적인 웹 애플리케이션에서는 다수의 사용자가 동시에 시스템을 이용하게 됩니다. 특히 다음과 같은 상황에서 동시성 문제가 발생할 수 있습니다:

- **재고 관리**: 동시에 여러 사용자가 같은 상품을 구매할 때
- **쿠폰 발급**: 제한된 수량의 쿠폰을 동시에 발급받으려 할 때
- **잔액 관리**: 동시에 잔액을 충전하거나 사용할 때
- **주문 처리**: 동일한 상품에 대한 동시 주문

### 1.3 동시성 문제의 심각성
동시성 문제가 제대로 해결되지 않으면 다음과 같은 심각한 문제가 발생할 수 있습니다:

- **데이터 불일치**: 재고가 음수가 되거나 중복 주문 발생
- **비즈니스 로직 위반**: 쿠폰 수량 제한 무시
- **사용자 경험 저하**: 예상치 못한 오류 발생
- **재정적 손실**: 잘못된 거래 처리

## 🔭 2. 문제 분석

### 2.1 발생한 동시성 이슈들

#### 2.1.1 재고 관리 동시성 문제
```java
// 문제가 있는 코드
public void decreaseStock(Long productId, int quantity) {
    Stock stock = stockRepository.findByProductId(productId);
    stock.decreaseQuantity(quantity); // 동시 요청 시 문제 발생
    stockRepository.save(stock);
}
```

**문제점**:
- 동시에 여러 요청이 들어올 때 마지막 요청만 반영
- 재고가 음수가 될 수 있음
- 데이터 일관성 보장 불가

#### 2.1.2 쿠폰 발급 동시성 문제
```java
// 문제가 있는 코드
public void publishCoupon(Long couponId) {
    Coupon coupon = couponRepository.findById(couponId);
    if (coupon.getQuantity() > 0) {
        coupon.decreaseQuantity();
        // 동시 요청 시 중복 발급 가능
    }
}
```

**문제점**:
- 쿠폰 수량 제한을 초과하여 발급
- 동일한 쿠폰을 여러 번 발급받을 수 있음

#### 2.1.3 잔액 관리 동시성 문제
```java
// 문제가 있는 코드
public void chargeBalance(Long userId, Long amount) {
    Balance balance = balanceRepository.findByUserId(userId);
    balance.addAmount(amount); // 동시 충전 시 문제 발생
    balanceRepository.save(balance);
}
```

**문제점**:
- 동시 충전 시 일부 충전 금액이 손실
- 최대 잔액 제한을 초과할 수 있음

### 2.2 동시성 문제의 근본 원인

1. **Race Condition**: 여러 스레드가 동시에 같은 데이터를 수정
2. **읽기-수정-쓰기 패턴**: 읽은 데이터를 기반으로 수정 후 저장하는 과정에서 동시성 문제 발생
3. **트랜잭션 격리 수준**: 기본 격리 수준으로는 동시성 문제 완전 해결 불가
4. **락 미적용**: 동시성 제어를 위한 락 메커니즘 부재

## 🛠️ 3. Database 락을 통한 해결 방안

### 🔐 3.1 락의 개념과 필요성

**락(Lock)**이란 데이터베이스에서 동시성 제어를 위해 사용하는 메커니즘으로, 여러 트랜잭션이 동시에 같은 데이터에 접근할 때 데이터의 일관성을 보장합니다. **충돌을 방지하고 정합성을 보장하기 위한 제어 장치이다.**
이를 통해 데이터의 동시 읽기/쓰기 충돌을 제어할 수 있습니다.

일반적으로 **쓰기 트랜잭션**에는 **배타 락(X-Lock)** 이 걸려,  
다른 트랜잭션이 동시에 동일한 데이터에 쓰기 작업을 수행하지 못하도록 막는다.

하지만 대부분의 RDBMS는 **MVCC(Multi-Version Concurrency Control)** 을 지원하여,  
**쓰기 작업 중인 데이터라도 이전 트랜잭션 기준의 값을 읽을 수 있도록 보장**한다.

> **MVCC(Multi-Version Concurrency Control)이란?**  
> 트랜잭션마다 데이터를 버전 관리하여, **동시에 쓰기가 수행되고 있어도 읽기는 과거 시점의 일관된 데이터로 가능**하게 해주는 방식이다.

### 🔐 3.2 Database 락의 종류

#### 3.2.1 Optimistic Lock (낙관적 락)
- **개념**: 충돌이 적을 것이라고 낙관적으로 가정. 읽기 시점에는 락을 걸지 않으며, 쓰기 시점에 버전 충돌 여부를 확인하는 방식이다.
  - 충돌이 감지되면 예외를 발생시켜 트랜잭션을 롤백하거나 재시도해야 한다.
  - 충돌이 빈번할 경우 반복적인 실패와 재시도로 인해 성능 저하가 발생할 수 있다.
- **동작 방식**: 버전 필드를 통해 데이터 변경 감지
- **적용 시점**: 커밋 시점에 충돌 검사

#### 3.2.2 Pessimistic Lock (비관적 락)
- **개념**: 데이터 충돌이 자주 발생할 것으로 가정하고, 트랜잭션 시작 시점에 락을 설정하여 다른 트랜잭션의 접근을 차단하는 방식이다.
  - 확실한 정합성이 필요한 경우에 적합하다.
  - 다만, 락을 유지하는 동안 다른 요청은 대기 상태가 되므로, 요청량이 많은 환경에서는 데드락이나 병목 현상이 발생할 수 있다.
- **동작 방식**: 데이터 접근 시점에 락 획득
- **적용 시점**: 데이터 읽기 시점에 락 획득

### 3.3 JPA에서의 락 구현

#### 3.3.1 Optimistic Lock 구현
```java
@Entity
public class Stock {
    @Id
    private Long id;
    
    @Version
    private Long version; // Optimistic Lock을 위한 버전 필드
    
    private int quantity;
    
    public void decreaseQuantity(int amount) {
        if (this.quantity < amount) {
            throw new IllegalArgumentException("재고 부족");
        }
        this.quantity -= amount;
    }
}
```

#### 3.3.2 Pessimistic Lock 구현
```java
@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Coupon> findWithLockById(Long id);
}
```

## 4. 동시성 제어를 위한 락 전략

### 4.1 Optimistic Lock 전략

#### 4.1.1 적용 대상
- **재고 관리**: Stock 엔티티
- **잔액 관리**: Balance 엔티티
- **사용자 정보**: User 엔티티

#### 4.1.2 구현 방법
```java
@Entity
public class Stock {
    @Version
    private Long version;
    
    public void decreaseQuantity(int quantity) {
        if (this.quantity < quantity) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
        this.quantity -= quantity;
    }
}
```

#### 4.1.3 장단점
**장점**:
- 성능 우수 (락 획득 대기 시간 없음)
- 데드락 위험 낮음
- 읽기 작업이 많은 경우 유리

**단점**:
- 충돌 시 재시도 로직 필요
- 실패 확률이 높은 경우 성능 저하

### 4.2 Pessimistic Lock 전략

#### 4.2.1 적용 대상
- **쿠폰 발급**: Coupon 엔티티
- **주문 처리**: Order 엔티티
- **결제 처리**: Payment 엔티티

#### 4.2.2 구현 방법
```java
@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Coupon> findWithLockById(Long id);
}

@Service
public class CouponService {
    public void publishCoupon(CouponCommand.Publish command) {
        Coupon coupon = couponRepository.findWithLockById(command.getCouponId())
            .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));
        
        coupon.publish();
        couponRepository.save(coupon);
    }
}
```

#### 4.2.3 장단점
**장점**:
- 충돌 시 즉시 실패 처리
- 데이터 일관성 보장
- 복잡한 재시도 로직 불필요

**단점**:
- 성능 저하 (락 획득 대기)
- 데드락 위험
- 동시성 저하

## 🧭 5. 동시성 제어 방식을 결정하는 기준

### 5.1 비즈니스 특성에 따른 선택

기능별로 락 전략을 다르게 설정하는 것이 아닌, **동일한 자원에 대해서는 일관된 락 전략**을 사용해야 한다.

> 예: 잔액 충전은 **낙관적 락** 잔액 차감은 **비관적 락**을 사용하는 것이 아니라 **"잔액"이라는 동일한 자원에 대해 동일한 락 전략을 적용해야 한다.**

#### ⚖️ 락 전략을 결정하는 기준

충돌 가능성에 따라 결정할 수 있지만, **반드시 성공해야되는 요청**이면 비관적 락을 사용해야 한다. 그렇지 않으면 낙관적 락을 사용한다.

#### 📏 락 범위를 결정하는 기준

락은 **가능한 최소 범위로 설정**해야 한다. 락 범위가 넓을수록 **성능 저하와 데드락 위험이 증가**한다.

> 예: 잔액 테이블 전체에 락을 설정하는 것이 아니라, 특정 사용자의 잔액 데이터에만 락을 설정한다. 여기서 **"사용자" 단위가 락의 범위**이다.

#### 5.1.1 Optimistic Lock 선택 기준
- **읽기 작업이 많은 경우**: 데이터 조회가 수정보다 빈번한 경우
- **충돌 확률이 낮은 경우**: 동시 수정이 자주 발생하지 않는 경우
- **성능이 중요한 경우**: 락 획득 대기 시간을 최소화해야 하는 경우

#### 5.1.2 Pessimistic Lock 선택 기준
- **쓰기 작업이 많은 경우**: 데이터 수정이 조회보다 빈번한 경우
- **충돌 확률이 높은 경우**: 동시 수정이 자주 발생하는 경우
- **데이터 일관성이 중요한 경우**: 정확한 데이터 보장이 우선인 경우

### 5.2 도메인별 적용 기준

| 도메인 | 락 방식 | 선택 이유 |
|--------|---------|----------|
| 재고 관리 | Optimistic Lock | 읽기 작업이 많고, 충돌 확률이 상대적으로 낮음 |
| 쿠폰 발급 | Pessimistic Lock | 수량 제한이 엄격하고, 동시 발급 시 충돌 확률 높음 |
| 잔액 관리 | Optimistic Lock | 개인별 잔액이므로 충돌 확률이 낮음 |
| 주문 처리 | Pessimistic Lock | 재고 확인과 주문 처리가 동시에 일어나므로 일관성 중요 |

### 5.3 성능과 일관성의 트레이드오프

#### 5.3.1 성능 우선
- **Optimistic Lock** 선택
- 충돌 시 재시도 로직 구현
- 사용자 경험 향상

#### 5.3.2 일관성 우선
- **Pessimistic Lock** 선택
- 즉시 실패 처리
- 데이터 정확성 보장

## 🧩 6. JPA 락 전략 설정

### 6.1 Optimistic Lock 설정

#### 6.1.1 엔티티 설정
```java
@Entity
@Table(name = "stock")
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Version
    private Long version; // Optimistic Lock을 위한 버전 필드
    
    private Long productId;
    private int quantity;
    
    // 생성자, getter, setter
}
```

#### 6.1.2 서비스 레이어 구현
```java
@Service
@Transactional
public class StockService {
    public void decreaseStock(Long productId, int quantity) {
        Stock stock = stockRepository.findByProductId(productId)
            .orElseThrow(() -> new IllegalArgumentException("재고를 찾을 수 없습니다."));
        
        stock.decreaseQuantity(quantity);
        stockRepository.save(stock); // Optimistic Lock 적용
    }
}
```

#### 6.1.3 예외 처리
```java
@Service
public class StockService {
    public void decreaseStockWithRetry(Long productId, int quantity) {
        int maxRetries = 3;
        int retryCount = 0;
        
        while (retryCount < maxRetries) {
            try {
                decreaseStock(productId, quantity);
                return;
            } catch (OptimisticLockingFailureException e) {
                retryCount++;
                if (retryCount >= maxRetries) {
                    throw new RuntimeException("재고 차감 실패: 최대 재시도 횟수 초과");
                }
                // 잠시 대기 후 재시도
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("재고 차감 중단");
                }
            }
        }
    }
}
```

### 6.2 Pessimistic Lock 설정

#### 6.2.1 Repository 설정
```java
@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Coupon> findWithLockById(Long id);
    
    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<Coupon> findWithReadLockById(Long id);
}
```

#### 6.2.2 서비스 레이어 구현
```java
@Service
@Transactional
public class CouponService {
    public void publishCoupon(CouponCommand.Publish command) {
        Coupon coupon = couponRepository.findWithLockById(command.getCouponId())
            .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));
        
        coupon.publish();
        couponRepository.save(coupon);
    }
}
```

#### 6.2.3 락 타임아웃 설정
```java
@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "1000")})
    Optional<Coupon> findWithLockById(Long id);
}
```

### 6.3 트랜잭션 설정

#### 6.3.1 트랜잭션 격리 수준 설정
```java
@Service
@Transactional(isolation = Isolation.READ_COMMITTED)
public class StockService {
    // 서비스 메서드들
}
```

#### 6.3.2 트랜잭션 전파 설정
```java
@Service
@Transactional(propagation = Propagation.REQUIRED)
public class OrderService {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processPayment(PaymentCommand command) {
        // 결제 처리 로직
    }
}
```

## 7. 동시성 제어 방식 선정

### 7.1 도메인별 락 전략 선정

#### 7.1.1 재고 관리 (Stock)
```java
// Optimistic Lock 적용
@Entity
public class Stock {
    @Version
    private Long version;
    
    public void decreaseQuantity(int quantity) {
        if (this.quantity < quantity) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
        this.quantity -= quantity;
    }
}
```

재고 차감은 비관적 락을 적용했다. 특정 상품이 이벤트 상품이거나 인기 상품인 경우, 동시에 많은 주문 및 결제 요청이 발생할 수 있다.  
**재고가 충분함에도 차감 실패가 발생하는 상황을 방지하기 위해**, 비관적 락을 적용하여 **정확한 수량 차감이 보장되도록 제어**한다.

- **선정 이유**:
  - 재고 조회가 수정보다 빈번함
  - 동시 구매 시 충돌 확률이 상대적으로 낮음
  - 성능이 중요한 도메인
- **락 전략** : 비관적 락
- **락 범위** : 재고 테이블의 "상품" 단위


#### 7.1.2 쿠폰 발급 (Coupon)
```java
// Pessimistic Lock 적용
@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Coupon> findWithLockById(Long id);
}
```
선착순 쿠폰 발급은 충돌이 발생할 가능성이 높다.   
만약 낙관적 락으로 설정한다면, 동시에 여러 명이 발급 요청 시에 하나의 요청만 처리되고 나머지 요청은 충돌로 인해 롤백된다.  
최악의 경우, 선착순 내에 요청을 보냈음에도 **반복된 충돌로 인해 발급에 실패**할 수 있다.  
따라서 **비관적 락을 사용해 모든 요청이 정상적으로 처리될 수 있도록 제어**한다.

- **락 전략** : 비관적 락
- **락 범위** : 쿠폰 테이블의 "쿠폰" 단위
- **선정 이유**:
  - 수량 제한이 엄격함
  - 동시 발급 시 충돌 확률이 높음
  - 데이터 일관성이 매우 중요함

#### 7.1.3 잔액 관리 (Balance)
```java
// Optimistic Lock 적용
@Entity
public class Balance {
    @Version
    private Long version;
    
    public void addAmount(Long amount) {
        if (this.balance + amount > MAX_BALANCE_AMOUNT) {
            throw new IllegalArgumentException("최대 잔액을 초과합니다.");
        }
        this.balance += amount;
    }
}
```
비즈니스 상황에 따라 낙관적 락과 비관적 락 모두 고려 가능하다.  
다만 사용자 입장에서 **중복 충전이나 차감은 의도하지 않은 행위**이므로, **낙관적 락**을 적용하여 하나의 요청만 처리되도록 제어한다.

- **락 전략**: 낙관적 락
- **락 범위**: 잔액 테이블의 "사용자" 단위
- **선정 이유**:
  - 개인별 잔액이므로 충돌 확률이 낮음
  - 읽기 작업이 수정보다 빈번함
  - 성능이 중요한 도메인

### 7.2 성능 최적화 전략

#### 7.2.1 락 범위 최소화
```java
@Service
public class StockService {
    @Transactional
    public void decreaseStock(Long productId, int quantity) {
        // 락 획득 범위를 최소화
        Stock stock = stockRepository.findByProductId(productId)
            .orElseThrow(() -> new IllegalArgumentException("재고를 찾을 수 없습니다."));
        
        // 비즈니스 로직 수행
        stock.decreaseQuantity(quantity);
        
        // 락 해제 (트랜잭션 종료 시)
        stockRepository.save(stock);
    }
}
```

#### 7.2.2 배치 처리 최적화
```java
@Service
public class StockService {
    @Transactional
    public void decreaseStockBatch(List<StockDecreaseCommand> commands) {
        // 배치로 처리하여 락 획득 횟수 최소화
        for (StockDecreaseCommand command : commands) {
            Stock stock = stockRepository.findByProductId(command.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("재고를 찾을 수 없습니다."));
            
            stock.decreaseQuantity(command.getQuantity());
            stockRepository.save(stock);
        }
    }
}
```

## 🧪 8. 테스트를 통한 동시성 검증

### 8.1 동시성 테스트 환경 구성

동시성 시나리오 검증은 아래와 같은 방식으로 수행한다.

1. `ExecutorService` 및 `CompletableFuture`를 활용한 테스트 클래스 구성
2. 스레드 수는 2개로 설정하여 간단한 충돌 상황을 유도
3. `AtomicInteger`로 성공/실패 요청 수 집계

#### 8.1.1 테스트 지원 클래스
```java
public class ConcurrencyTestSupport {
    public static void runConcurrentTest(int threadCount, Runnable task) {
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    task.run();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }
    }
}
```

#### 8.1.2 데이터베이스 정리 클래스
```java
@Component
public class DatabaseCleaner {
    @PersistenceContext
    private EntityManager entityManager;
    
    @Transactional
    public void clean() {
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE balance_transaction").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE balance").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE stock").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE coupon").executeUpdate();
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
    }
}
```

### 8.2 동시성 테스트 구현

#### 8.2.1 재고 동시성 테스트
```java
@Test
@DisplayName("재고 차감 시 동시 요청이 들어오면 하나만 성공해야 한다.")
void decreaseStockConcurrency() {
    // Given
    Stock stock = Stock.create(1L, 10);
    stockRepository.save(stock);
    
    AtomicInteger successCount = new AtomicInteger(0);
    AtomicInteger failCount = new AtomicInteger(0);
    
    // When
    ConcurrencyTestSupport.runConcurrentTest(2, () -> {
        try {
            stockService.decreaseStock(1L, 8);
            successCount.incrementAndGet();
        } catch (Exception e) {
            failCount.incrementAndGet();
        }
    });
    
    // Then
    assertThat(successCount.get()).isEqualTo(1);
    assertThat(failCount.get()).isEqualTo(1);
    
    Stock updatedStock = stockRepository.findByProductId(1L).orElseThrow();
    assertThat(updatedStock.getQuantity()).isEqualTo(2);
}
```

#### 8.2.2 쿠폰 동시성 테스트
```java
@Test
@DisplayName("쿠폰 발급 시 동시 요청이 들어오면 하나만 성공해야 한다.")
void publishCouponConcurrency() {
    // Given
    Coupon coupon = Coupon.create("테스트 쿠폰", 0.1, 1, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(1));
    couponRepository.save(coupon);
    
    AtomicInteger successCount = new AtomicInteger(0);
    AtomicInteger failCount = new AtomicInteger(0);
    
    // When
    ConcurrencyTestSupport.runConcurrentTest(2, () -> {
        try {
            couponService.publishCoupon(CouponCommand.Publish.of(1L, coupon.getId()));
            successCount.incrementAndGet();
        } catch (Exception e) {
            failCount.incrementAndGet();
        }
    });
    
    // Then
    assertThat(successCount.get()).isEqualTo(1);
    assertThat(failCount.get()).isEqualTo(1);
}
```

#### 8.2.3 잔액 동시성 테스트
```java
@Test
@DisplayName("잔액 충전 시 동시 요청이 들어오면 하나만 성공해야 한다.")
void chargeBalanceConcurrency() {
    // Given
    User user = User.create("테스트 사용자");
    userRepository.save(user);
    
    Balance balance = Balance.create(user.getId());
    balanceRepository.save(balance);
    
    AtomicInteger successCount = new AtomicInteger(0);
    AtomicInteger failCount = new AtomicInteger(0);
    
    // When
    ConcurrencyTestSupport.runConcurrentTest(2, () -> {
        try {
            balanceService.chargeBalance(BalanceCommand.Charge.of(user.getId(), 1000L));
            successCount.incrementAndGet();
        } catch (Exception e) {
            failCount.incrementAndGet();
        }
    });
    
    // Then
    assertThat(successCount.get()).isEqualTo(1);
    assertThat(failCount.get()).isEqualTo(1);
    
    Balance updatedBalance = balanceRepository.findByUserId(user.getId()).orElseThrow();
    assertThat(updatedBalance.getBalance()).isEqualTo(1000L);
}
```

### 8.3 테스트 결과 분석

#### 8.3.1 성공한 테스트들
| 테스트 | 락 방식 | 결과 | 검증 사항 |
|--------|---------|------|----------|
| StockServiceConcurrencyTest | Optimistic Lock | ✅ 성공 | 재고 차감 동시성 제어 |
| CouponServiceConcurrencyTest | Pessimistic Lock | ✅ 성공 | 쿠폰 발급 동시성 제어 |
| BalanceServiceConcurrencyTest | Optimistic Lock | ✅ 성공 | 잔액 충전 동시성 제어 |

#### 8.3.2 테스트를 통한 검증 포인트
1. **동시성 제어**: 동시 요청 시 하나만 성공하는지 확인
2. **데이터 일관성**: 최종 데이터가 예상과 일치하는지 확인
3. **예외 처리**: 동시성 충돌 시 적절한 예외가 발생하는지 확인
4. **성능 영향**: 락 적용으로 인한 성능 저하가 허용 범위 내인지 확인

## 🚧 9. 한계와 결론

### 9.1 현재 구현의 한계

앞서 살펴본 것처럼 DB 수준의 락을 통해 동시성 문제를 효과적으로 제어할 수 있다.  
하지만 **DB 락 기반의 동시성 제어는 몇 가지 한계점**을 갖고 있으며, 상황에 따라 적절한 대안이 필요하다. 

#### 9.1.1 기술적 한계

예를 들어, 잔액 충전/차감과 같이 **같은 자원에 대해 반복된 요청이 들어올 경우**,  
낙관적 락을 통해 **중복 요청을 제한**하며 정합성을 보장할 수 있다.

하지만 서로 다른 형태의 요청이 동시에 들어올 경우 (예: **한쪽은 충전, 다른 한쪽은 차감 요청**)   
**모두 정합하게 반영되어야 하는 상황**이라면 단순 낙관적 락으로는 제어가 어렵다.

이 경우 **비관적 락으로 직렬화 처리**할 수 있지만, 이는 다음과 같은 **사이드 이펙트**를 유발할 수 있다.

이러한 상황에서는 **Idempotency Key(멱등 키)를** 활용해  
동일한 요청이 여러 번 전송되더라도 **최초 요청과 동일한 결과만 수행되도록 제어**할 수 있다. 

1. **단일 데이터베이스 환경**: 분산 환경에서의 동시성 제어 미고려
2. **락 타임아웃**: 긴 트랜잭션 시 데드락 위험
3. **성능 저하**: Pessimistic Lock 사용 시 동시성 저하
4. **복잡한 비즈니스 로직**: 복잡한 도메인 로직에서의 락 적용 어려움

### 9.1.2 비즈니스 측면의 한계

예를 들어, 잔액 충전/차감과 같이 **같은 자원에 대해 반복된 요청이 들어올 경우**,  
낙관적 락을 통해 **중복 요청을 제한**하며 정합성을 보장할 수 있다.

하지만 서로 다른 형태의 요청이 동시에 들어올 경우 (예: **한쪽은 충전, 다른 한쪽은 차감 요청**)   
**모두 정합하게 반영되어야 하는 상황**이라면 단순 낙관적 락으로는 제어가 어렵다.

이 경우 **비관적 락으로 직렬화 처리**할 수 있지만, 이는 다음과 같은 **사이드 이펙트**를 유발할 수 있다.

이러한 상황에서는 **Idempotency Key(멱등 키)를** 활용해  
동일한 요청이 여러 번 전송되더라도 **최초 요청과 동일한 결과만 수행되도록 제어**할 수 있다. 

### 9.1.3 순서 보장의 한계

비관적 락을 사용해도 "공정성"이 보장되지는 않는다.   
여기서의 "공정성"이란, 먼저 대기한 트랜잭션이 먼저 락을 획득하는 것을 의미한다. 

하지만 대부분의 DB 락은 비공정한 방식으로 동작하며,  
락 획득 순서는 OS 스케줄링, 커넥션 풀, 트랜잭션 격리 수준 등에 의해 결정된다.

따라서 동시 요청이 몰리는 선착순 쿠폰 발급의 경우,    
공정성이 보장되지 않기 때문에 "선착순"의 의미가 무의미해질 수 있다.

이러한 시나리오에서는 단순 DB 락이 아닌,    
Kafka와 같은 메시지 큐 기반의 직렬 처리 방식을 도입해 순서를 보장하는 것이 바람직하다.

### 9.1.4 성능 측면의 한계

비관적 락은 **트랜잭션이 끝날 때까지 자원을 점유**하므로,  
요청량이 많을 경우 병목과 대기 현상이 발생하게 된다.

DB 락의 한계를 보완하기 위해 메세지 큐(Kafka, RabbitMQ 등)와 분산 락(Zookeeper, Redis 등)을 활용하여 성능을 완화 할 수 있다.

### ✅ 9.2 결론

본 보고서에서는 동시성 이슈를 분석하고, 방안을 제시하고 DB 락을 통한 동시성 제어로 동시성 문제를 해결해보았다. 

+ **동시성 문제 정의**: 잔액 충전/차감, 선착순 쿠폰 발급, 재고 차감 등에서의 경합 상황
+ **동시성 제어를 위한 DB 락 적용**: 낙관적 락 및 비관적 락을 활용한 정합성 보장 방식 제시
+ **테스트 기반 검증**: 실제 테스트 코드와 결과를 통해 락 전략의 효과 검증
+ **한계 및 대안**: DB 락의 구조적 한계와 이를 보완할 수 있는 전략 제시 (멱등성, 메시지 큐, 분산 락 등)

이러한 동시성 제어를 통해 시스템의 안정성과 신뢰성을 확보할 수 있으며,  
향후에는 DB 락의 한계를 시스템 설계 측면으로 극복하여 더욱 향상된 성능과 확장성을 갖춘 시스템을 구축할 수 있을 것으로 기대된다.

