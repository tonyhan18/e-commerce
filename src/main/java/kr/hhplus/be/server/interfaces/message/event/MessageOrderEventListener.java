package kr.hhplus.be.server.interfaces.message.event;

import kr.hhplus.be.server.domain.message.MessageCommand;
import kr.hhplus.be.server.domain.message.MessageService;
import kr.hhplus.be.server.domain.order.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.context.event.EventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageOrderEventListener {

    private final MessageService messageService;

    @Async
    @EventListener
    public void handlePaidEvent(OrderEvent.Completed event) {
        messageService.sendOrder(MessageCommand.Order.of(event));
    }
}
