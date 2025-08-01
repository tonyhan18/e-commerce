package kr.hhplus.be.server.domain.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceIntegrationTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .id(1L)
            .username("테스트 사용자")
            .build();
    }

    @Test
    @DisplayName("사용자 조회 - 성공")
    void getUser_success() {
        // given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(testUser);

        // when
        UserInfo.User result = userService.getUser(userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("테스트 사용자");
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("사용자 조회 - 존재하지 않는 사용자")
    void getUser_notFound() {
        // given
        Long userId = 999L;
        when(userRepository.findById(userId))
            .thenThrow(new IllegalArgumentException("User not found with id: 999"));

        // when & then
        assertThatThrownBy(() -> userService.getUser(userId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("User not found with id: 999");
        verify(userRepository, times(1)).findById(userId);
    }
} 