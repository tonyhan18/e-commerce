package kr.hhplus.be.server.domain.user;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserInfo.User getUser(Long userId) {
        User user = userRepository.findById(userId);
        return UserInfo.User.builder()
            .userId(user.getId())
            .username(user.getUsername())
            .build();
    }
}