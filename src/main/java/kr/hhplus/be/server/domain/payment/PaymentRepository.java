package kr.hhplus.be.server.domain.payment;

public interface PaymentRepository {
    Payment save(Payment payment);
    Payment findById(Long id);
}
