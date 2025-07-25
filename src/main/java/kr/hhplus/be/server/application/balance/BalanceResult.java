package kr.hhplus.be.server.application.balance;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BalanceResult {
    @Getter
    public static class Balance{
        private final Long balance;

        public Balance(Long balance) {
            this.balance = balance;
        }

        public static Balance of(Long balance) {
            return new Balance(balance);
        }
    }
}
