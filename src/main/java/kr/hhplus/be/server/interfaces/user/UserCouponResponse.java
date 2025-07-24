package kr.hhplus.be.server.interfaces.user;

import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

import kr.hhplus.be.server.application.user.UserCouponResult;
import lombok.AccessLevel;
import lombok.Getter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserCouponResponse {
    
    @Getter
    @NoArgsConstructor
    public static class Coupons {
        private List<Coupon> coupons;

        @Builder
        private Coupons(java.util.List<Coupon> coupons) {
            this.coupons = coupons;
        }

        public static Coupons of(UserCouponResult.Coupons coupons) {
            return new Coupons(coupons.getCoupons().stream()
                .map(Coupon::of)
                .toList());
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Coupon {
        private Long id;
        private String name;
        private Double discountRate;

        @Builder
        private Coupon(Long id, String name, Double discountRate) {
            this.id = id;
            this.name = name;
            this.discountRate = discountRate;
        }

        public static Coupon of(UserCouponResult.Coupon coupon) {
            return Coupon.builder()
                .id(coupon.getUserCouponId())
                .name(coupon.getCouponName())
                .discountRate(coupon.getDiscountRate())
                .build();
        }
    }
}
