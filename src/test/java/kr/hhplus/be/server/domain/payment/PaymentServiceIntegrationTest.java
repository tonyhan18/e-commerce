package kr.hhplus.be.server.domain.payment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.support.IntegrationTestSupport;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class PaymentServiceIntegrationTest extends IntegrationTestSupport{ 

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @DisplayName("주문을 결제 한다.")
    @Test
    void pay() {
        // given
        PaymentCommand.Payment command =  PaymentCommand.Payment.of(1L, 1L, 100_000L);

        // when
        PaymentInfo.Payment result = paymentService.pay(command);

        // then
        Payment payment = paymentRepository.findById(result.getPaymentId()).orElseThrow();
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }
} 