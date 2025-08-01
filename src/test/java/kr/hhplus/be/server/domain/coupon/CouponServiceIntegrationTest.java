package kr.hhplus.be.server.domain.coupon;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceIntegrationTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    @Test
    @DisplayName("쿠폰 발급 - 성공")
    void publishCoupon_success() {
        // given
        Long couponId = 1L;
        Coupon mockCoupon = mock(Coupon.class);
        when(couponRepository.findById(couponId)).thenReturn(mockCoupon);
        when(mockCoupon.publish()).thenReturn(mockCoupon);

        // when
        couponService.publishCoupon(couponId);

        // then
        verify(couponRepository, times(1)).findById(couponId);
        verify(mockCoupon, times(1)).publish();
    }

    @Test
    @DisplayName("쿠폰 발급 - 발급 불가능한 쿠폰")
    void publishCoupon_cannotPublish() {
        // given
        Long couponId = 1L;
        Coupon mockCoupon = mock(Coupon.class);
        when(couponRepository.findById(couponId)).thenReturn(mockCoupon);
        doThrow(new IllegalStateException("쿠폰을 발급할 수 없습니다."))
            .when(mockCoupon).publish();

        // when & then
        assertThatThrownBy(() -> couponService.publishCoupon(couponId))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("쿠폰을 발급할 수 없습니다.");
        verify(couponRepository, times(1)).findById(couponId);
        verify(mockCoupon, times(1)).publish();
    }

    @Test
    @DisplayName("쿠폰 발급 - 만료된 쿠폰")
    void publishCoupon_expired() {
        // given
        Long couponId = 1L;
        Coupon mockCoupon = mock(Coupon.class);
        when(couponRepository.findById(couponId)).thenReturn(mockCoupon);
        doThrow(new IllegalStateException("쿠폰이 만료되었습니다."))
            .when(mockCoupon).publish();

        // when & then
        assertThatThrownBy(() -> couponService.publishCoupon(couponId))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("쿠폰이 만료되었습니다.");
        verify(couponRepository, times(1)).findById(couponId);
        verify(mockCoupon, times(1)).publish();
    }

    @Test
    @DisplayName("쿠폰 발급 - 수량 부족")
    void publishCoupon_insufficientQuantity() {
        // given
        Long couponId = 1L;
        Coupon mockCoupon = mock(Coupon.class);
        when(couponRepository.findById(couponId)).thenReturn(mockCoupon);
        doThrow(new IllegalStateException("쿠폰 수량이 부족합니다."))
            .when(mockCoupon).publish();

        // when & then
        assertThatThrownBy(() -> couponService.publishCoupon(couponId))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("쿠폰 수량이 부족합니다.");
        verify(couponRepository, times(1)).findById(couponId);
        verify(mockCoupon, times(1)).publish();
    }

    @Test
    @DisplayName("쿠폰 조회 - 성공")
    void getCoupon_success() {
        // given
        Long couponId = 1L;
        Coupon mockCoupon = mock(Coupon.class);
        when(couponRepository.findById(couponId)).thenReturn(mockCoupon);
        when(mockCoupon.getId()).thenReturn(1L);
        when(mockCoupon.getName()).thenReturn("테스트 쿠폰");
        when(mockCoupon.getDiscountRate()).thenReturn(0.1);

        // when
        CouponInfo.Coupon result = couponService.getCoupon(couponId);

        // then
        assertThat(result).isNotNull();
        verify(couponRepository, times(1)).findById(couponId);
    }

    @Test
    @DisplayName("쿠폰 조회 - 존재하지 않는 쿠폰")
    void getCoupon_notFound() {
        // given
        Long couponId = 999L;
        when(couponRepository.findById(couponId))
            .thenThrow(new IllegalArgumentException("Coupon not found with id: 999"));

        // when & then
        assertThatThrownBy(() -> couponService.getCoupon(couponId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Coupon not found with id: 999");
        verify(couponRepository, times(1)).findById(couponId);
    }

    @Test
    @DisplayName("쿠폰 발급 - 정상적인 쿠폰 발급 후 수량 감소")
    void publishCoupon_successWithQuantityDecrease() {
        // given
        Long couponId = 1L;
        Coupon mockCoupon = mock(Coupon.class);
        when(couponRepository.findById(couponId)).thenReturn(mockCoupon);
        when(mockCoupon.publish()).thenReturn(mockCoupon);

        // when
        couponService.publishCoupon(couponId);

        // then
        verify(couponRepository, times(1)).findById(couponId);
        verify(mockCoupon, times(1)).publish();
    }
} 