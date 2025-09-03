package kr.hhplus.be.server.domain.order;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Builder;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderInfo {
    @Getter
    public static class Order {

        private final Long orderId;
        private final long totalPrice;
        private final long discountPrice;

        private Order(Long orderId, long totalPrice, long discountPrice) {
            this.orderId = orderId;
            this.totalPrice = totalPrice;
            this.discountPrice = discountPrice;
        }

        public static Order of(Long orderId, long totalPrice, long discountPrice) {
            return new Order(orderId, totalPrice, discountPrice);
        }

        public static Order of(kr.hhplus.be.ecommerce.domain.order.Order order) {
            return new Order(order.getId(), order.getTotalPrice(), order.getDiscountPrice());
        }
    }

    public static class User {

        private final Long userId;
        private final String userName;

        private User(Long userId, String userName) {
            this.userId = userId;
            this.userName = userName;
        }

        public static User of(Long userId, String userName) {
            return new User(userId, userName);
        }
    }

    @Getter
    public static class Product {

        private final Long id;
        private final String name;
        private final long price;
        private final int quantity;

        private Product(Long id, String name, long price, int quantity) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }

        public static Product of(Long id, String name, long price, int quantity) {
            return new Product(id, name, price, quantity);
        }
    }

    @Getter
    public static class Coupon {

        private final Long userCouponId;
        private final Long couponId;
        private final String couponName;
        private final double discountRate;
        private final LocalDateTime issuedAt;

        @Builder
        private Coupon(Long userCouponId, Long couponId, String couponName, double discountRate, LocalDateTime issuedAt) {
            this.userCouponId = userCouponId;
            this.couponId = couponId;
            this.couponName = couponName;
            this.discountRate = discountRate;
            this.issuedAt = issuedAt;
        }

        public static Coupon of(Long userCouponId, Long couponId, String couponName, double discountRate, LocalDateTime issuedAt) {
            return Coupon.builder()
                .userCouponId(userCouponId)
                .couponId(couponId)
                .couponName(couponName)
                .discountRate(discountRate)
                .issuedAt(issuedAt)
                .build();
        }
    }

    @Getter
    public static class Completed {

        private final Long orderId;
        private final Long userId;
        private final Long userCouponId;
        private final OrderStatus orderStatus;
        private final long totalPrice;
        private final long discountPrice;
        private final LocalDateTime completedAt;

        @Builder
        private Completed(Long orderId,
                          Long userId,
                          Long userCouponId,
                          OrderStatus orderStatus,
                          long totalPrice,
                          long discountPrice,
                          LocalDateTime completedAt) {
            this.orderId = orderId;
            this.userId = userId;
            this.userCouponId = userCouponId;
            this.orderStatus = orderStatus;
            this.totalPrice = totalPrice;
            this.discountPrice = discountPrice;
            this.completedAt = completedAt;
        }

        public static Completed of(kr.hhplus.be.ecommerce.domain.order.Order order) {
            return Completed.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .userCouponId(order.getUserCouponId())
                .orderStatus(order.getOrderStatus())
                .totalPrice(order.getTotalPrice())
                .discountPrice(order.getDiscountPrice())
                .completedAt(order.getCompletedAt())
                .build();
        }
    }
}
