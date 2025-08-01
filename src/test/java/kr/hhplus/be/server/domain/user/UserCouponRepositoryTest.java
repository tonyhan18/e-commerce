package kr.hhplus.be.server.domain.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCouponRepositoryTest {

    @Mock
    private UserCouponRepository userCouponRepository;

    private UserCoupon testUserCoupon;

    @BeforeEach
    void setUp() {
        testUserCoupon = UserCoupon.builder()
            .id(1L)
            .userId(1L)
            .couponId(1L)
            .usedStatus(UserCouponUsedStatus.UNUSED)
            .issuedAt(LocalDateTime.now())
            .build();
    }

    @Test
    @DisplayName("사용자 쿠폰 저장 - 성공")
    void save_success() {
        // given
        when(userCouponRepository.save(any(UserCoupon.class))).thenReturn(testUserCoupon);

        // when
        UserCoupon savedUserCoupon = userCouponRepository.save(testUserCoupon);

        // then
        assertThat(savedUserCoupon).isNotNull();
        assertThat(savedUserCoupon.getId()).isEqualTo(1L);
        verify(userCouponRepository, times(1)).save(testUserCoupon);
    }

    @Test
    @DisplayName("사용자 ID와 쿠폰 ID로 조회 - 성공")
    void findByUserIdAndCouponId_success() {
        // given
        Long userId = 1L;
        Long couponId = 1L;
        when(userCouponRepository.findByUserIdAndCouponId(userId, couponId)).thenReturn(testUserCoupon);

        // when
        UserCoupon result = userCouponRepository.findByUserIdAndCouponId(userId, couponId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getCouponId()).isEqualTo(1L);
        verify(userCouponRepository, times(1)).findByUserIdAndCouponId(userId, couponId);
    }

    @Test
    @DisplayName("사용자 ID와 사용 가능한 상태로 조회 - 성공")
    void findByUserIdAndUsableStatusIn_success() {
        // given
        Long userId = 1L;
        List<UserCouponUsedStatus> statuses = List.of(UserCouponUsedStatus.UNUSED);
        List<UserCoupon> userCoupons = List.of(testUserCoupon);
        when(userCouponRepository.findByUserIdAndUsableStatusIn(userId, statuses)).thenReturn(userCoupons);

        // when
        List<UserCoupon> result = userCouponRepository.findByUserIdAndUsableStatusIn(userId, statuses);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(1L);
        verify(userCouponRepository, times(1)).findByUserIdAndUsableStatusIn(userId, statuses);
    }

    @Test
    @DisplayName("사용자 쿠폰 ID로 조회 - 성공")
    void findById_success() {
        // given
        Long userCouponId = 1L;
        when(userCouponRepository.findById(userCouponId)).thenReturn(testUserCoupon);

        // when
        UserCoupon result = userCouponRepository.findById(userCouponId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(userCouponRepository, times(1)).findById(userCouponId);
    }
} 