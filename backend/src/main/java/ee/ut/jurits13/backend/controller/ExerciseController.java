package ee.ut.jurits13.backend.controller;

import ee.ut.jurits13.backend.dto.ExerciseRequestDTO;
import ee.ut.jurits13.backend.dto.ExerciseResponseDTO;
import ee.ut.jurits13.backend.service.ExerciseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {
    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @GetMapping
    public List<ExerciseResponseDTO> getAllExercises() {
        return exerciseService.getAllExercises();
    }

    @GetMapping("/{id}")
    public ExerciseResponseDTO getExerciseById(@PathVariable Long id) {
        return exerciseService.getExerciseById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExerciseResponseDTO createExercise(@Valid @RequestBody ExerciseRequestDTO dto) {
        return exerciseService.createExercise(dto);
    }

    @PutMapping("/{id}")
    public ExerciseResponseDTO updateExercise(@PathVariable Long id, @Valid @RequestBody ExerciseRequestDTO dto) {
        return exerciseService.updateExercise(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteExercise(@PathVariable Long id) {
        exerciseService.deleteExercise(id);
    }
}
