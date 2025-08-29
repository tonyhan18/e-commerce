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

    @Getter
    public static class User {

        private final Long userId;
        private final String userName;

        private User(Long userId, String userName) {
            this.userId = userId;
            this.userName = userName;
        }

        public static User of(Long userId, String userName) {
            return new User(userId, userName);
        }
    }
}
