package ee.ut.jurits13.backend.service;

import ee.ut.jurits13.backend.dto.HelpSessionRequestDTO;
import ee.ut.jurits13.backend.dto.HelpSessionResponseDTO;
import ee.ut.jurits13.backend.entity.HelpSession;
import ee.ut.jurits13.backend.entity.User;
import ee.ut.jurits13.backend.repository.HelpSessionRepository;
import ee.ut.jurits13.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class HelpSessionService {
    private final HelpSessionRepository helpSessionRepository;
    private final UserRepository userRepository;

    public HelpSessionService(HelpSessionRepository helpSessionRepository, UserRepository userRepository) {
        this.helpSessionRepository = helpSessionRepository;
        this.userRepository = userRepository;
    }

    private HelpSessionResponseDTO toResponseDTO(HelpSession session) {
        return new HelpSessionResponseDTO(
                session.getId(),
                session.getUser().getId(),
                session.getUser().getUsername(),
                session.getTitle(),
                session.getProblemDescription(),
                session.getCodeSnippet(),
                session.getWhatTried(),
                session.getStatus().name(),
                session.getCreatedAt(),
                session.getUpdatedAt()
        );
    }

    public HelpSessionResponseDTO createSession(HelpSessionRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        HelpSession session = new HelpSession(
                user,
                dto.getTitle(),
                dto.getProblemDescription(),
                dto.getCodeSnippet(),
                dto.getWhatTried()
        );

        HelpSession saved = helpSessionRepository.save(session);
        return toResponseDTO(saved);
    }

    public HelpSessionResponseDTO getSessionById(Long id) {
        HelpSession session = helpSessionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Help session not found"));

        return toResponseDTO(session);
    }

    public List<HelpSessionResponseDTO> getSessionsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        return helpSessionRepository.findByUserIdOrderByUpdatedAtDesc(userId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public HelpSession getSessionEntityById(Long id) {
        return helpSessionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Help session not found"));
    }
}
