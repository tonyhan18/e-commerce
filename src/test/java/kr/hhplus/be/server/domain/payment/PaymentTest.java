package kr.hhplus.be.server.domain.payment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class PaymentTest {

    @Test
    @DisplayName("정적 팩토리 메서드로 결제 객체를 생성할 수 있다.")
    void createPayment() {
        // given
        Long orderId = 1L;
        Long amount = 10000L;

        // when
        Payment payment = Payment.create(orderId, amount);

        // then
        assertThat(payment.getOrderId()).isEqualTo(orderId);
        assertThat(payment.getAmount()).isEqualTo(amount);
        assertThat(payment.getPaymentMethod()).isEqualTo(PaymentMethod.UNKNOWN);
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.READY);
        assertThat(payment.getPaidAt()).isNull();
        assertThat(payment.getId()).isNull();
    }

    @Test
    @DisplayName("결제 금액이 0 이하이면 예외가 발생한다.")
    void createPaymentWithInvalidAmount() {
        // given
        Long orderId = 1L;
        Long amount = 0L;

        // when & then
        assertThatThrownBy(() -> Payment.create(orderId, amount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결제 금액은 0원 이상이어야 합니다.");
    }

    @Test
    @DisplayName("결제 상태가 READY일 때 pay()를 호출하면 COMPLETED로 변경되고 paidAt이 설정된다.")
    void paySuccess() {
        // given
        Payment payment = Payment.create(1L, 10000L);

        // when
        payment.pay();

        // then
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(payment.getPaidAt()).isNotNull();
        assertThat(payment.getPaidAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("결제 불가능한 상태에서 pay()를 호출하면 예외가 발생한다.")
    void payFail() {
        // given
        Payment payment = Payment.builder()
                .orderId(1L)
                .amount(10000L)
                .paymentMethod(PaymentMethod.CARD)
                .paymentStatus(PaymentStatus.COMPLETED) // 이미 결제 완료 상태
                .build();

        // when & then
        assertThatThrownBy(payment::pay)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("결제 불가능한 상태입니다.");
    }

    @Test
    @DisplayName("빌더로 결제 객체를 생성할 수 있다.")
    void builderCreatePayment() {
        // given
        Long id = 10L;
        Long orderId = 2L;
        Long amount = 5000L;
        LocalDateTime paidAt = LocalDateTime.now();

        // when
        Payment payment = Payment.builder()
                .id(id)
                .orderId(orderId)
                .amount(amount)
                .paymentMethod(PaymentMethod.CARD)
                .paymentStatus(PaymentStatus.COMPLETED)
                .paidAt(paidAt)
                .build();

        // then
        assertThat(payment.getId()).isEqualTo(id);
        assertThat(payment.getOrderId()).isEqualTo(orderId);
        assertThat(payment.getAmount()).isEqualTo(amount);
        assertThat(payment.getPaymentMethod()).isEqualTo(PaymentMethod.CARD);
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(payment.getPaidAt()).isEqualTo(paidAt);
    }
} 