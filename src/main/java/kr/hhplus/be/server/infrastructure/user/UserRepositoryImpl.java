package kr.hhplus.be.server.infrastructure.user;

import kr.hhplus.be.server.domain.user.*;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepository {
    @Override
    public User findById(Long id) {
        // TODO: 실제 DB 연동 로직 구현
        return null;
    }
} 