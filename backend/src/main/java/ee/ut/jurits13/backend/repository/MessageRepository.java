package ee.ut.jurits13.backend.repository;

import ee.ut.jurits13.backend.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByHelpSessionIdOrderByCreatedAtAsc(Long helpSessionId);
}
