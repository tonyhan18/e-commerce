package kr.hhplus.be.server.domain.payment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentInfo.Payment pay(PaymentCommand.Payment command) {
        Payment payment = Payment.create(command.getOrderId(), command.getAmount());
        payment.pay();

        paymentRepository.save(payment);
        return PaymentInfo.Payment.of(payment.getId());
    }

    @Transactional
    public void cancelPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId);
        payment.cancel();
    }
}
