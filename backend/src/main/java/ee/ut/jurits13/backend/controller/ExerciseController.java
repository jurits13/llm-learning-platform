package ee.ut.jurits13.backend.controller;

import ee.ut.jurits13.backend.entity.Exercise;
import ee.ut.jurits13.backend.service.ExerciseService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {
    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @GetMapping
    public List<Exercise> getAllExercises() {
        return exerciseService.getAllExercises();
    }

    @GetMapping("/{id}")
    public Exercise getExerciseByID(@PathVariable Long id) {
        return exerciseService.getExerciseByID(id)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));
    }

    @PostMapping
    Exercise createExercise(@Valid @RequestBody Exercise exercise) {
        return exerciseService.createExercise(exercise);
    }
}
