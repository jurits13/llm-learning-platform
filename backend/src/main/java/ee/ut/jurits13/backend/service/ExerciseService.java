package ee.ut.jurits13.backend.service;

import ee.ut.jurits13.backend.dto.ExerciseRequestDTO;
import ee.ut.jurits13.backend.dto.ExerciseResponseDTO;
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

    private ExerciseResponseDTO toResponseDTO(Exercise exercise) {
        return new ExerciseResponseDTO(
                exercise.getId(),
                exercise.getTitle(),
                exercise.getDescription(),
                exercise.getDifficulty()
        );
    }

    public List<ExerciseResponseDTO> getAllExercises() {
        return exerciseRepository.findAll().stream().map(this::toResponseDTO).toList();
    }

    public ExerciseResponseDTO getExerciseById(Long id) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercise not found"));
        return toResponseDTO(exercise);
    }

    public ExerciseResponseDTO createExercise(ExerciseRequestDTO dto) {
        Exercise e = new Exercise(dto.getTitle(), dto.getDescription(), dto.getDifficulty());
        Exercise saved = exerciseRepository.save(e);

        return toResponseDTO(saved);
    }

    public ExerciseResponseDTO updateExercise(Long id, ExerciseRequestDTO dto) {
        Exercise existing = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercise not found"));

        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());
        existing.setDifficulty(dto.getDifficulty());

        Exercise saved = exerciseRepository.save(existing);

        return toResponseDTO(saved);
    }

    public void deleteExercise(Long id) {
        if (!exerciseRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercise not found");

        }
        exerciseRepository.deleteById(id);
    }
}
