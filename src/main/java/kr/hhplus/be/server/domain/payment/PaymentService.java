// package kr.hhplus.be.server.domain.payment;

// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.stream.Collectors;

// import org.springframework.stereotype.Service;

// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class PaymentService {
//     private final PaymentRepository paymentRepository;

//     public void pay(PaymentCommand.Payment payment) {
//         Payment payment = Payment.create(payment.getOrderId(), payment.getAmount());
//         payment.pay();
//         paymentRepository.save(payment);
//     }

//     public PaymentInfo.Orders getOrders(LocalDateTime startDate, LocalDateTime endDate) {
//         List<Payment> payments = paymentRepository.findCompletedPaymentsWithin(PaymentStatus.forCompleted(), startDate, endDate);
//         return PaymentInfo.Orders.of(payments.stream().map(Payment::getOrderId).collect(Collectors.toList()));
//     }
// }
