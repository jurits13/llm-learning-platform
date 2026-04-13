package ee.ut.jurits13.backend.controller;

import ee.ut.jurits13.backend.dto.HelpSessionRequestDTO;
import ee.ut.jurits13.backend.dto.HelpSessionResponseDTO;
import ee.ut.jurits13.backend.dto.MessageRequestDTO;
import ee.ut.jurits13.backend.dto.MessageResponseDTO;
import ee.ut.jurits13.backend.service.HelpSessionService;
import ee.ut.jurits13.backend.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/help-sessions")
public class HelpSessionController {
    private final HelpSessionService helpSessionService;
    private final MessageService messageService;

    public HelpSessionController(HelpSessionService helpSessionService, MessageService messageService) {
        this.helpSessionService = helpSessionService;
        this.messageService = messageService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HelpSessionResponseDTO createSession(@Valid @RequestBody HelpSessionRequestDTO dto) {
        return helpSessionService.createSession(dto);
    }

    @GetMapping("/{id}")
    public HelpSessionResponseDTO getSessionById(@PathVariable Long id) {
        return helpSessionService.getSessionById(id);
    }

    @GetMapping("/user/{userId}")
    public List<HelpSessionResponseDTO> getSessionsByUserId(@PathVariable Long userId) {
        return helpSessionService.getSessionsByUserId(userId);
    }

    @GetMapping("/{id}/messages")
    public List<MessageResponseDTO> getMessagesBySessionId(@PathVariable Long id) {
        return messageService.getMessagesBySessionId(id);
    }

    @PostMapping("/{id}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO createMessage(@PathVariable Long id, @Valid @RequestBody MessageRequestDTO dto) {
        return messageService.createStudentMessageAndCoachReply(id, dto);
    }
}
