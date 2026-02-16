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
    private final FeedbackService feedbackService;

    public SubmissionService(SubmissionRepository submissionRepository, ExerciseRepository exerciseRepository, UserRepository userRepository, FeedbackService feedbackService) {
        this.submissionRepository = submissionRepository;
        this.exerciseRepository = exerciseRepository;
        this.userRepository = userRepository;
        this.feedbackService = feedbackService;
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
                submission.getCreatedAt(),
                submission.getLlmModel(),
                submission.getPromptVersion(),
                submission.getEvaluatedAt()
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

        // 1) Save initial submission
        Submission s = new Submission(exercise, user, dto.getAnswer());
        Submission saved = submissionRepository.save(s);

        // 2) Generate coach-style feedback (stub for now)
        String feedback = feedbackService.generateCoachFeedback(exercise, saved.getAnswer());

        // 3) Store feedback + traceability metadata
        saved.setFeedback(feedback);
        saved.setLlmModel("stub");
        saved.setPromptVersion("v1");
        saved.setEvaluatedAt(java.time.Instant.now());

        // optional: keep isCorrect null for now
        // saved.setIsCorrect(null);

        // 4) Save updated submission
        Submission updated = submissionRepository.save(saved);

        return toResponseDTO(updated);
    }

    public void deleteSubmission(Long id) {
        if (!submissionRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Submission not found");

        }
        submissionRepository.deleteById(id);
    }

}
