package kr.hhplus.be.server.domain.coupon;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponRepositoryTest {

    @Mock
    private CouponRepository couponRepository;

    private Coupon testCoupon;

    @BeforeEach
    void setUp() {
        testCoupon = Coupon.builder()
            .id(1L)
            .name("테스트 쿠폰")
            .discountRate(10.0)
            .quantity(100)
            .status(CouponStatus.PUBLISHABLE)
            .expiredAt(LocalDateTime.now().plusDays(30))
            .build();
    }

    @Test
    @DisplayName("쿠폰 조회 - 성공")
    void findById_success() {
        // given
        Long couponId = 1L;
        when(couponRepository.findById(couponId)).thenReturn(testCoupon);

        // when
        Coupon result = couponRepository.findById(couponId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("테스트 쿠폰");
        verify(couponRepository, times(1)).findById(couponId);
    }

    @Test
    @DisplayName("쿠폰 조회 - 존재하지 않는 쿠폰")
    void findById_notFound() {
        // given
        Long couponId = 999L;
        when(couponRepository.findById(couponId))
            .thenThrow(new IllegalArgumentException("Coupon not found with id: 999"));

        // when & then
        assertThat(couponRepository.findById(couponId))
            .isNull();
        verify(couponRepository, times(1)).findById(couponId);
    }
} 