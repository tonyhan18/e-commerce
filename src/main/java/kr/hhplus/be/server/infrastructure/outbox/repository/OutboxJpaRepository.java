package kr.hhplus.be.server.infrastructure.outbox.repository;

import kr.hhplus.be.server.domain.outbox.Outbox;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OutboxJpaRepository extends JpaRepository<Outbox, Long> {

    List<Outbox> findAllByCreatedAtLessThanEqualOrderByCreatedAtAsc(LocalDateTime createdAt, Pageable pageable);

    void deleteByEventId(String eventId);
}
