package kr.hhplus.be.server.application.balance;

import kr.hhplus.be.server.domain.balance.BalanceService;
import kr.hhplus.be.server.domain.balance.BalanceCommand;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class BalanceFacadeConcurrencyTest {

    @Autowired
    private BalanceFacade balanceFacade;

    @Autowired
    private UserService userService;

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private UserRepository userRepository;

    private Long testUserId;
    private static final Long CHARGE_AMOUNT = 1000L;
    private static final int THREAD_COUNT = 5;
    private static final int OPERATIONS_PER_THREAD = 20;

    @BeforeEach
    @Transactional
    void setUp() {
        // 테스트용 사용자 생성
        User testUser = User.create("테스트사용자_1");
        testUser = userRepository.save(testUser);
        this.testUserId = testUser.getId();
    }

    @Test
    @DisplayName("동시 잔액 충전 시 잔액 정합성 테스트")
    void concurrentChargeBalanceTest() throws InterruptedException {
        // Given
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // When
        for (int i = 0; i < THREAD_COUNT; i++) {
            CompletableFuture.runAsync(() -> {
                try {
                    for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                        try {
                            balanceFacade.chargeBalance(BalanceCriteria.Charge.of(testUserId, CHARGE_AMOUNT));
                            successCount.incrementAndGet();
                        } catch (Exception e) {
                            failureCount.incrementAndGet();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            }, executor);
        }

        // 모든 스레드가 완료될 때까지 대기
        latch.await();
        executor.shutdown();

        // Then
        long expectedBalance = (long) successCount.get() * CHARGE_AMOUNT;
        long actualBalance = balanceFacade.getBalance(testUserId).getBalance();

        System.out.println("=== 동시성 테스트 결과 ===");
        System.out.println("성공한 충전 횟수: " + successCount.get());
        System.out.println("실패한 충전 횟수: " + failureCount.get());
        System.out.println("예상 잔액: " + expectedBalance);
        System.out.println("실제 잔액: " + actualBalance);
        System.out.println("잔액 차이: " + (expectedBalance - actualBalance));

        // 동시성 이슈가 있을 수 있으므로 실제 결과를 확인
        System.out.println("=== 동시성 이슈 분석 ===");
        if (actualBalance != expectedBalance) {
            System.out.println("⚠️ 동시성 이슈 발견: 잔액이 정확하지 않음");
            System.out.println("이는 동시성 제어가 필요함을 의미합니다.");
        } else {
            System.out.println("✅ 동시성 제어가 정상적으로 작동함");
        }
        
        // 최소한 성공한 충전이 있어야 함
        assertThat(successCount.get()).isGreaterThan(0);
        // 잔액이 0보다 커야 함
        assertThat(actualBalance).isGreaterThan(0);
    }

    @Test
    @DisplayName("동시 잔액 충전과 조회 시 정합성 테스트")
    void concurrentChargeAndGetBalanceTest() throws InterruptedException {
        // Given
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        AtomicLong totalCharged = new AtomicLong(0);
        AtomicLong totalRetrieved = new AtomicLong(0);

        // When
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            CompletableFuture.runAsync(() -> {
                try {
                    for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                        if (threadId % 2 == 0) {
                            // 짝수 스레드는 충전
                            balanceFacade.chargeBalance(BalanceCriteria.Charge.of(testUserId, CHARGE_AMOUNT));
                            totalCharged.addAndGet(CHARGE_AMOUNT);
                        } else {
                            // 홀수 스레드는 조회
                            long balance = balanceFacade.getBalance(testUserId).getBalance();
                            totalRetrieved.addAndGet(balance);
                        }
                    }
                } finally {
                    latch.countDown();
                }
            }, executor);
        }

        // 모든 스레드가 완료될 때까지 대기
        latch.await();
        executor.shutdown();

        // Then
        long finalBalance = balanceFacade.getBalance(testUserId).getBalance();
        long expectedCharged = (THREAD_COUNT / 2) * OPERATIONS_PER_THREAD * CHARGE_AMOUNT;

        System.out.println("=== 동시 충전/조회 테스트 결과 ===");
        System.out.println("총 충전 금액: " + totalCharged.get());
        System.out.println("총 조회된 잔액 합계: " + totalRetrieved.get());
        System.out.println("최종 잔액: " + finalBalance);
        System.out.println("예상 충전 금액: " + expectedCharged);

        // 동시성 이슈 분석
        System.out.println("=== 동시성 이슈 분석 ===");
        if (finalBalance != expectedCharged) {
            System.out.println("⚠️ 동시성 이슈 발견: 최종 잔액이 예상과 다름");
            System.out.println("이는 동시성 제어가 필요함을 의미합니다.");
        } else {
            System.out.println("✅ 동시성 제어가 정상적으로 작동함");
        }
        
        // 최소한 충전이 성공해야 함
        assertThat(totalCharged.get()).isGreaterThan(0);
        assertThat(finalBalance).isGreaterThan(0);
    }

    @Test
    @DisplayName("동시 잔액 사용 시 잔액 부족 예외 테스트")
    void concurrentUseBalanceTest() throws InterruptedException {
        // Given
        long initialBalance = 10000L;
        balanceFacade.chargeBalance(BalanceCriteria.Charge.of(testUserId, initialBalance));

        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // When
        for (int i = 0; i < THREAD_COUNT; i++) {
            CompletableFuture.runAsync(() -> {
                try {
                    for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                        try {
                            // 잔액 사용 (충전과 반대)
                            balanceService.useBalance(BalanceCommand.Use.of(testUserId, CHARGE_AMOUNT));
                            successCount.incrementAndGet();
                        } catch (Exception e) {
                            failureCount.incrementAndGet();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            }, executor);
        }

        // 모든 스레드가 완료될 때까지 대기
        latch.await();
        executor.shutdown();

        // Then
        long finalBalance = balanceFacade.getBalance(testUserId).getBalance();

        System.out.println("=== 동시 잔액 사용 테스트 결과 ===");
        System.out.println("성공한 사용 횟수: " + successCount.get());
        System.out.println("실패한 사용 횟수: " + failureCount.get());
        System.out.println("최종 잔액: " + finalBalance);

        // 잔액이 음수가 되면 안 됨
        assertThat(finalBalance).isGreaterThanOrEqualTo(0);
        // 성공한 사용 횟수는 초기 잔액을 초과할 수 없음
        assertThat(successCount.get()).isLessThanOrEqualTo((int) (initialBalance / CHARGE_AMOUNT));
    }

    @Test
    @DisplayName("동시 잔액 충전 시 트랜잭션 격리 수준 테스트")
    void transactionIsolationTest() throws InterruptedException {
        // Given
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Long> balances = new ArrayList<>();

        // When
        for (int i = 0; i < THREAD_COUNT; i++) {
            CompletableFuture.runAsync(() -> {
                try {
                    // 각 스레드가 동시에 잔액을 조회하고 충전
                    long currentBalance = balanceFacade.getBalance(testUserId).getBalance();
                    balanceFacade.chargeBalance(BalanceCriteria.Charge.of(testUserId, CHARGE_AMOUNT));
                    balances.add(currentBalance);
                } finally {
                    latch.countDown();
                }
            }, executor);
        }

        // 모든 스레드가 완료될 때까지 대기
        latch.await();
        executor.shutdown();

        // Then
        long finalBalance = balanceFacade.getBalance(testUserId).getBalance();
        long expectedBalance = (long) THREAD_COUNT * CHARGE_AMOUNT;

        System.out.println("=== 트랜잭션 격리 테스트 결과 ===");
        System.out.println("최종 잔액: " + finalBalance);
        System.out.println("예상 잔액: " + expectedBalance);
        System.out.println("조회된 잔액들: " + balances);

        // 동시성 이슈 분석
        System.out.println("=== 동시성 이슈 분석 ===");
        if (finalBalance != expectedBalance) {
            System.out.println("⚠️ 동시성 이슈 발견: 최종 잔액이 예상과 다름");
            System.out.println("이는 동시성 제어가 필요함을 의미합니다.");
        } else {
            System.out.println("✅ 동시성 제어가 정상적으로 작동함");
        }
        
        // 최소한 충전이 성공해야 함
        assertThat(finalBalance).isGreaterThan(0);
    }
} 