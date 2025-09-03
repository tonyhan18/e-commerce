package kr.hhplus.be.server.domain.coupon;

public interface CouponEventPublisher {

    void used(CouponEvent.Used event);

    void useFailed(CouponEvent.UseFailed event);

    void canceled(CouponEvent.Canceled event);
}
