package kr.hhplus.be.server.domain.balance;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Getter;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class BalanceInfo {
    
    @Getter
    public static class Balance {
        private final Long balance;

        private Balance(Long balance) {
            this.balance = balance;
        }

        public static Balance of(Long balance) {
            return new Balance(balance);
        }
    }
}
