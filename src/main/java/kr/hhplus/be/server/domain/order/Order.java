package kr.hhplus.be.server.domain.order;

import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.order.OrderStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order {
    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long userCouponId;

    private OrderStatus orderStatus;

    private long totalPrice;

    private long discountPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderProduct> orderProducts = new ArrayList<>();
    
    private Order(Long userId, Long userCouponId, List<OrderProduct> orderProducts, double discountRate) {
        this.userId = userId;
        this.userCouponId = userCouponId;
        this.orderStatus = OrderStatus.CREATED;

        orderProducts.forEach(this::addOrderProduct);
        long calculatedTotalPrice = calculateTotalPrice(orderProducts);
        long calculatedDiscountPrice = calculateDiscountPrice(calculatedTotalPrice, discountRate);

        this.totalPrice = calculatedTotalPrice;
        this.discountPrice = calculatedDiscountPrice;
    }

    public static Order create(Long userId, Long userCouponId, List<OrderProduct> orderProducts, double discountRate) {
        validateOrderProducts(orderProducts);
        return new Order(userId, userCouponId, orderProducts, discountRate);
    }

    public void paid() {
        this.orderStatus = OrderStatus.PAID;
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
