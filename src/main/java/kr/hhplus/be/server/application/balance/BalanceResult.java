package kr.hhplus.be.server.application.balance;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BalanceResult {
    @Getter
    public static class Balance{
        private final Long amount;

        public Balance(Long amount) {
            this.amount = amount;
        }

        public static Balance of(Long amount) {
            return new Balance(amount);
        }
    }
}
