package kr.hhplus.be.server.infrastructure.payment.event;

import kr.hhplus.be.server.domain.payment.PaymentEvent;
import kr.hhplus.be.server.domain.payment.PaymentEventPublisher;
import kr.hhplus.be.server.support.event.EventType;
import kr.hhplus.be.server.support.outbox.OutboxEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventPublisherImpl implements PaymentEventPublisher {

    private final OutboxEventPublisher outboxEventPublisher;

    @Override
    public void paid(PaymentEvent.Paid event) {
        outboxEventPublisher.publishEvent(EventType.PAYMENT_PAID, event.getOrderId(), event);
    }

    @Override
    public void payFailed(PaymentEvent.PayFailed event) {
        outboxEventPublisher.publishEvent(EventType.PAYMENT_FAILED, event.getOrderId(), event);
    }

    @Override
    public void canceled(PaymentEvent.Canceled event) {
        outboxEventPublisher.publishEvent(EventType.PAYMENT_CANCELED, event.getOrderId(), event);
    }
}
