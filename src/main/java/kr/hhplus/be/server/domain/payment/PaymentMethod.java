package kr.hhplus.be.server.domain.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentMethod {
    CARD("카드"),
    CASH("현금"),
    VIRTUAL_ACCOUNT("가상계좌"),
    UNKNOWN("알 수 없음");

    private final String description;
    
}
