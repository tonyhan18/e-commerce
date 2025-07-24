package kr.hhplus.be.server.infrastructure.payment;

import kr.hhplus.be.server.domain.payment.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class PaymentRepositoryImpl implements PaymentRepository {
    @Override
    public Payment save(Payment payment) {
        // TODO: 실제 DB 연동 로직 구현
        return payment;
    }

    @Override
    public List<Payment> findCompletedPaymentsWithin(List<PaymentStatus> paymentStatuses, LocalDateTime startDate, LocalDateTime endDate) {
        // TODO: 실제 DB 연동 로직 구현
        return null;
    }
} 