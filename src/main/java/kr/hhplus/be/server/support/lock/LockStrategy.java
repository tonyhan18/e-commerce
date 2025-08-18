package kr.hhplus.be.server.support.lock;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LockStrategy {

    SPIN_LOCK("스핀 락"),
    PUB_SUB_LOCK("Pub/Sub 락"),
    ;
    private final String description;
}
