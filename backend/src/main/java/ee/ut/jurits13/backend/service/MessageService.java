package ee.ut.jurits13.backend.service;

import ee.ut.jurits13.backend.dto.MessageRequestDTO;
import ee.ut.jurits13.backend.dto.MessageResponseDTO;
import ee.ut.jurits13.backend.entity.HelpSession;
import ee.ut.jurits13.backend.entity.Message;
import ee.ut.jurits13.backend.entity.MessageRole;
import ee.ut.jurits13.backend.repository.HelpSessionRepository;
import ee.ut.jurits13.backend.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final HelpSessionRepository helpSessionRepository;
    private final HelpSessionService helpSessionService;
    private final CoachService coachService;

    private static final Logger log = LoggerFactory.getLogger(MessageService.class);

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
                message.getCoachResponseLevel(),
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

    @Transactional
    public MessageResponseDTO createStudentMessageAndCoachReply(Long helpSessionId, MessageRequestDTO dto) {
        HelpSession session = helpSessionService.getSessionEntityById(helpSessionId);

        log.info("Creating student message for helpSessionId={}", helpSessionId);

        Message studentMessage = new Message(session, MessageRole.STUDENT, dto.getContent());
        messageRepository.save(studentMessage);

        List<Message> conversation = messageRepository.findByHelpSessionIdOrderByCreatedAtAsc(helpSessionId);

        CoachService.CoachReply coachReply = coachService.generateReply(session, conversation);

        if (coachReply.filtered()) {
            log.warn(
                    "Coach reply filtered for helpSessionId={}, reason={}, level={}",
                    helpSessionId,
                    coachReply.filterReason(),
                    coachReply.coachResponseLevel()
            );
        } else {
            log.info(
                    "Coach reply generated for helpSessionId={}, level={}, model={}",
                    helpSessionId,
                    coachReply.coachResponseLevel(),
                    coachReply.llmModel()
            );
        }

        Message coachMessage = new Message(session, MessageRole.COACH, coachReply.content());
        coachMessage.setLlmModel(coachReply.llmModel());
        coachMessage.setPromptVersion(coachReply.promptVersion());
        coachMessage.setFilteredByPolicy(coachReply.filtered());
        coachMessage.setPolicyReason(coachReply.filterReason());
        coachMessage.setCoachResponseLevel(coachReply.coachResponseLevel());

        Message savedCoachMessage = messageRepository.save(coachMessage);

        session.setUpdatedAt(java.time.Instant.now());
        helpSessionRepository.save(session);

        return toResponseDTO(savedCoachMessage);
    }
}
