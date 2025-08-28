package kr.hhplus.be.server.domain.message;

import kr.hhplus.be.server.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class MessageServiceTest extends IntegrationTestSupport {

    @Autowired
    private MessageService messageService;

    @MockBean
    private MessageClient messageClient;

    @DisplayName("주문을 외부 시스템에 전송한다.")
    @Test
    void sendMessage() {
        // given
        MessageCommand.Order command = MessageCommand.Order.builder()
            .orderId(1L)
            .userId(1L)
            .userCouponId(1L)
            .totalPrice(10_000L)
            .discountPrice(1_000L)
            .paidAt(LocalDateTime.now())
            .build();

        // when
        messageService.sendOrder(command);

        // then
        verify(messageClient, times(1)).sendOrder(command);
    }
}