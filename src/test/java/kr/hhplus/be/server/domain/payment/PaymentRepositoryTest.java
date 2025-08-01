package kr.hhplus.be.server.domain.payment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentRepositoryTest {

    @Mock
    private PaymentRepository paymentRepository;

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
    @DisplayName("결제 저장 - 성공")
    void save_success() {
        // given
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

        // when
        Payment savedPayment = paymentRepository.save(testPayment);

        // then
        assertThat(savedPayment).isNotNull();
        assertThat(savedPayment.getId()).isEqualTo(1L);
        verify(paymentRepository, times(1)).save(testPayment);
    }

    @Test
    @DisplayName("결제 ID로 조회 - 성공")
    void findById_success() {
        // given
        Long paymentId = 1L;
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(testPayment));

        // when
        Optional<Payment> result = paymentRepository.findById(paymentId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        verify(paymentRepository, times(1)).findById(paymentId);
    }

    @Test
    @DisplayName("결제 ID로 조회 - 존재하지 않는 결제")
    void findById_notFound() {
        // given
        Long paymentId = 999L;
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        // when
        Optional<Payment> result = paymentRepository.findById(paymentId);

        // then
        assertThat(result).isEmpty();
        verify(paymentRepository, times(1)).findById(paymentId);
    }

    @Test
    @DisplayName("완료된 결제 목록 조회 - 성공")
    void findCompletedPaymentsWithIn_success() {
        // given
        List<PaymentStatus> statuses = List.of(PaymentStatus.COMPLETED);
        LocalDateTime startDateTime = LocalDateTime.now().minusDays(7);
        LocalDateTime endDateTime = LocalDateTime.now();
        List<Payment> payments = List.of(testPayment);
        
        when(paymentRepository.findCompletedPaymentsWithIn(statuses, startDateTime, endDateTime))
            .thenReturn(payments);

        // when
        List<Payment> result = paymentRepository.findCompletedPaymentsWithIn(statuses, startDateTime, endDateTime);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        verify(paymentRepository, times(1)).findCompletedPaymentsWithIn(statuses, startDateTime, endDateTime);
    }
} 