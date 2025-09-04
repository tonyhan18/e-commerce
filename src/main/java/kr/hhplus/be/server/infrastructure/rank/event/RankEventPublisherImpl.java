package kr.hhplus.be.server.infrastructure.rank.event;

import kr.hhplus.be.server.domain.rank.RankEvent;
import kr.hhplus.be.server.domain.rank.RankEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RankEventPublisherImpl implements RankEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void created(RankEvent.Created event) {
        applicationEventPublisher.publishEvent(event);
    }
}
