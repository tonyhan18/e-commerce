package kr.hhplus.be.server.infrastructure.balance.event;

import kr.hhplus.be.server.domain.balance.BalanceEvent;
import kr.hhplus.be.server.domain.balance.BalanceEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BalanceSpringEventPublisher implements BalanceEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void used(BalanceEvent.Used event) {
        eventPublisher.publishEvent(event);
    }

    @Override
    public void useFailed(BalanceEvent.UseFailed event) {
        eventPublisher.publishEvent(event);
    }
}
