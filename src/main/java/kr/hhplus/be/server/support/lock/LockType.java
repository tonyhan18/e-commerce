package kr.hhplus.be.server.support.lock;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LockType {

    COUPON("쿠폰"),
    ;

    private final String description;

    public String createKey(String key) {
        return this.name().toLowerCase() + ":" + key;
    }
}
