package kr.hhplus.be.server.domain.payment;

public interface PaymentClient {

    void useBalance(Long userId, long amount);

    void useCoupon(Long userCouponId);

    PaymentInfo.Order getOrder(Long orderId);

    void refundBalance(Long userId, long amount);

    void cancelCoupon(Long userCouponId);
}
