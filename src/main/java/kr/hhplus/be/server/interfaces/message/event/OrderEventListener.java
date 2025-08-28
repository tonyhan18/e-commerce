package kr.hhplus.be.server.interfaces.message.event;

import kr.hhplus.be.server.domain.message.MessageCommand;
import kr.hhplus.be.server.domain.message.MessageService;
import kr.hhplus.be.server.domain.order.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final MessageService messageService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaidEvent(OrderEvent.Paid event) {
        messageService.sendOrder(MessageCommand.Order.of(event));
    }
}
