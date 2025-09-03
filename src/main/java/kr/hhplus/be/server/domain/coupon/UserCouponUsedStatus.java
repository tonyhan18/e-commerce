package kr.hhplus.be.server.domain.coupon;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum UserCouponUsedStatus {

    USED("사용완료"),
    UNUSED("미사용"),
    ;

    private final String description;

    private static final List<UserCouponUsedStatus> CANNOT_USABLE_STATUSES = List.of(USED);

    public boolean cannotUsable() {
        return CANNOT_USABLE_STATUSES.contains(this);
    }
}
