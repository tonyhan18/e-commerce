package kr.hhplus.be.server.infrastructure.order;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderExternalClient;

@Slf4j
@Component
public class OrderExternalClientImpl implements OrderExternalClient {

    @Override
    public void sendOrderMessage(Order order) {
        //log.info("sendOrderMessage: {}", order);
    }
}
