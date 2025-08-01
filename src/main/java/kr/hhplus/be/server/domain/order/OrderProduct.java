package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Getter
@Entity 
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "order_product", indexes = {
    @Index(name = "idx_order_id", columnList = "order_id")
})
public class OrderProduct {
    @Id
    @Column(name = "order_product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private Long productId;

    private String productName;

    private long unitPrice;

    private int quantity;

    @Builder
    private OrderProduct(Long id, Order order, Long productId, String productName, long unitPrice, int quantity) {
        this.id = id;
        this.order = order;
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public static OrderProduct create(Long productId, String productName, long unitPrice, int quantity) {
        return OrderProduct.builder()
            .productId(productId)
            .productName(productName)
            .unitPrice(unitPrice)
            .quantity(quantity)
            .build();
    }

    public long getPrice() {
        return this.unitPrice * this.quantity;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
