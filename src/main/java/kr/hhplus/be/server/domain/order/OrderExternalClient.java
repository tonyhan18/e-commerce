package kr.hhplus.be.server.domain.order;

public interface OrderExternalClient {
    void sendOrderMessage(Order order);
}