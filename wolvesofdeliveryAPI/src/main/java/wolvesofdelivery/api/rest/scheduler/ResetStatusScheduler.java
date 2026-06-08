package wolvesofdelivery.api.rest.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import wolvesofdelivery.api.rest.repository.UsuarioRepository;
import wolvesofdelivery.api.rest.service.WebSocketService;

@Component
public class ResetStatusScheduler {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private WebSocketService webSocketService;

    // EXECUTA TODO DIA AS 05:00 DA MANHÃ
    @Scheduled(cron = "0 10 16 * * *")
    public void resetarStatusMotoristas() {
        System.out.println("Resetando status dos motoristas para OFFLINE...");

        // SETA TODOS OS MOTORISTAS PARA OFFLINE
        usuarioRepository.findByTipoUserOrderByNomeAsc("MOTORISTA")
            .forEach(motorista -> {
                motorista.setStatus(0L);
                usuarioRepository.save(motorista);
            });

        // SETA TODOS OS CLIENTES PARA OFFLINE
        usuarioRepository.findByTipoUserOrderByNomeAsc("CLIENTE")
            .forEach(cliente -> {
                cliente.setStatus(0L);
                usuarioRepository.save(cliente);
            });

        // NOTIFICA VIA WEBSOCKET
        webSocketService.notificarAtualizacaoFila();

        System.out.println("Reset concluído!");
    }
}