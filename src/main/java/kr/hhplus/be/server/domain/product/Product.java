package kr.hhplus.be.server.domain.product;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "product", indexes = {
    @Index(name = "idx_product_status", columnList = "status"),
    @Index(name = "idx_product_price", columnList = "price"),
    @Index(name = "idx_product_status_price", columnList = "status,price")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "price", nullable = false)
    private Long price;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProductSellingStatus sellStatus;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;



    public static Product create(String name, long price, ProductSellingStatus sellStatus) {
        validateName(name);
        validatePrice(price);
        validateSellStatus(sellStatus);

        return Product.builder()
            .name(name)
            .price(price)
            .sellStatus(sellStatus)
            .build();
    }

    public boolean cannotSelling() {
        return sellStatus.cannotSelling();
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("상품 이름은 필수입니다.");
        }
    }

    private static void validatePrice(long price) {
        if (price <= 0) {
            throw new IllegalArgumentException("상품 가격은 0보다 커야 합니다.");
        }
    }

    private static void validateSellStatus(ProductSellingStatus sellStatus) {
        if (sellStatus == null) {
            throw new IllegalArgumentException("상품 판매 상태는 필수입니다.");
        }
    }

}