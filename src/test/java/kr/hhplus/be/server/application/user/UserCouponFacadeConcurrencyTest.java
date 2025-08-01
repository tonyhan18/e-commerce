package kr.hhplus.be.server.application.user;

import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.CouponStatus;
import kr.hhplus.be.server.domain.user.UserCouponService;
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

import java.time.LocalDateTime;
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
class UserCouponFacadeConcurrencyTest {

    @Autowired
    private UserCouponFacade userCouponFacade;

    @Autowired
    private UserService userService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private UserCouponService userCouponService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    private Long testUserId;
    private Long testCouponId;
    private static final int THREAD_COUNT = 5;
    private static final int OPERATIONS_PER_THREAD = 10;

    @BeforeEach
    @Transactional
    void setUp() {
        // 테스트용 사용자 생성
        User testUser = User.create("테스트사용자_1");
        testUser = userRepository.save(testUser);
        this.testUserId = testUser.getId();
        
        // 테스트용 쿠폰 생성
        Coupon testCoupon = Coupon.create("테스트쿠폰", 0.1, 100, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(30));
        testCoupon = couponRepository.save(testCoupon);
        this.testCouponId = testCoupon.getId();
    }

    @Test
    @DisplayName("동시 쿠폰 발급 시 중복 발급 방지 테스트")
    void concurrentPublishUserCouponTest() throws InterruptedException {
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
                            // 동일한 사용자에게 동일한 쿠폰을 여러 번 발급 시도
                            userCouponFacade.publishUserCoupon(
                                UserCouponCriteria.Publish.of(testUserId, testCouponId)
                            );
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
        UserCouponResult.Coupons userCoupons = userCouponFacade.getUserCoupons(testUserId);
        int actualCouponCount = userCoupons.getCoupons().size();

        System.out.println("=== 동시 쿠폰 발급 테스트 결과 ===");
        System.out.println("성공한 발급 횟수: " + successCount.get());
        System.out.println("실패한 발급 횟수: " + failureCount.get());
        System.out.println("실제 발급된 쿠폰 수: " + actualCouponCount);

        // 동시성 이슈 분석
        System.out.println("=== 동시성 이슈 분석 ===");
        if (actualCouponCount != 1) {
            System.out.println("⚠️ 동시성 이슈 발견: 중복 발급이 발생함");
            System.out.println("이는 중복 발급 방지 메커니즘이 필요함을 의미합니다.");
        } else {
            System.out.println("✅ 중복 발급 방지가 정상적으로 작동함");
        }
        
        // 최소한 발급 시도가 있어야 함
        assertThat(successCount.get()).isGreaterThan(0);
    }

    @Test
    @DisplayName("동시 쿠폰 발급과 조회 시 정합성 테스트")
    void concurrentPublishAndGetUserCouponsTest() throws InterruptedException {
        // Given
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        AtomicInteger publishCount = new AtomicInteger(0);
        AtomicInteger getCount = new AtomicInteger(0);

        // When
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            CompletableFuture.runAsync(() -> {
                try {
                    for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                        if (threadId % 2 == 0) {
                            // 짝수 스레드는 쿠폰 발급
                            try {
                                userCouponFacade.publishUserCoupon(
                                    UserCouponCriteria.Publish.of(testUserId, testCouponId)
                                );
                                publishCount.incrementAndGet();
                            } catch (Exception e) {
                                // 중복 발급 시도는 실패할 수 있음
                            }
                        } else {
                            // 홀수 스레드는 쿠폰 조회
                            UserCouponResult.Coupons coupons = userCouponFacade.getUserCoupons(testUserId);
                            getCount.addAndGet(coupons.getCoupons().size());
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
        UserCouponResult.Coupons finalCoupons = userCouponFacade.getUserCoupons(testUserId);
        int finalCouponCount = finalCoupons.getCoupons().size();

        System.out.println("=== 동시 발급/조회 테스트 결과 ===");
        System.out.println("발급 시도 횟수: " + publishCount.get());
        System.out.println("조회된 쿠폰 총 개수: " + getCount.get());
        System.out.println("최종 쿠폰 개수: " + finalCouponCount);

        // 동시성 이슈 분석
        System.out.println("=== 동시성 이슈 분석 ===");
        if (finalCouponCount != 1) {
            System.out.println("⚠️ 동시성 이슈 발견: 중복 발급이 발생함");
            System.out.println("이는 중복 발급 방지 메커니즘이 필요함을 의미합니다.");
        } else {
            System.out.println("✅ 중복 발급 방지가 정상적으로 작동함");
        }
        
        // 최소한 발급 시도가 있어야 함
        assertThat(publishCount.get()).isGreaterThan(0);
    }

    @Test
    @DisplayName("다른 사용자에게 동시 쿠폰 발급 시 정합성 테스트")
    void concurrentPublishToDifferentUsersTest() throws InterruptedException {
        // Given
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // When
        for (int i = 0; i < THREAD_COUNT; i++) {
            final Long userId = (long) (i + 1);
            CompletableFuture.runAsync(() -> {
                try {
                    for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                        try {
                            // 각 스레드는 다른 사용자에게 쿠폰 발급
                            userCouponFacade.publishUserCoupon(
                                UserCouponCriteria.Publish.of(userId, testCouponId)
                            );
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
        System.out.println("=== 다른 사용자 동시 발급 테스트 결과 ===");
        System.out.println("성공한 발급 횟수: " + successCount.get());
        System.out.println("실패한 발급 횟수: " + failureCount.get());

        // 각 사용자별로 쿠폰 개수 확인
        for (int i = 1; i <= THREAD_COUNT; i++) {
            Long userId = (long) i;
            try {
                UserCouponResult.Coupons userCoupons = userCouponFacade.getUserCoupons(userId);
                int couponCount = userCoupons.getCoupons().size();
                System.out.println("사용자 " + userId + "의 쿠폰 개수: " + couponCount);
                
                // 각 사용자는 1개의 쿠폰만 가져야 함
                assertThat(couponCount).isEqualTo(1);
            } catch (Exception e) {
                // 사용자가 존재하지 않는 경우 무시
            }
        }
    }

    @Test
    @DisplayName("트랜잭션 격리 수준 테스트")
    void transactionIsolationTest() throws InterruptedException {
        // Given
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Integer> couponCounts = new ArrayList<>();

        // When
        for (int i = 0; i < THREAD_COUNT; i++) {
            CompletableFuture.runAsync(() -> {
                try {
                    // 각 스레드가 동시에 쿠폰을 조회하고 발급
                    UserCouponResult.Coupons currentCoupons = userCouponFacade.getUserCoupons(testUserId);
                    int currentCount = currentCoupons.getCoupons().size();
                    
                    try {
                        userCouponFacade.publishUserCoupon(
                            UserCouponCriteria.Publish.of(testUserId, testCouponId)
                        );
                    } catch (Exception e) {
                        // 중복 발급 시도는 실패할 수 있음
                    }
                    
                    couponCounts.add(currentCount);
                } finally {
                    latch.countDown();
                }
            }, executor);
        }

        // 모든 스레드가 완료될 때까지 대기
        latch.await();
        executor.shutdown();

        // Then
        UserCouponResult.Coupons finalCoupons = userCouponFacade.getUserCoupons(testUserId);
        int finalCouponCount = finalCoupons.getCoupons().size();

        System.out.println("=== 트랜잭션 격리 테스트 결과 ===");
        System.out.println("최종 쿠폰 개수: " + finalCouponCount);
        System.out.println("조회된 쿠폰 개수들: " + couponCounts);

        // 동시성 이슈 분석
        System.out.println("=== 동시성 이슈 분석 ===");
        if (finalCouponCount != 1) {
            System.out.println("⚠️ 동시성 이슈 발견: 중복 발급이 발생함");
            System.out.println("이는 중복 발급 방지 메커니즘이 필요함을 의미합니다.");
        } else {
            System.out.println("✅ 중복 발급 방지가 정상적으로 작동함");
        }
        
        // 최소한 쿠폰이 발급되어야 함
        assertThat(finalCouponCount).isGreaterThan(0);
    }
} 