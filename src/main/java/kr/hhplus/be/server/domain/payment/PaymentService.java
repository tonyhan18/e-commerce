package kr.hhplus.be.server.domain.payment;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentInfo.Payment pay(PaymentCommand.Payment command) {
        Payment payment = Payment.create(command.getOrderId(), command.getAmount());
        payment.pay();
        paymentRepository.save(payment);

        return PaymentInfo.Payment.of(payment.getId());
    }
}
