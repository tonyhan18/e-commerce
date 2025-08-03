package kr.hhplus.be.server.domain.payment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class PaymentServiceIntegrationTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Payment testPayment;

    @BeforeEach
    void setUp() {
        testPayment = Payment.builder()
            .id(1L)
            .orderId(1L)
            .amount(10000L)
            .paymentMethod(PaymentMethod.UNKNOWN)
            .paymentStatus(PaymentStatus.COMPLETED)
            .paidAt(LocalDateTime.now())
            .build();
    }

    @Test
    @DisplayName("결제 처리 - 성공")
    void pay_success() {
        // given
        Long orderId = 1L;
        Long amount = 10000L;
        PaymentCommand.Payment command = PaymentCommand.Payment.of(orderId, 1L, amount);
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

        // when
        PaymentInfo.Payment result = paymentService.pay(command);

        // then
        assertThat(result).isNotNull();
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("완료된 주문 조회 - 성공")
    void getCompletedOrdersBetweenDays_success() {
        // given
        int recentDays = 7;
        List<Payment> completedPayments = List.of(testPayment);
        when(paymentRepository.findCompletedPaymentsWithIn(eq(PaymentStatus.forCompleted()), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(completedPayments);

        // when
        PaymentInfo.Orders result = paymentService.getCompletedOrdersBetweenDays(recentDays);

        // then
        assertThat(result.getOrderIds()).hasSize(1);
        assertThat(result.getOrderIds().get(0)).isEqualTo(1L);
        verify(paymentRepository, times(1)).findCompletedPaymentsWithIn(eq(PaymentStatus.forCompleted()), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("완료된 주문 조회 - 빈 결과")
    void getCompletedOrdersBetweenDays_empty() {
        // given
        int recentDays = 7;
        List<Payment> completedPayments = List.of();
        when(paymentRepository.findCompletedPaymentsWithIn(eq(PaymentStatus.forCompleted()), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(completedPayments);

        // when
        PaymentInfo.Orders result = paymentService.getCompletedOrdersBetweenDays(recentDays);

        // then
        assertThat(result.getOrderIds()).isEmpty();
        verify(paymentRepository, times(1)).findCompletedPaymentsWithIn(eq(PaymentStatus.forCompleted()), any(LocalDateTime.class), any(LocalDateTime.class));
    }
} 