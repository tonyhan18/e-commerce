package kr.hhplus.be.server.domain.user;

import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository  {
    User save(User user);
    User findById(Long id);
}
