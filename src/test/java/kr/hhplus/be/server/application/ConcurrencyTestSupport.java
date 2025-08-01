package kr.hhplus.be.server.application;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * 동시성 테스트를 위한 공통 지원 클래스
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public abstract class ConcurrencyTestSupport {

    protected static final int DEFAULT_THREAD_COUNT = 10;
    protected static final int DEFAULT_OPERATIONS_PER_THREAD = 100;

    /**
     * 동시성 테스트 실행
     * @param threadCount 스레드 수
     * @param operationsPerThread 스레드당 실행할 작업 수
     * @param operation 실행할 작업
     * @return 테스트 결과
     */
    protected ConcurrencyTestResult runConcurrencyTest(
            int threadCount,
            int operationsPerThread,
            Runnable operation
    ) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            CompletableFuture.runAsync(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        try {
                            operation.run();
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

        latch.await();
        executor.shutdown();

        return new ConcurrencyTestResult(successCount.get(), failureCount.get());
    }

    /**
     * 동시성 테스트 실행 (결과 반환)
     * @param threadCount 스레드 수
     * @param operationsPerThread 스레드당 실행할 작업 수
     * @param operation 실행할 작업
     * @return 테스트 결과
     */
    protected <T> ConcurrencyTestResultWithData<T> runConcurrencyTestWithData(
            int threadCount,
            int operationsPerThread,
            Supplier<T> operation
    ) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        AtomicInteger dataCollected = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            CompletableFuture.runAsync(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        try {
                            T data = operation.get();
                            if (dataCollected.getAndIncrement() == 0) {
                                // 첫 번째 성공한 결과만 저장
                            }
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

        latch.await();
        executor.shutdown();

        // 마지막에 한 번 더 실행하여 결과 수집
        T result = null;
        try {
            result = operation.get();
        } catch (Exception e) {
            // 무시
        }

        return new ConcurrencyTestResultWithData<>(successCount.get(), failureCount.get(), result);
    }

    /**
     * 동시성 테스트 결과
     */
    public static class ConcurrencyTestResult {
        private final int successCount;
        private final int failureCount;

        public ConcurrencyTestResult(int successCount, int failureCount) {
            this.successCount = successCount;
            this.failureCount = failureCount;
        }

        public int getSuccessCount() {
            return successCount;
        }

        public int getFailureCount() {
            return failureCount;
        }

        public int getTotalCount() {
            return successCount + failureCount;
        }

        public double getSuccessRate() {
            return (double) successCount / getTotalCount() * 100;
        }

        @Override
        public String toString() {
            return String.format("ConcurrencyTestResult{success=%d, failure=%d, rate=%.2f%%}",
                    successCount, failureCount, getSuccessRate());
        }
    }

    /**
     * 데이터를 포함한 동시성 테스트 결과
     */
    public static class ConcurrencyTestResultWithData<T> extends ConcurrencyTestResult {
        private final T data;

        public ConcurrencyTestResultWithData(int successCount, int failureCount, T data) {
            super(successCount, failureCount);
            this.data = data;
        }

        public T getData() {
            return data;
        }
    }
} 