package duolthdelivery.api.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import duolthdelivery.api.rest.service.WebSocketService;

import java.util.Map;

@RestController
@RequestMapping("/v1/localizacao")
@CrossOrigin(origins = "*")
public class LocalizacaoController {

    @Autowired
    private WebSocketService webSocketService;

    @PostMapping(produces = "application/json")
    public ResponseEntity<?> receberLocalizacao(@RequestBody Map<String, Object> payload) {
        webSocketService.notificarLocalizacaoMotorista(payload);
        return ResponseEntity.ok().build();
    }
}