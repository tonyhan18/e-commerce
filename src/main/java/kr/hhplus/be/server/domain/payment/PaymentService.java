package kr.hhplus.be.server.domain.payment;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public void pay(PaymentCommand.Payment command) {
        Payment payment = Payment.create(command.getOrderId(), command.getAmount());
        payment.pay();
        paymentRepository.save(payment);
    }

    public PaymentInfo.Orders getCompletedOrdersBetweenDays(int recentDays) {
        LocalDateTime endDateTime = LocalDateTime.now();
        LocalDateTime startDateTime = endDateTime.minusDays(recentDays);

        List<Payment> completedPayments = paymentRepository
            .findCompletedPaymentsWithin(PaymentStatus.forCompleted(), startDateTime, endDateTime);

        return PaymentInfo.Orders.of(completedPayments.stream()
            .map(Payment::getOrderId)
            .toList());
    }
}
