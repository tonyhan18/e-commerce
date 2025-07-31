package kr.hhplus.be.server.domain.payment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(Long id);
    List<Payment> findCompletedPaymentsWithin(List<PaymentStatus> statuses, LocalDateTime startDateTime, LocalDateTime endDateTime)
}
