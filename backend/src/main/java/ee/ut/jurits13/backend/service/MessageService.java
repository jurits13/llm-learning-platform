package ee.ut.jurits13.backend.service;

import ee.ut.jurits13.backend.dto.MessageRequestDTO;
import ee.ut.jurits13.backend.dto.MessageResponseDTO;
import ee.ut.jurits13.backend.entity.HelpSession;
import ee.ut.jurits13.backend.entity.Message;
import ee.ut.jurits13.backend.entity.MessageRole;
import ee.ut.jurits13.backend.repository.HelpSessionRepository;
import ee.ut.jurits13.backend.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final HelpSessionRepository helpSessionRepository;
    private final HelpSessionService helpSessionService;
    private final CoachService coachService;

    public MessageService(
            MessageRepository messageRepository,
            HelpSessionRepository helpSessionRepository,
            HelpSessionService helpSessionService,
            CoachService coachService
    ) {
        this.messageRepository = messageRepository;
        this.helpSessionRepository = helpSessionRepository;
        this.helpSessionService = helpSessionService;
        this.coachService = coachService;
    }

    private MessageResponseDTO toResponseDTO(Message message) {
        return new MessageResponseDTO(
                message.getId(),
                message.getRole().name(),
                message.getContent(),
                message.getCreatedAt(),
                message.getLlmModel(),
                message.getPromptVersion(),
                message.isFilteredByPolicy(),
                message.getPolicyReason()
        );
    }

    public List<MessageResponseDTO> getMessagesBySessionId(Long helpSessionId) {
        helpSessionService.getSessionEntityById(helpSessionId);

        return messageRepository.findByHelpSessionIdOrderByCreatedAtAsc(helpSessionId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public MessageResponseDTO createStudentMessageAndCoachReply(Long helpSessionId, MessageRequestDTO dto) {
        HelpSession session = helpSessionService.getSessionEntityById(helpSessionId);

        Message studentMessage = new Message(session, MessageRole.STUDENT, dto.getContent());
        messageRepository.save(studentMessage);

        List<Message> conversation = messageRepository.findByHelpSessionIdOrderByCreatedAtAsc(helpSessionId);

        CoachService.CoachReply coachReply = coachService.generateReply(session, conversation);

        Message coachMessage = new Message(session, MessageRole.COACH, coachReply.content());
        coachMessage.setLlmModel(coachReply.llmModel());
        coachMessage.setPromptVersion(coachReply.promptVersion());
        coachMessage.setFilteredByPolicy(coachReply.filtered());
        coachMessage.setPolicyReason(coachReply.filterReason());

        Message savedCoachMessage = messageRepository.save(coachMessage);

        session.setUpdatedAt(java.time.Instant.now());
        helpSessionRepository.save(session);

        return toResponseDTO(savedCoachMessage);
    }
}
