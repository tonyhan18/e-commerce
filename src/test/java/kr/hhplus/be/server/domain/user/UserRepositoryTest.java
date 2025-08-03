package kr.hhplus.be.server.domain.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.create("테스트 사용자");
    }

    @Test
    @DisplayName("사용자 저장 - 성공")
    void save_success() {
        // given
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // when
        User savedUser = userRepository.save(testUser);

        // then
        assertThat(savedUser).isNotNull();
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("사용자 조회 - 성공")
    void findById_success() {
        // given
        when(userRepository.findById(1L)).thenReturn(testUser);

        // when
        User foundUser = userRepository.findById(1L);

        // then
        assertThat(foundUser).isNotNull();
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("사용자 조회 - 존재하지 않는 사용자")
    void findById_notFound() {
        // given
        when(userRepository.findById(999L))
            .thenReturn(null);

        // when
        User result = userRepository.findById(999L);

        // then
        assertThat(result).isNull();
        verify(userRepository, times(1)).findById(999L);
    }
} 