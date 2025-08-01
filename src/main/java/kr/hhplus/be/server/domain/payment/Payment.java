package kr.hhplus.be.server.domain.payment;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {
    @Id
    @Column(name = "payment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private Long amount;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private LocalDateTime paidAt;

    @Builder    
    private Payment(
        Long id,
        Long orderId, 
        Long amount, 
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        LocalDateTime paidAt
    ) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.paidAt = paidAt;
    }

    public static Payment create(Long orderId, Long amount) {
        validateAmount(amount);

        return Payment.builder()
            .orderId(orderId)
            .amount(amount)
            .paymentMethod(PaymentMethod.UNKNOWN) // 
            .paymentStatus(PaymentStatus.READY) // 초기 상태는 결제 대기
            .build();
    }

    public void pay() {
        if (this.paymentStatus.cannotPayable()) {
            throw new IllegalStateException("결제 불가능한 상태입니다.");
        }
        this.paymentStatus = PaymentStatus.COMPLETED;
        this.paidAt = LocalDateTime.now();
    }

    private static void validateAmount(Long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("결제 금액은 0원 이상이어야 합니다.");
        }
    }
}
