package kr.hhplus.be.server.domain.order;

public interface OrderEventPublisher {

    void created(OrderEvent.Created event);

    void completed(OrderEvent.Completed event);

    void completeFailed(OrderEvent.CompleteFailed event);
}
