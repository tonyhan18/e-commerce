package kr.hhplus.be.server.support.key;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KeyType {

    RANK("랭크"),
    COUPON("사용자 쿠폰"),
    ORDER("주문"),
    ;

    private final String description;

    public String getKey() {
        return this.name().toLowerCase();
    }
}
