package ee.ut.jurits13.backend.repository;

import ee.ut.jurits13.backend.entity.HelpSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HelpSessionRepository extends JpaRepository<HelpSession, Long> {
    List<HelpSession> findByUserIdOrderByUpdatedAtDesc(Long userId);
}
