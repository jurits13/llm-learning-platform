package ee.ut.jurits13.backend.service;

import ee.ut.jurits13.backend.dto.ExerciseRequestDTO;
import ee.ut.jurits13.backend.entity.Exercise;
import ee.ut.jurits13.backend.repository.ExerciseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;

    public ExerciseService(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    public List<Exercise> getAllExercises() {
        return exerciseRepository.findAll();
    }

    public Exercise getExerciseById(Long id) {
        return exerciseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercise not found"));
    }

    public Exercise createExercise(ExerciseRequestDTO dto) {
        Exercise e = new Exercise(dto.getTitle(), dto.getDescription(), dto.getDifficulty());
        return exerciseRepository.save(e);
    }

    public Exercise updateExercise(Long id, ExerciseRequestDTO dto) {
        Exercise existing = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercise not found"));

        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());
        existing.setDifficulty(dto.getDifficulty());

        return exerciseRepository.save(existing);
    }

    public void deleteExercise(Long id) {
        if (!exerciseRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercise not found");

        }
        exerciseRepository.deleteById(id);
    }
}
