package kr.hhplus.be.server.infrastructure.outbox.event;

import kr.hhplus.be.server.domain.outbox.Outbox;
import kr.hhplus.be.server.domain.outbox.OutboxEventProducer;
import kr.hhplus.be.server.support.message.MessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxEventProducerImpl implements OutboxEventProducer {

    private final MessageProducer messageProducer;

    @Override
    public void produceEvent(Outbox outbox) {
        messageProducer.send(outbox);
    }
}
