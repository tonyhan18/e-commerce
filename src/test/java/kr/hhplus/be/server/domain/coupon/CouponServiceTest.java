package kr.hhplus.be.server.domain.coupon;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import kr.hhplus.be.server.support.MockTestSupport;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CouponServiceTest  extends MockTestSupport{

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    @Test
    @DisplayName("쿠폰을 발급할 수 있다.")
    void publishCoupon() {
        // given
        Long couponId = 1L;
        Coupon coupon = Coupon.builder()
                .id(couponId)
                .name("테스트 쿠폰")
                .discountRate(0.1)
                .quantity(10)
                .status(CouponStatus.PUBLISHABLE)
                .expiredAt(LocalDateTime.now().plusDays(1))
                .build();
        when(couponRepository.findWithLockById(couponId)).thenReturn(coupon);

        // when
        couponService.publishCoupon(couponId);

        // then
        verify(couponRepository, times(1)).findWithLockById(couponId);
        assertThat(coupon.getQuantity()).isEqualTo(9); // 발급 후 수량이 1 감소
    }

    @DisplayName("유효한 ID로 쿠폰을 발급해야 한다.")
    @Test
    void publishCouponWithInvalidId() {
        // given
        when(couponRepository.findWithLockById(anyLong()))
            .thenThrow(new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));

        // when & then
        assertThatThrownBy(() -> couponService.publishCoupon(anyLong()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("쿠폰을 찾을 수 없습니다.");
    }

    @DisplayName("쿠폰 발급 가능할 때, 쿠폰을 발급할 수 있다.")
    @Test
    void publishCouponWithCannotPublishable() {
        // given
        Coupon coupon = Coupon.builder()
            .name("쿠폰명")
            .status(CouponStatus.REGISTERED)
            .expiredAt(LocalDateTime.now().plusDays(1))
            .build();

        when(couponRepository.findWithLockById(anyLong()))
            .thenReturn(coupon);

        // when & then
        assertThatThrownBy(() -> couponService.publishCoupon(anyLong()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("쿠폰을 발급할 수 없습니다.");
    }

    @DisplayName("쿠폰 만료 기간이 지나지 않았을 때, 쿠폰을 발급할 수 있다.")
    @Test
    void publishCouponWithExpired() {
        // given
        Coupon coupon = Coupon.builder()
            .name("쿠폰명")
            .status(CouponStatus.PUBLISHABLE)
            .expiredAt(LocalDateTime.now().minusDays(1))
            .build();

        when(couponRepository.findWithLockById(anyLong()))
            .thenReturn(coupon);

        // when & then
        assertThatThrownBy(() -> couponService.publishCoupon(anyLong()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("쿠폰이 만료되었습니다.");
    }

    @DisplayName("쿠폰 수량이 충분할 시, 쿠폰을 발급할 수 있다.")
    @Test
    void publishCouponWithInsufficientQuantity() {
        // given
        Coupon coupon = Coupon.builder()
            .name("쿠폰명")
            .status(CouponStatus.PUBLISHABLE)
            .expiredAt(LocalDateTime.now().plusDays(1))
            .quantity(0)
            .build();

        when(couponRepository.findWithLockById(anyLong()))
            .thenReturn(coupon);

        // when & then
        assertThatThrownBy(() -> couponService.publishCoupon(anyLong()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("쿠폰 수량이 부족합니다.");
    }
} 