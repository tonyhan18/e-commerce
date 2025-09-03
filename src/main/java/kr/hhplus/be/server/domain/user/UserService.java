package kr.hhplus.be.server.domain.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserInfo.User getUser(Long userId) {
        User user = userRepository.findById(userId);
        return UserInfo.User.builder()
            .userId(user.getId())
            .username(user.getUsername())
            .build();
    }
}