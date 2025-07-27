package kr.hhplus.be.server.domain.payment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentCommand {
    @Getter
    public static class Payment {
        private final Long orderId;
        private final Long amount;
        private final Long userId;

        private Payment(Long orderId, Long amount, Long userId) {
            this.orderId = orderId;
            this.amount = amount;
            this.userId = userId;
        }

        public static Payment of(Long orderId, Long amount, Long userId) {
            return new Payment(orderId, amount, userId);
        }
    }
}
