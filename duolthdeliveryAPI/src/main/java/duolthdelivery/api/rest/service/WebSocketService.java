package duolthdelivery.api.rest.service;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
@Service
public class WebSocketService {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    public void notificarAtualizacaoFila() {
        messagingTemplate.convertAndSend("/topic/fila", "atualizar");
    }
    
    public void notificarRecusaMotorista(Long proximoMotoristaId) {
        messagingTemplate.convertAndSend("/topic/recusa", 
            proximoMotoristaId != null ? proximoMotoristaId.toString() : "null");
    }
    
    public void notificarLocalizacaoMotorista(Map<String, Object> payload) {
        messagingTemplate.convertAndSend("/topic/localizacao", (Object) payload);
    }
}