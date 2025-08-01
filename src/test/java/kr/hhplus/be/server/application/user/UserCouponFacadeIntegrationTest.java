package kr.hhplus.be.server.application.user;

import kr.hhplus.be.server.domain.coupon.CouponInfo;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.user.UserCouponInfo;
import kr.hhplus.be.server.domain.user.UserCouponService;
import kr.hhplus.be.server.domain.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCouponFacadeIntegrationTest {

    @Mock
    private UserService userService;

    @Mock
    private CouponService couponService;

    @Mock
    private UserCouponService userCouponService;

    @InjectMocks
    private UserCouponFacade userCouponFacade;

    @Test
    @DisplayName("사용자 쿠폰 발급 - 성공")
    void publishUserCoupon_success() {
        // given
        Long userId = 1L;
        Long couponId = 1L;
        UserCouponCriteria.Publish criteria = mock(UserCouponCriteria.Publish.class);
        when(criteria.getUserId()).thenReturn(userId);
        when(criteria.getCouponId()).thenReturn(couponId);

        // when
        userCouponFacade.publishUserCoupon(criteria);

        // then
        verify(userService, times(1)).getUser(userId);
        verify(couponService, times(1)).publishCoupon(couponId);
        verify(userCouponService, times(1)).createUserCoupon(any());
    }

    @Test
    @DisplayName("사용자 쿠폰 발급 - 사용자 없음")
    void publishUserCoupon_userNotFound() {
        // given
        Long userId = 999L;
        UserCouponCriteria.Publish criteria = mock(UserCouponCriteria.Publish.class);
        when(criteria.getUserId()).thenReturn(userId);
        doThrow(new IllegalArgumentException("User not found"))
            .when(userService).getUser(userId);

        // when & then
        assertThatThrownBy(() -> userCouponFacade.publishUserCoupon(criteria))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("User not found");
        verify(userService, times(1)).getUser(userId);
        verify(couponService, never()).publishCoupon(any());
        verify(userCouponService, never()).createUserCoupon(any());
    }

    @Test
    @DisplayName("사용자 쿠폰 발급 - 쿠폰 발급 실패")
    void publishUserCoupon_couponPublishFailure() {
        // given
        Long userId = 1L;
        Long couponId = 1L;
        UserCouponCriteria.Publish criteria = mock(UserCouponCriteria.Publish.class);
        when(criteria.getUserId()).thenReturn(userId);
        when(criteria.getCouponId()).thenReturn(couponId);
        doThrow(new IllegalStateException("쿠폰을 발급할 수 없습니다."))
            .when(couponService).publishCoupon(couponId);

        // when & then
        assertThatThrownBy(() -> userCouponFacade.publishUserCoupon(criteria))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("쿠폰을 발급할 수 없습니다.");
        verify(userService, times(1)).getUser(userId);
        verify(couponService, times(1)).publishCoupon(couponId);
        verify(userCouponService, never()).createUserCoupon(any());
    }

    @Test
    @DisplayName("사용자 쿠폰 목록 조회 - 성공")
    void getUserCoupons_success() {
        // given
        Long userId = 1L;
        List<UserCouponInfo.Coupon> userCoupons = List.of(
            mock(UserCouponInfo.Coupon.class),
            mock(UserCouponInfo.Coupon.class)
        );
        UserCouponInfo.Coupons userCouponInfo = mock(UserCouponInfo.Coupons.class);
        when(userCouponInfo.getCoupons()).thenReturn(userCoupons);
        when(userCouponService.getUserCoupons(userId)).thenReturn(userCouponInfo);

        UserCouponInfo.Coupon userCoupon1 = userCoupons.get(0);
        when(userCoupon1.getUserCouponId()).thenReturn(1L);
        when(userCoupon1.getCouponId()).thenReturn(1L);

        UserCouponInfo.Coupon userCoupon2 = userCoupons.get(1);
        when(userCoupon2.getUserCouponId()).thenReturn(2L);
        when(userCoupon2.getCouponId()).thenReturn(2L);

        CouponInfo.Coupon coupon1 = mock(CouponInfo.Coupon.class);
        when(coupon1.getName()).thenReturn("쿠폰1");
        when(coupon1.getDiscountRate()).thenReturn(0.1);
        when(couponService.getCoupon(1L)).thenReturn(coupon1);

        CouponInfo.Coupon coupon2 = mock(CouponInfo.Coupon.class);
        when(coupon2.getName()).thenReturn("쿠폰2");
        when(coupon2.getDiscountRate()).thenReturn(0.2);
        when(couponService.getCoupon(2L)).thenReturn(coupon2);

        // when
        UserCouponResult.Coupons result = userCouponFacade.getUserCoupons(userId);

        // then
        assertThat(result).isNotNull();
        verify(userService, times(1)).getUser(userId);
        verify(userCouponService, times(1)).getUserCoupons(userId);
        verify(couponService, times(2)).getCoupon(any(Long.class));
    }

    @Test
    @DisplayName("사용자 쿠폰 목록 조회 - 사용자 없음")
    void getUserCoupons_userNotFound() {
        // given
        Long userId = 999L;
        doThrow(new IllegalArgumentException("User not found"))
            .when(userService).getUser(userId);

        // when & then
        assertThatThrownBy(() -> userCouponFacade.getUserCoupons(userId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("User not found");
        verify(userService, times(1)).getUser(userId);
        verify(userCouponService, never()).getUserCoupons(any());
        verify(couponService, never()).getCoupon(any(Long.class));
    }
} 