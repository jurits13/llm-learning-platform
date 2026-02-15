package ee.ut.jurits13.backend.service;

import ee.ut.jurits13.backend.dto.SubmissionRequestDTO;
import ee.ut.jurits13.backend.dto.SubmissionResponseDTO;
import ee.ut.jurits13.backend.entity.Exercise;
import ee.ut.jurits13.backend.entity.Submission;
import ee.ut.jurits13.backend.entity.User;
import ee.ut.jurits13.backend.repository.ExerciseRepository;
import ee.ut.jurits13.backend.repository.SubmissionRepository;
import ee.ut.jurits13.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;

    public SubmissionService(SubmissionRepository submissionRepository, ExerciseRepository exerciseRepository, UserRepository userRepository) {
        this.submissionRepository = submissionRepository;
        this.exerciseRepository = exerciseRepository;
        this.userRepository = userRepository;
    }

    private SubmissionResponseDTO toResponseDTO(Submission submission) {
        return new SubmissionResponseDTO(
                submission.getId(),
                submission.getExercise().getId(),
                submission.getUser().getId(),
                submission.getUser().getUsername(),
                submission.getAnswer(),
                submission.getIsCorrect(),
                submission.getFeedback(),
                submission.getCreatedAt()
        );
    }

    public List<SubmissionResponseDTO> getAllSubmissions() {
        return submissionRepository.findAll().stream().map(this::toResponseDTO).toList();
    }

    public SubmissionResponseDTO getSubmissionById(Long id) {
        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Submission not found"));

        return toResponseDTO(submission);
    }

    public SubmissionResponseDTO createSubmission(SubmissionRequestDTO dto) {
        Exercise exercise = exerciseRepository.findById(dto.getExerciseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercise not found"));

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));


        Submission s = new Submission(exercise, user, dto.getAnswer());
        Submission saved = submissionRepository.save(s);

        return toResponseDTO(saved);
    }

    public void deleteSubmission(Long id) {
        if (!submissionRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Submission not found");

        }
        submissionRepository.deleteById(id);
    }

}
