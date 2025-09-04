package kr.hhplus.be.server.domain.outbox;

import lombok.Getter;

@Getter
public class OutboxEvent {

    private final Outbox outbox;

    private OutboxEvent(Outbox outbox) {
        this.outbox = outbox;
    }

    public static OutboxEvent of(Outbox outbox) {
        return new OutboxEvent(outbox);
    }
}
