package kr.hhplus.be.server.domain.payment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;
import kr.hhplus.be.server.support.MockTestSupport;

class PaymentTest extends MockTestSupport{
    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @DisplayName("결제를 생성하고 저장한다.")
    @Test
    void pay() {
        // given
        PaymentCommand.Payment command = PaymentCommand.Payment.of(1L, 1L, 1_000L);

        // when
        paymentService.pay(command);

        // then
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }
} 