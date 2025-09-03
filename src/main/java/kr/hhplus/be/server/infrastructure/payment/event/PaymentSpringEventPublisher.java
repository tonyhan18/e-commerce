package kr.hhplus.be.server.infrastructure.payment.event;

import kr.hhplus.be.server.domain.payment.PaymentEvent;
import kr.hhplus.be.server.domain.payment.PaymentEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentSpringEventPublisher implements PaymentEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void paid(PaymentEvent.Paid event) {
        eventPublisher.publishEvent(event);
    }

    @Override
    public void payFailed(PaymentEvent.PayFailed event) {
        eventPublisher.publishEvent(event);
    }

    @Override
    public void canceled(PaymentEvent.Canceled event) {
        eventPublisher.publishEvent(event);
    }
}
