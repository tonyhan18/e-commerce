package kr.hhplus.be.server.infrastructure.order.event;

import kr.hhplus.be.server.domain.order.OrderEvent;
import kr.hhplus.be.server.domain.order.OrderEventPublisher;
import kr.hhplus.be.server.support.event.EventType;
import kr.hhplus.be.server.support.outbox.OutboxEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventPublisherImpl implements OrderEventPublisher {

    private final OutboxEventPublisher outboxEventPublisher;

    @Override
    public void created(OrderEvent.Created event) {
        outboxEventPublisher.publishEvent(EventType.ORDER_CREATED, event.getOrderId(), event);
    }

    @Override
    public void completed(OrderEvent.Completed event) {
        outboxEventPublisher.publishEvent(EventType.ORDER_COMPLETED, event.getOrderId(), event);
    }

    @Override
    public void completeFailed(OrderEvent.CompleteFailed event) {
        outboxEventPublisher.publishEvent(EventType.ORDER_COMPLETE_FAILED, event.getOrderId(), event);
    }
}
