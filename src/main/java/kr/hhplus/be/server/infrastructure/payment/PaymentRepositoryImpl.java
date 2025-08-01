package kr.hhplus.be.server.infrastructure.payment;

import kr.hhplus.be.server.domain.payment.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment save(Payment payment) {
        return paymentJpaRepository.save(payment);
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return paymentJpaRepository.findById(id);
    }

    @Override
    public List<Payment> findCompletedPaymentsWithIn(List<PaymentStatus> statuses, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return paymentJpaRepository.findByPaymentStatusInAndPaidAtBetween(statuses, startDateTime, endDateTime);
    }
}   