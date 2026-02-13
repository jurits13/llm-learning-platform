package ee.ut.jurits13.backend.service;

import ee.ut.jurits13.backend.entity.Exercise;
import ee.ut.jurits13.backend.repository.ExerciseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;

    public ExerciseService(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    public List<Exercise> getAllExercises() {
        return exerciseRepository.findAll();
    }

    public Optional<Exercise> getExerciseByID(Long id) {
        return exerciseRepository.findById(id);
    }

    public Exercise createExercise(Exercise exercise) {
        // simple validation
        if (exercise.getTitle() == null || exercise.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title is required");
        }
        return exerciseRepository.save(exercise);
    }
}
