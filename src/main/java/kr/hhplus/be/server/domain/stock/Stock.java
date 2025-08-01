package kr.hhplus.be.server.domain.stock;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {
    @Id
    @Column(name = "stock_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private int quantity;

    @Builder
    private Stock(Long id, Long productId, int quantity) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
    }

    public static Stock create(Long productId, int quantity) {
        return Stock.builder()
            .productId(productId)
            .quantity(quantity)
            .build();
    }

    public void deductQuantity(int quantity) {
        if (this.quantity < quantity) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
        this.quantity -= quantity;
    }

    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }

    public boolean isEmpty() {
        return this.quantity == 0;
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }
    
    private static void validateQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("재고는 0보다 작을 수 없습니다.");
        }
    }
}
