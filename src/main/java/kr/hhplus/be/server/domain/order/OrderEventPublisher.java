package kr.hhplus.be.server.domain.order;

public interface OrderEventPublisher {

    void paid(OrderEvent.Paid event);
}
