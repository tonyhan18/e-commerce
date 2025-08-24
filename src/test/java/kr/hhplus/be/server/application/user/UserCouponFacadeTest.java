package kr.hhplus.be.server.application.user;

import kr.hhplus.be.server.domain.coupon.CouponInfo;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.user.UserCouponInfo;
import kr.hhplus.be.server.domain.user.UserCouponService;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.support.MockTestSupport;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.InOrder;
    
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.*;

class UserCouponFacadeTest extends MockTestSupport{

    @InjectMocks
    private UserCouponFacade userCouponFacade;

    @Mock
    private UserService userService;

    @Mock
    private CouponService couponService;

    @Mock
    private UserCouponService userCouponService;

    @DisplayName("사용자 쿠폰을 발급을 요청한다.")
    @Test
    void requestPublishUserCoupon() {
        // given
        UserCouponCriteria.PublishRequest criteria = mock(UserCouponCriteria.PublishRequest.class);

        // when
        userCouponFacade.requestPublishUserCoupon(criteria);

        // then
        InOrder inOrder = inOrder(userCouponService);
        inOrder.verify(userCouponService, times(1)).requestPublishUserCoupon(criteria.toCommand(any(LocalDateTime.class)));
    }

    @DisplayName("사용자 쿠폰을 발급한다.")
    @Test
    void publishUserCoupons() {
        // given
        UserCouponCriteria.Publish criteria = mock(UserCouponCriteria.Publish.class);
        CouponInfo.PublishableCoupons coupons = mock(CouponInfo.PublishableCoupons.class);

        when(coupons.getCoupons())
            .thenReturn(
                List.of(
                    CouponInfo.PublishableCoupon.builder()
                        .couponId(1L)
                        .quantity(10)
                        .build(),
                    CouponInfo.PublishableCoupon.builder()
                        .couponId(2L)
                        .quantity(20)
                        .build()
                )
            );

        when(couponService.getPublishableCoupons())
            .thenReturn(coupons);

        // when
        userCouponFacade.publishUserCoupons(criteria);

        // then
        InOrder inOrder = inOrder(couponService, userCouponService);
        inOrder.verify(couponService, times(1)).getPublishableCoupons();
        inOrder.verify(userCouponService, times(2)).publishUserCoupons(any());
    }

    @DisplayName("사용자 쿠폰 발급을 완료한다.")
    @Test
    void finishedPublishCoupons() {
        // given
        CouponInfo.PublishableCoupons coupons = mock(CouponInfo.PublishableCoupons.class);

        when(coupons.getCoupons())
            .thenReturn(
                List.of(
                    CouponInfo.PublishableCoupon.builder()
                        .couponId(1L)
                        .quantity(10)
                        .build(),
                    CouponInfo.PublishableCoupon.builder()
                        .couponId(2L)
                        .quantity(20)
                        .build()
                )
            );

        when(couponService.getPublishableCoupons())
            .thenReturn(coupons);

        when(userCouponService.isPublishFinished(any()))
            .thenReturn(true)
            .thenReturn(false);

        // when
        userCouponFacade.finishedPublishCoupons();

        // then
        InOrder inOrder = inOrder(couponService, userCouponService, couponService);
        inOrder.verify(couponService, times(1)).getPublishableCoupons();
        inOrder.verify(userCouponService, times(2)).isPublishFinished(any());
        verify(couponService, times(1)).finishCoupon(eq(1L));
    }

    @DisplayName("보유 쿠폰 목록을 조회한다.")
    @Test
    void getUserCoupons() {
        // given
        UserCouponInfo.Coupons coupons = mock(UserCouponInfo.Coupons.class);

        when(coupons.getCoupons())
            .thenReturn(
                List.of(
                    UserCouponInfo.Coupon.builder()
                        .userCouponId(1L)
                        .couponId(1L)
                        .issuedAt(LocalDateTime.of(2025, 4, 1, 12, 0, 0))
                        .build(),
                    UserCouponInfo.Coupon.builder()
                        .userCouponId(2L)
                        .couponId(2L)
                        .issuedAt(LocalDateTime.of(2025, 4, 10, 12, 0, 0))
                        .build()
                )
            );

        when(userCouponService.getUserCoupons(anyLong()))
            .thenReturn(coupons);

        when(couponService.getCoupon(anyLong()))
            .thenReturn(CouponInfo.Coupon.builder()
                .name("10% 쿠폰명")
                .discountRate(0.1)
                .build()
            );

        // when
        UserCouponResult.Coupons result = userCouponFacade.getUserCoupons(1L);

        // then
        InOrder inOrder = inOrder(userService, userCouponService, couponService);
        inOrder.verify(userService, times(1)).getUser(anyLong());
        inOrder.verify(userCouponService, times(1)).getUserCoupons(anyLong());
        inOrder.verify(couponService, times(2)).getCoupon(anyLong());

        assertThat(result.getCoupons())
            .extracting("userCouponId", "couponName", "discountRate")
            .containsExactlyInAnyOrder(
                tuple(1L, "10% 쿠폰명", 0.1),
                tuple(2L, "10% 쿠폰명", 0.1)
            );
    }
} 