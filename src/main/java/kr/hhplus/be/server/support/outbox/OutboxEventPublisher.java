package kr.hhplus.be.server.support.outbox;

import kr.hhplus.be.server.support.event.EventType;

public interface OutboxEventPublisher {

    <T> void publishEvent(EventType type, Long shardKey, T payload);

    <T> void publishManualEvent(EventType type, Long shardKey, T payload);
}
