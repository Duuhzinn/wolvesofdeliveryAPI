package wolvesofdelivery.api.rest.controller;

import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wolvesofdelivery.api.rest.model.Corridas;
import wolvesofdelivery.api.rest.model.Firebasetoken;
import wolvesofdelivery.api.rest.model.Usuario;
import wolvesofdelivery.api.rest.repository.CorridasRepository;
import wolvesofdelivery.api.rest.repository.FirebasetokenRepository;
import wolvesofdelivery.api.rest.repository.UsuarioRepository;
import wolvesofdelivery.api.rest.service.FirebaseNotificationService;

//LIBERANDO O ACESSO PARA QUALQUER SISTEMA
@CrossOrigin(origins = "*")

@RestController
@RequestMapping(value = "/v1/pushnotification")
public class PushNotificationController {

	@Autowired
	private CorridasRepository corridasRepository;
	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private FirebasetokenRepository firebasetokenRepository;
	@Autowired
	private FirebaseNotificationService firebaseNotificationService;
	@Autowired
    private SimpMessagingTemplate messagingTemplate;

	@PostMapping(value = "/send/{usuarioId}/{despachanteId}", produces = "application/json")
	public ResponseEntity<?> enviarNotificacaoSemCorrida(@PathVariable Long usuarioId, @PathVariable Long despachanteId) {
	    Optional<Usuario> optional = usuarioRepository.findById(usuarioId);
	    if (optional.isPresent()) {
	        Firebasetoken firebasetoken = firebasetokenRepository.findByUsuarioId(usuarioId);
	        if (firebasetoken == null) {
	            return ResponseEntity.badRequest().body("Usuário sem token Firebase");
	        }
	        String resposta = firebaseNotificationService.enviarNotificacao(firebasetoken.getToken(),
	                "Nova Corrida 🏍️", "Você tem uma nova corrida disponível!", 0L, despachanteId);
	        
	        // NOTIFICA VIA WEBSOCKET
	        messagingTemplate.convertAndSend("/topic/fila", usuarioId);
	        return ResponseEntity.ok(resposta);
	    } else {
	        return ResponseEntity.badRequest().body("Usuário não encontrado");
	    }
	}
	
	@CacheEvict(value = "cacheUser", allEntries = true) // SE TIVER CACHE QUE NAO É USADO VAI REMOVER
	@CachePut("cacheUser") // TEM MUDANÇA? VAI TRAZER E COLOCAR NO CACHE
	@PostMapping(value = "/lostRace/{usuarioId}/{corridaId}", produces = "application/json")
	public ResponseEntity<?> corridaPerdida(@PathVariable Long usuarioId, @PathVariable Long corridaId){
		Optional<Usuario> optional = usuarioRepository.findById(usuarioId);
		if (optional.isPresent()) {
			Usuario usuario = optional.get();
			Firebasetoken firebasetoken = firebasetokenRepository.findByUsuarioId(usuarioId);
			if(firebasetoken == null) {
				return ResponseEntity.badRequest().body("Usuário sem tokem FireBase");
			}
			String resposta = firebaseNotificationService.enviarNotificacao(firebasetoken.getToken(), 
				    "Corrida Perdida ❌", "Você perdeu a corrida n.º", corridaId, 0L);
			
	        // NOTIFICA VIA WEBSOCKET
	        messagingTemplate.convertAndSend("/topic/fila", usuarioId);
			return ResponseEntity.ok(resposta);
		} else {
			return ResponseEntity.badRequest().body("Usuário não encontrado");

		}
		
	}
	
	@CacheEvict(value = "cacheUser", allEntries = true) // SE TIVER CACHE QUE NAO É USADO VAI REMOVER
	@CachePut("cacheUser") // TEM MUDANÇA? VAI TRAZER E COLOCAR NO CACHE
	@PostMapping(value = "/createRace/{motoristaId}/{despachanteId}", produces = "application/json")
	public ResponseEntity<?> corridaAceita(@PathVariable Long motoristaId, @PathVariable Long despachanteId) {

		Usuario motorista = usuarioRepository.findById(motoristaId)
				.orElseThrow(() -> new RuntimeException("Motorista não encontrado"));

		Usuario despachante = usuarioRepository.findById(despachanteId)
				.orElseThrow(() -> new RuntimeException("Despachante não encontrado"));

		Corridas corrida = new Corridas();
		corrida.setMotorista(motorista);
		corrida.setCliente(despachante);
		corrida.setData_aceite(new Timestamp(System.currentTimeMillis()));
		corrida.setData_chamada(new Timestamp(System.currentTimeMillis()));
		corrida.setStatus_corrida("EM ANDAMENTO");
		corridasRepository.save(corrida);

		messagingTemplate.convertAndSend("/topic/corrida", motoristaId);
		return ResponseEntity.ok("Corrida aceita pelo motorista " + motoristaId);
	}
		
	

}
