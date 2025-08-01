package kr.hhplus.be.server.domain.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCouponServiceIntegrationTest {

    @Mock
    private UserCouponRepository userCouponRepository;

    @Mock
    private UserCoupon testUserCoupon;

    @InjectMocks
    private UserCouponService userCouponService;

    @BeforeEach
    void setUp() {
        // 기본 mock 설정은 각 테스트에서 필요에 따라 설정
    }

    @Test
    @DisplayName("사용자 쿠폰 생성 - 성공")
    void createUserCoupon_success() {
        // given
        Long userId = 1L;
        Long couponId = 1L;
        UserCouponCommand.Publish command = UserCouponCommand.Publish.of(userId, couponId);
        when(userCouponRepository.save(any(UserCoupon.class))).thenReturn(testUserCoupon);

        // when
        userCouponService.createUserCoupon(command);

        // then
        verify(userCouponRepository, times(1)).save(any(UserCoupon.class));
    }

    @Test
    @DisplayName("사용 가능한 쿠폰 조회 - 성공")
    void getUsableCoupon_success() {
        // given
        Long userId = 1L;
        Long couponId = 1L;
        UserCouponCommand.UsableCoupon command = UserCouponCommand.UsableCoupon.of(userId, couponId);
        when(testUserCoupon.getId()).thenReturn(1L);
        when(testUserCoupon.cannotUse()).thenReturn(false);
        when(userCouponRepository.findByUserIdAndCouponId(userId, couponId)).thenReturn(testUserCoupon);

        // when
        UserCouponInfo.UsableCoupon result = userCouponService.getUsableCoupon(command);

        // then
        assertThat(result).isNotNull();
        verify(userCouponRepository, times(1)).findByUserIdAndCouponId(userId, couponId);
    }

    @Test
    @DisplayName("사용 가능한 쿠폰 조회 - 사용 불가능한 쿠폰")
    void getUsableCoupon_cannotUse() {
        // given
        Long userId = 1L;
        Long couponId = 1L;
        UserCouponCommand.UsableCoupon command = UserCouponCommand.UsableCoupon.of(userId, couponId);
        UserCoupon usedCoupon = mock(UserCoupon.class);
        when(usedCoupon.cannotUse()).thenReturn(true);
        when(userCouponRepository.findByUserIdAndCouponId(userId, couponId)).thenReturn(usedCoupon);

        // when & then
        assertThatThrownBy(() -> userCouponService.getUsableCoupon(command))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("사용할 수 없는 쿠폰입니다.");
        verify(userCouponRepository, times(1)).findByUserIdAndCouponId(userId, couponId);
    }

    @Test
    @DisplayName("사용자 쿠폰 사용 - 성공")
    void useUserCoupon_success() {
        // given
        Long userCouponId = 1L;
        when(userCouponRepository.findById(userCouponId)).thenReturn(testUserCoupon);

        // when
        userCouponService.useUserCoupon(userCouponId);

        // then
        verify(userCouponRepository, times(1)).findById(userCouponId);
        verify(testUserCoupon, times(1)).use();
    }

    @Test
    @DisplayName("사용자 쿠폰 목록 조회 - 성공")
    void getUserCoupons_success() {
        // given
        Long userId = 1L;
        when(testUserCoupon.getId()).thenReturn(1L);
        when(testUserCoupon.getCouponId()).thenReturn(1L);
        when(testUserCoupon.getIssuedAt()).thenReturn(LocalDateTime.now());
        List<UserCoupon> userCoupons = List.of(testUserCoupon);
        when(userCouponRepository.findByUserIdAndUsableStatusIn(userId, UserCouponUsedStatus.forUsable()))
            .thenReturn(userCoupons);

        // when
        UserCouponInfo.Coupons result = userCouponService.getUserCoupons(userId);

        // then
        assertThat(result).isNotNull();
        verify(userCouponRepository, times(1)).findByUserIdAndUsableStatusIn(userId, UserCouponUsedStatus.forUsable());
    }

    @Test
    @DisplayName("사용자 쿠폰 목록 조회 - 빈 결과")
    void getUserCoupons_empty() {
        // given
        Long userId = 1L;
        List<UserCoupon> userCoupons = List.of();
        when(userCouponRepository.findByUserIdAndUsableStatusIn(userId, UserCouponUsedStatus.forUsable()))
            .thenReturn(userCoupons);

        // when
        UserCouponInfo.Coupons result = userCouponService.getUserCoupons(userId);

        // then
        assertThat(result).isNotNull();
        verify(userCouponRepository, times(1)).findByUserIdAndUsableStatusIn(userId, UserCouponUsedStatus.forUsable());
    }

    @Test
    @DisplayName("사용자 쿠폰 사용 - 쿠폰 사용 실패")
    void useUserCoupon_failure() {
        // given
        Long userCouponId = 1L;
        UserCoupon usedCoupon = mock(UserCoupon.class);
        when(userCouponRepository.findById(userCouponId)).thenReturn(usedCoupon);
        doThrow(new IllegalStateException("사용할 수 없는 쿠폰입니다."))
            .when(usedCoupon).use();

        // when & then
        assertThatThrownBy(() -> userCouponService.useUserCoupon(userCouponId))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("사용할 수 없는 쿠폰입니다.");
        verify(userCouponRepository, times(1)).findById(userCouponId);
        verify(usedCoupon, times(1)).use();
    }
} 