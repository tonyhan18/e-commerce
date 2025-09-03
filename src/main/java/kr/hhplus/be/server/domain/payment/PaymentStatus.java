package kr.hhplus.be.server.domain.payment;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentStatus {
    READY("준비"),
    WAITING("대기"),
    COMPLETED("완료"),
    FAILED("실패"),
    CANCELED("취소");


    private final String description;

    private static final List<PaymentStatus> CANNOT_PAYABLE_STATUSES = List.of(COMPLETED, FAILED, CANCELED);

    public boolean cannotPayable() {
        return CANNOT_PAYABLE_STATUSES.contains(this);
    }

    // public static List<PaymentStatus> forCompleted() {
    //     return List.of(COMPLETED);
    // }
}
