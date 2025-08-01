package kr.hhplus.be.server.domain.payment;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentInfo {
    @Getter
    public static class Orders {
        private final List<Long> orderIds;

        private Orders(List<Long> orderIds) {
            this.orderIds = orderIds;
        }

        public static Orders of(List<Long> orderIds) {
            return new Orders(orderIds);
        }
    }

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
