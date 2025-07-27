package kr.hhplus.be.server.domain.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInfo {
    @Getter
    public static class User {
        private final Long userId;
        private final String username;

        @Builder
        public User(Long userId, String username) {
            this.userId = userId;
            this.username = username;
        }
    }
}
