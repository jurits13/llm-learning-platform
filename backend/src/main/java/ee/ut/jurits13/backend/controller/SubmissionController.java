package ee.ut.jurits13.backend.controller;

import ee.ut.jurits13.backend.dto.SubmissionRequestDTO;
import ee.ut.jurits13.backend.dto.SubmissionResponseDTO;
import ee.ut.jurits13.backend.service.SubmissionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {
    private final SubmissionService submissionService;


    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @GetMapping
    public List<SubmissionResponseDTO> getAllSubmissions() {
        return submissionService.getAllSubmissions();
    }

    @GetMapping("/{id}")
    public SubmissionResponseDTO getSubmissionById(@PathVariable Long id) {
        return submissionService.getSubmissionById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubmissionResponseDTO createSubmission(@Valid @RequestBody SubmissionRequestDTO dto) {
        return submissionService.createSubmission(dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSubmission(@PathVariable Long id) {
        submissionService.deleteSubmission(id);
    }
}
