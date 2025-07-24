package kr.hhplus.be.server.domain.payment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    private PaymentCommand.Payment paymentCommand;

    @BeforeEach
    void setUp() {
        paymentCommand = PaymentCommand.Payment.of(1L, 10000L, null);
    }

    @Test
    @DisplayName("pay() 호출 시 Payment가 생성되고 저장된다.")
    void pay() {
        // given
        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);

        // when
        paymentService.pay(paymentCommand);

        // then
        verify(paymentRepository, times(1)).save(captor.capture());
        Payment saved = captor.getValue();
        assertThat(saved.getOrderId()).isEqualTo(1L);
        assertThat(saved.getAmount()).isEqualTo(10000L);
        assertThat(saved.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(saved.getPaidAt()).isNotNull();
    }

    @Test
    @DisplayName("getCompletedOrdersBetweenDays()는 기간 내 결제완료된 주문ID 목록을 반환한다.")
    void getCompletedOrdersBetweenDays() {
        // given
        LocalDateTime now = LocalDateTime.now();
        List<Payment> completedPayments = Arrays.asList(
                Payment.builder().orderId(1L).amount(10000L).paymentStatus(PaymentStatus.COMPLETED)
                        .paidAt(now.minusDays(1)).build(),
                Payment.builder().orderId(2L).amount(20000L).paymentStatus(PaymentStatus.COMPLETED)
                        .paidAt(now.minusDays(2)).build());
        when(paymentRepository.findCompletedPaymentsWithin(any(), any(), any())).thenReturn(completedPayments);

        // when
        PaymentInfo.Orders result = paymentService.getCompletedOrdersBetweenDays(3);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getOrderIds()).containsExactly(1L, 2L); // 실제 필드 값 검증
        verify(paymentRepository, times(1)).findCompletedPaymentsWithin(any(), any(), any());
    }

}