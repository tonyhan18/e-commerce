package kr.hhplus.be.server.domain.payment;

public interface PaymentEventPublisher {

    void paid(PaymentEvent.Paid event);

    void payFailed(PaymentEvent.PayFailed event);

    void canceled(PaymentEvent.Canceled event);
}
