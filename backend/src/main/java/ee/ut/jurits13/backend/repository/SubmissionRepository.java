package ee.ut.jurits13.backend.repository;

import ee.ut.jurits13.backend.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

}
