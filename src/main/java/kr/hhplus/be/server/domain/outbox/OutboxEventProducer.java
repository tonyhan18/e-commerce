package kr.hhplus.be.server.domain.outbox;

public interface OutboxEventProducer {

    void produceEvent(Outbox outbox);
}
