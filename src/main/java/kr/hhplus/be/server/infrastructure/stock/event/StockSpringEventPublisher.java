package kr.hhplus.be.server.infrastructure.stock.event;

import kr.hhplus.be.server.domain.stock.StockEvent;
import kr.hhplus.be.server.domain.stock.StockEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockSpringEventPublisher implements StockEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void deducted(StockEvent.Deducted event) {
        eventPublisher.publishEvent(event);
    }

    @Override
    public void deductFailed(StockEvent.DeductFailed event) {
        eventPublisher.publishEvent(event);
    }
}
