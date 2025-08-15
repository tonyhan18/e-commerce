package kr.hhplus.be.server.support.lock;

import jakarta.transaction.Transactional;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;

import kr.hhplus.be.server.support.ConcurrencyTestSupport;
import kr.hhplus.be.server.support.lock.DistributedLock;
import kr.hhplus.be.server.support.lock.LockType;
import kr.hhplus.be.server.support.lock.LockStrategy;
import static org.assertj.core.api.Assertions.assertThat;

@Import(DistributedLockAspectIntegrationTest.TestService.class)
class DistributedLockAspectIntegrationTest extends ConcurrencyTestSupport {

    @Autowired
    private TestService testService;

    @DisplayName("스핀 락을 사용한 분산 락이 정상적으로 동작한다.")
    @Test
    void spinLockTest() {
        // given
        String key = "spinLockKey";
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        executeConcurrency(10, () -> {
            try {
                testService.spinLock(key);
                successCount.incrementAndGet();
            } catch (Exception e) {
                failCount.incrementAndGet();
            }
        });

        // then
        assertThat(successCount.get()).isEqualTo(10);
        assertThat(failCount.get()).isZero();
    }

    @DisplayName("Pub/Sub 락을 사용한 분산 락이 정상적으로 동작한다.")
    @Test
    void pubSubLockTest() {
        // given
        String key = "pubSubLockKey";
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        executeConcurrency(10, () -> {
            try {
                testService.pubSubLock(key);
                successCount.incrementAndGet();
            } catch (Exception e) {
                failCount.incrementAndGet();
            }
        });

        // then
        assertThat(successCount.get()).isEqualTo(10);
        assertThat(failCount.get()).isZero();
    }

    @Service
    static class TestService {

        @Transactional
        @DistributedLock(type = LockType.COUPON, key = "#key", strategy = LockStrategy.SPIN_LOCK)
        public String spinLock(String key) {
            return "spinLock";
        }

        @Transactional
        @DistributedLock(type = LockType.COUPON, key = "#key", strategy = LockStrategy.PUB_SUB_LOCK)
        public String pubSubLock(String key) {
            return "pubSubLock";
        }
    }

}