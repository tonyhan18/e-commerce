package kr.hhplus.be.server.domain.coupon;

public interface CouponEventPublisher {

    void publishRequested(CouponEvent.PublishRequested event);

    void published(CouponEvent.Published event);
}
