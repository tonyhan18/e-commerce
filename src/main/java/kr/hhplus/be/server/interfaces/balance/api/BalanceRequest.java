package kr.hhplus.be.server.interfaces.balance.api;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.application.balance.BalanceCriteria;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.Getter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BalanceRequest {

    @Getter
    @NoArgsConstructor
    public static class Charge {
        @NotNull(message = "잔액은 필수 입니다")
        @Positive(message = "잔액은 양수여야 합니다")
        private Long amount;

        public Charge(Long amount) {
            this.amount = amount;
        }

        public static Charge of(Long amount) {
            return new Charge(amount);
        }

        public BalanceCriteria.Charge toCriteria(Long userId) {
            return BalanceCriteria.Charge.of(userId, amount);
        }
    }
} 