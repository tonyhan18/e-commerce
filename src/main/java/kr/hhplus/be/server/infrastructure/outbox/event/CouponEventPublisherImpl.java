package kr.hhplus.be.server.infrastructure.outbox.event;

import kr.hhplus.be.server.domain.coupon.CouponEvent;
import kr.hhplus.be.server.domain.coupon.CouponEventPublisher;
import kr.hhplus.be.server.support.event.EventType;
import kr.hhplus.be.server.support.message.DefaultMessage;
import kr.hhplus.be.server.support.message.Message;
import kr.hhplus.be.server.support.message.MessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponEventPublisherImpl implements CouponEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public <T> void publishEvent(EventType type, Long partitionKey, T payload) {
        String eventId = UUID.randomUUID().toString();
        Outbox outbox = Outbox.create(
            eventId,
            type,
            partitionKey,
            Event.of(eventId, type, payload).toJson()
        );

        eventPublisher.publishEvent(OutboxEvent.of(outbox));
    }
}
