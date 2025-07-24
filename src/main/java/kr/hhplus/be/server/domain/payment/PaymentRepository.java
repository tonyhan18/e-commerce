package kr.hhplus.be.server.domain.payment;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository {
    Payment save(Payment payment);

    List<Payment> findCompletedPaymentsWithin(List<PaymentStatus> paymentStatuses, LocalDateTime startDate, LocalDateTime endDate);
}
