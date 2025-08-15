package kr.hhplus.be.server.domain.order;

import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import org.aspectj.weaver.ast.Or;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.order.OrderStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders", indexes = {
    @Index(name = "idx_order_status_paid_at", columnList = "order_status, paid_at")
})
public class Order {
    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long userCouponId;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private long totalPrice;

    private long discountPrice;

    private LocalDateTime paidAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderProduct> orderProducts = new ArrayList<>();
    
    @Builder
    private Order(Long userId, Long userCouponId, double discountRate, List<OrderProduct> orderProducts) {
        this.userId = userId;
        this.userCouponId = userCouponId;
        this.orderStatus = OrderStatus.CREATED;

        orderProducts.forEach(this::addOrderProduct);
        long calculatedTotalPrice = calculateTotalPrice(orderProducts);
        long calculatedDiscountPrice = calculateDiscountPrice(calculatedTotalPrice, discountRate);

        this.totalPrice = calculatedTotalPrice - calculatedDiscountPrice;
        this.discountPrice = calculatedDiscountPrice;
    }

    public static Order create(Long userId, Long userCouponId, double discountRate, List<OrderProduct> orderProducts) {
        validateOrderProducts(orderProducts);
        return Order.builder()
                .userId(userId)
                .orderProducts(orderProducts)
                .userCouponId(userCouponId)
                .discountRate(discountRate)
                .build();
    }

    public void paid(LocalDateTime paidAt) {
        this.orderStatus = OrderStatus.PAID;
        this.paidAt = paidAt;
    }

    private long calculateTotalPrice(List<OrderProduct> orderProducts) {
        return orderProducts.stream().mapToLong(OrderProduct::getPrice).sum();
    }

    private long calculateDiscountPrice(long totalPrice, double discountRate) {
        return (long) (totalPrice * discountRate);
    }

    private void addOrderProduct(OrderProduct orderProduct) {
        this.orderProducts.add(orderProduct);
        orderProduct.setOrder(this);
    }

    private static void validateOrderProducts(List<OrderProduct> orderProducts) {
        if (orderProducts == null || orderProducts.isEmpty()) {
            throw new IllegalArgumentException("주문 상품이 없습니다.");
        }
    }

}
