package kr.hhplus.be.server.infrastructure.payment.repository;

import java.util.Optional;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment save(Payment payment) {
        return paymentJpaRepository.save(payment);
    }

    @Override
    public Payment findById(Long id) {
        return paymentJpaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));
    }
}   