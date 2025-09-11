package kr.hhplus.be.server.interfaces.coupon.event;

import kr.hhplus.be.server.domain.coupon.CouponCommand;
import kr.hhplus.be.server.domain.coupon.CouponEvent;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.support.event.Event;
import kr.hhplus.be.server.support.event.EventType.GroupId;
import kr.hhplus.be.server.support.event.EventType.Topic;
import kr.hhplus.be.server.support.exception.CoreException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponMessageEventListener {

    private final CouponService couponService;

    @KafkaListener(topics = Topic.COUPON_PUBLISH_REQUESTED, groupId = GroupId.COUPON)
    public void handle(String message, Acknowledgment ack) {
        log.info("쿠폰 발급 요청 이벤트 수신 {}", message);

        try {
            Event<CouponEvent.PublishRequested> event = Event.of(message, CouponEvent.PublishRequested.class);
            CouponEvent.PublishRequested payload = event.getPayload();

            couponService.publishUserCoupon(CouponCommand.Publish.of(payload.getUserId(), payload.getCouponId()));
            ack.acknowledge();
        } catch (CoreException e) {
            log.warn("쿠폰 발급 요청 이벤트 처리 중 오류 발생: {}", e.getMessage(), e);
            ack.acknowledge();
        }
    }
}
