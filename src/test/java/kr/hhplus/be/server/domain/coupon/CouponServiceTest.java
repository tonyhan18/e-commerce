package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.CouponInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

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
        when(couponRepository.findById(couponId)).thenReturn(coupon);

        // when
        couponService.publishCoupon(couponId);

        // then
        verify(couponRepository, times(1)).findById(couponId);
        assertThat(coupon.getQuantity()).isEqualTo(9); // 발급 후 수량이 1 감소
    }
} 