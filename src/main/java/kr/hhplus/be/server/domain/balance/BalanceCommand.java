package kr.hhplus.be.server.domain.balance; 

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class BalanceCommand {
    
    @Getter
    public static class Charge {
        private final Long userId;
        private final Long amount;

        private Charge(Long userId, Long amount) {
            this.userId = userId;
            this.amount = amount;
        }

        public static Charge of(Long userId, Long amount) {
            return new Charge(userId, amount);
        }
    }

    @Getter
    public static class Use {
        private final Long userId;
        private final Long amount;

        private Use(Long userId, Long amount) {
            this.userId = userId;
            this.amount = amount;
        }

        public static Use of(Long userId, Long amount) {
            return new Use(userId, amount);
        }
    }
}
