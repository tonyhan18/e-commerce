package kr.hhplus.be.server.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserCouponTest {

    @Test
    @DisplayName("사용자 쿠폰 생성 - 성공")
    void create_success() {
        // given
        Long userId = 1L;
        Long couponId = 1L;

        // when
        UserCoupon userCoupon = UserCoupon.create(userId, couponId);

        // then
        assertThat(userCoupon.getUserId()).isEqualTo(userId);
        assertThat(userCoupon.getCouponId()).isEqualTo(couponId);
        assertThat(userCoupon.getUsedStatus()).isEqualTo(UserCouponUsedStatus.UNUSED);
        assertThat(userCoupon.getIssuedAt()).isNotNull();
        assertThat(userCoupon.getUsedAt()).isNull();
    }

    @Test
    @DisplayName("쿠폰 사용 - 성공")
    void use_success() {
        // given
        UserCoupon userCoupon = UserCoupon.create(1L, 1L);

        // when
        userCoupon.use();

        // then
        assertThat(userCoupon.getUsedStatus()).isEqualTo(UserCouponUsedStatus.USED);
        assertThat(userCoupon.getUsedAt()).isNotNull();
    }

    @Test
    @DisplayName("쿠폰 사용 - 이미 사용된 쿠폰")
    void use_alreadyUsed() {
        // given
        UserCoupon userCoupon = UserCoupon.create(1L, 1L);
        userCoupon.use(); // 첫 번째 사용

        // when & then
        assertThatThrownBy(() -> userCoupon.use())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("사용할 수 없는 쿠폰입니다.");
    }

    @Test
    @DisplayName("사용 불가능 여부 확인 - 사용 가능한 쿠폰")
    void cannotUse_unused() {
        // given
        UserCoupon userCoupon = UserCoupon.create(1L, 1L);

        // when
        boolean cannotUse = userCoupon.cannotUse();

        // then
        assertThat(cannotUse).isFalse();
    }

    @Test
    @DisplayName("사용 불가능 여부 확인 - 사용된 쿠폰")
    void cannotUse_used() {
        // given
        UserCoupon userCoupon = UserCoupon.create(1L, 1L);
        userCoupon.use();

        // when
        boolean cannotUse = userCoupon.cannotUse();

        // then
        assertThat(cannotUse).isTrue();
    }
} 