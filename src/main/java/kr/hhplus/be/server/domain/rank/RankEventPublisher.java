package kr.hhplus.be.server.domain.rank;

public interface RankEventPublisher {

    void created(RankEvent.Created event);
}
