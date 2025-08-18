package kr.hhplus.be.server.domain.payment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentInfo {
    @Getter
    public static class Payment {
        private final Long paymentId;
        
        private Payment(Long paymentId) {
            this.paymentId = paymentId;
        }

        public static Payment of(Long paymentId) {
            return new Payment(paymentId);
        }
    }
}
