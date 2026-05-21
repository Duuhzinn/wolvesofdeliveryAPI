package wolvesofdelivery.api.rest.controller;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/v1/corrida")
@CrossOrigin(origins = "*")
public class CorridaController {

    private final SimpMessagingTemplate messagingTemplate;

    public CorridaController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/aceitar/{motoristaId}")
    public ResponseEntity<String> aceitarCorrida(@PathVariable Long motoristaId) {
        // publica no tópico avisando o frontend que o motorista aceitou
        messagingTemplate.convertAndSend("/topic/corrida", motoristaId);
        return ResponseEntity.ok("Corrida aceita pelo motorista " + motoristaId);
    }
}