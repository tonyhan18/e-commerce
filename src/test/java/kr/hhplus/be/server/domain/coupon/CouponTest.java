package kr.hhplus.be.server.domain.coupon;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class CouponTest {

    @Test
    @DisplayName("쿠폰을 생성할 수 있다.")
    void createCoupon() {
        // given
        String name = "테스트 쿠폰";
        double discountRate = 0.1;
        int quantity = 100;
        CouponStatus status = CouponStatus.PUBLISHABLE;
        LocalDateTime expiredAt = LocalDateTime.now().plusDays(30);

        // when
        Coupon coupon = Coupon.create(name, discountRate, quantity, status, expiredAt);

        // then
        assertThat(coupon.getName()).isEqualTo(name);
        assertThat(coupon.getDiscountRate()).isEqualTo(discountRate);
        assertThat(coupon.getQuantity()).isEqualTo(quantity);
        assertThat(coupon.getStatus()).isEqualTo(status);
        assertThat(coupon.getExpiredAt()).isEqualTo(expiredAt);
        assertThat(coupon.getId()).isNull();
    }

    @Test
    @DisplayName("쿠폰 이름이 null이면 예외가 발생한다.")
    void createCouponWithNullName() {
        // given
        String name = null;
        double discountRate = 0.1;
        int quantity = 100;
        CouponStatus status = CouponStatus.PUBLISHABLE;
        LocalDateTime expiredAt = LocalDateTime.now().plusDays(30);

        // when & then
        assertThatThrownBy(() -> Coupon.create(name, discountRate, quantity, status, expiredAt))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("쿠폰 이름은 필수입니다.");
    }

    @Test
    @DisplayName("쿠폰 이름이 빈 문자열이면 예외가 발생한다.")
    void createCouponWithEmptyName() {
        // given
        String name = "";
        double discountRate = 0.1;
        int quantity = 100;
        CouponStatus status = CouponStatus.PUBLISHABLE;
        LocalDateTime expiredAt = LocalDateTime.now().plusDays(30);

        // when & then
        assertThatThrownBy(() -> Coupon.create(name, discountRate, quantity, status, expiredAt))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("쿠폰 이름은 필수입니다.");
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.1, 1.1, 2.0})
    @DisplayName("할인율이 0 미만이거나 1 초과이면 예외가 발생한다.")
    void createCouponWithInvalidDiscountRate(double invalidDiscountRate) {
        // given
        String name = "테스트 쿠폰";
        int quantity = 100;
        CouponStatus status = CouponStatus.PUBLISHABLE;
        LocalDateTime expiredAt = LocalDateTime.now().plusDays(30);

        // when & then
        assertThatThrownBy(() -> Coupon.create(name, invalidDiscountRate, quantity, status, expiredAt))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("쿠폰 할인율이 올바르지 않습니다.");
    }

    @Test
    @DisplayName("쿠폰 수량이 음수이면 예외가 발생한다.")
    void createCouponWithNegativeQuantity() {
        // given
        String name = "테스트 쿠폰";
        double discountRate = 0.1;
        int quantity = -1;
        CouponStatus status = CouponStatus.PUBLISHABLE;
        LocalDateTime expiredAt = LocalDateTime.now().plusDays(30);

        // when & then
        assertThatThrownBy(() -> Coupon.create(name, discountRate, quantity, status, expiredAt))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("쿠폰 수량은 0 이상이어야 합니다.");
    }

    @Test
    @DisplayName("쿠폰 상태가 null이면 예외가 발생한다.")
    void createCouponWithNullStatus() {
        // given
        String name = "테스트 쿠폰";
        double discountRate = 0.1;
        int quantity = 100;
        CouponStatus status = null;
        LocalDateTime expiredAt = LocalDateTime.now().plusDays(30);

        // when & then
        assertThatThrownBy(() -> Coupon.create(name, discountRate, quantity, status, expiredAt))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("쿠폰 상태는 필수입니다.");
    }

    @Test
    @DisplayName("쿠폰 만료일이 현재 시간보다 이전이면 예외가 발생한다.")
    void createCouponWithExpiredDate() {
        // given
        String name = "테스트 쿠폰";
        double discountRate = 0.1;
        int quantity = 100;
        CouponStatus status = CouponStatus.PUBLISHABLE;
        LocalDateTime expiredAt = LocalDateTime.now().minusDays(1);

        // when & then
        assertThatThrownBy(() -> Coupon.create(name, discountRate, quantity, status, expiredAt))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("쿠폰 만료일은 현재 시간 이후여야 합니다.");
    }

    @Test
    @DisplayName("쿠폰 만료일이 null이면 예외가 발생한다.")
    void createCouponWithNullExpiredAt() {
        // given
        String name = "테스트 쿠폰";
        double discountRate = 0.1;
        int quantity = 100;
        CouponStatus status = CouponStatus.PUBLISHABLE;
        LocalDateTime expiredAt = null;

        // when & then
        assertThatThrownBy(() -> Coupon.create(name, discountRate, quantity, status, expiredAt))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("쿠폰 만료일은 현재 시간 이후여야 합니다.");
    }

    @Test
    @DisplayName("쿠폰을 발급할 수 있다.")
    void publishCoupon() {
        // given
        Coupon coupon = Coupon.create("테스트 쿠폰", 0.1, 10, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(30));
        int originalQuantity = coupon.getQuantity();

        // when
        Coupon publishedCoupon = coupon.publish();

        // then
        assertThat(publishedCoupon.getQuantity()).isEqualTo(originalQuantity - 1);
        assertThat(publishedCoupon).isEqualTo(coupon); // 같은 객체 반환
    }

    @Test
    @DisplayName("쿠폰 수량이 0이면 발급할 수 없다.")
    void publishCouponWithZeroQuantity() {
        // given
        Coupon coupon = Coupon.create("테스트 쿠폰", 0.1, 0, CouponStatus.PUBLISHABLE, LocalDateTime.now().plusDays(30));

        // when & then
        assertThatThrownBy(coupon::publish)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("쿠폰 수량이 부족합니다.");
    }

    @Test
    @DisplayName("만료된 쿠폰은 발급할 수 없다.")
    void publishExpiredCoupon() {
        // given
        Coupon coupon = Coupon.create("테스트 쿠폰", 0.1, 10, CouponStatus.PUBLISHABLE, LocalDateTime.now().minusDays(1));

        // when & then
        assertThatThrownBy(coupon::publish)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("쿠폰이 만료되었습니다.");
    }

    @Test
    @DisplayName("빌더로 쿠폰을 생성할 수 있다.")
    void createCouponWithBuilder() {
        // given
        Long id = 1L;
        String name = "빌더 쿠폰";
        double discountRate = 0.2;
        int quantity = 50;
        CouponStatus status = CouponStatus.REGISTERED;
        LocalDateTime expiredAt = LocalDateTime.now().plusDays(60);

        // when
        Coupon coupon = Coupon.builder()
                .id(id)
                .name(name)
                .discountRate(discountRate)
                .quantity(quantity)
                .status(status)
                .expiredAt(expiredAt)
                .build();

        // then
        assertThat(coupon.getId()).isEqualTo(id);
        assertThat(coupon.getName()).isEqualTo(name);
        assertThat(coupon.getDiscountRate()).isEqualTo(discountRate);
        assertThat(coupon.getQuantity()).isEqualTo(quantity);
        assertThat(coupon.getStatus()).isEqualTo(status);
        assertThat(coupon.getExpiredAt()).isEqualTo(expiredAt);
    }
} 