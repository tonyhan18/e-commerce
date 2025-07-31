package kr.hhplus.be.server.infrastructure.order;

import org.springframework.stereotype.Component;

@Component
public class OrderExternalClientImpl implements OrderExternalClient {

    @Override
    public void sendOrderMessage(Order order) {
        // TODO: 메시지 큐 연동 등 구현
    }
}
