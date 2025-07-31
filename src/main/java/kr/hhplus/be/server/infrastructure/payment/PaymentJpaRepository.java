package kr.hhplus.be.server.infrastructure.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import kr.hhplus.be.server.domain.payment.PaymentStatus;
import kr.hhplus.be.server.domain.payment.Payment;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByPaymentStatusInAndPaidAtBetween(Collection<PaymentStatus> paymentStatuses, LocalDateTime paidAtStart, LocalDateTime paidAtEnd);
}
