package wolvesofdelivery.api.rest.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wolvesofdelivery.api.rest.model.ConfiguracaoCorrida;
import wolvesofdelivery.api.rest.model.CorridaExpirada;
import wolvesofdelivery.api.rest.model.Corridas;
import wolvesofdelivery.api.rest.model.Firebasetoken;
import wolvesofdelivery.api.rest.model.Usuario;
import wolvesofdelivery.api.rest.repository.ConfiguracaoCorridaRepository;
import wolvesofdelivery.api.rest.repository.CorridaExpiradaRepository;
import wolvesofdelivery.api.rest.repository.CorridasRepository;
import wolvesofdelivery.api.rest.repository.FirebasetokenRepository;
import wolvesofdelivery.api.rest.repository.UsuarioRepository;
import wolvesofdelivery.api.rest.service.FirebaseNotificationService;
import wolvesofdelivery.api.rest.service.MotoristaBloqueadoService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/v1/pushnotification")
public class PushNotificationController {

	@Autowired
	private CorridaExpiradaRepository corridaExpiradaRepository;
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
	@Autowired
	private ConfiguracaoCorridaRepository configuracaoCorridaRepository;
	@Autowired
	private MotoristaBloqueadoService motoristaBloqueadoService;

	@PostMapping(value = "/send/{usuarioId}/{despachanteId}", produces = "application/json")
	public ResponseEntity<?> enviarNotificacaoSemCorrida(@PathVariable Long usuarioId,
			@PathVariable Long despachanteId,
			@RequestBody Map<String, String> body) {

		Optional<Usuario> optional = usuarioRepository.findById(usuarioId);
		if (!optional.isPresent()) {
			return ResponseEntity.badRequest().body("Usuário não encontrado");
		}
		
		// VERIFICA SE O MOTORISTA ESTÁ BLOQUEADO POR ESSE ESTABELECIMENTO
		if (motoristaBloqueadoService.isMotoristaBloqueado(despachanteId, usuarioId)) {
			return ResponseEntity.badRequest().body("Motorista bloqueado para este estabelecimento");
		}

		Firebasetoken firebasetoken = firebasetokenRepository.findByUsuarioId(usuarioId);
		if (firebasetoken == null) {
			return ResponseEntity.badRequest().body("Usuário sem token Firebase");
		}

		// VERIFICA SE O MOTORISTA JÁ ESTÁ COM CORRIDA AGUARDANDO
		boolean temCorridaAguardando = corridasRepository
				.existsByMotoristaIdAndStatusCorrida(usuarioId, "AGUARDANDO");
		
		if (temCorridaAguardando) {
			return ResponseEntity.badRequest().body("Motorista já possui corrida aguardando");
		}

		Usuario motorista = optional.get();
		Usuario despachante = usuarioRepository.findById(despachanteId)
				.orElseThrow(() -> new RuntimeException("Despachante não encontrado"));
		String endereco = body.get("endereco");

		ConfiguracaoCorrida configuracaoCorrida = configuracaoCorridaRepository.findByUsuarioId(despachanteId);
		BigDecimal valorCorrida = (configuracaoCorrida != null && configuracaoCorrida.getValor() != null)
				? configuracaoCorrida.getValor() : BigDecimal.ZERO;

		Corridas corrida = new Corridas();
		corrida.setMotorista(motorista);
		corrida.setCliente(despachante);
		corrida.setUsuario(despachante);
		corrida.setData_chamada(new Timestamp(System.currentTimeMillis()));
		corrida.setStatus_corrida("AGUARDANDO");
		corrida.setEndereco_entrega(endereco);
		corrida.setValor_corrida(valorCorrida);
		Corridas corridaSalva = corridasRepository.save(corrida);

		String resposta = firebaseNotificationService.enviarNotificacao(firebasetoken.getToken(),
				"Nova Corrida 🏍️", "Você tem uma nova corrida disponível!", corridaSalva.getId(), despachanteId);

		messagingTemplate.convertAndSend("/topic/fila", usuarioId);
		return ResponseEntity.ok(Map.of("corridaId", corridaSalva.getId(), "resposta", resposta));
	}

	@PostMapping(value = "/sendMultiple/{usuarioId}/{despachanteId}", produces = "application/json")
	public ResponseEntity<?> enviarNotificacaoMultipla(@PathVariable Long usuarioId,
	        @PathVariable Long despachanteId,
	        @RequestBody Map<String, Object> body) {

	    Optional<Usuario> optional = usuarioRepository.findById(usuarioId);
	    if (!optional.isPresent()) {
	        return ResponseEntity.badRequest().body("Usuário não encontrado");
	    }
	    
		// VERIFICA SE O MOTORISTA ESTÁ BLOQUEADO POR ESSE ESTABELECIMENTO
		if (motoristaBloqueadoService.isMotoristaBloqueado(despachanteId, usuarioId)) {
			return ResponseEntity.badRequest().body("Motorista bloqueado para este estabelecimento");
		}

		Firebasetoken firebasetoken = firebasetokenRepository.findByUsuarioId(usuarioId);
	    if (firebasetoken == null) {
	        return ResponseEntity.badRequest().body("Usuário sem token Firebase");
	    }

	    // VERIFICA SE O MOTORISTA JÁ ESTÁ COM CORRIDA AGUARDANDO
	    boolean temCorridaAguardando = corridasRepository
	    		.existsByMotoristaIdAndStatusCorrida(usuarioId, "AGUARDANDO");
	    if (temCorridaAguardando) {
	        return ResponseEntity.badRequest().body("Motorista já possui corrida aguardando");
	    }

	    Usuario motorista = optional.get();
	    Usuario despachante = usuarioRepository.findById(despachanteId)
	            .orElseThrow(() -> new RuntimeException("Despachante não encontrado"));

	    ConfiguracaoCorrida configuracaoCorrida = configuracaoCorridaRepository.findByUsuarioId(despachanteId);
	    BigDecimal valorCorrida = (configuracaoCorrida != null && configuracaoCorrida.getValor() != null)
	            ? configuracaoCorrida.getValor() : BigDecimal.ZERO;

	    @SuppressWarnings("unchecked")
	    List<String> enderecos = (List<String>) body.get("enderecos");

	    List<Long> corridaIds = new java.util.ArrayList<>();
	    for (String endereco : enderecos) {
	        Corridas corrida = new Corridas();
	        corrida.setMotorista(motorista);
	        corrida.setCliente(despachante);
	        corrida.setUsuario(despachante);
	        corrida.setData_chamada(new Timestamp(System.currentTimeMillis()));
	        corrida.setStatus_corrida("AGUARDANDO");
	        corrida.setEndereco_entrega(endereco);
	        corrida.setValor_corrida(valorCorrida);
	        Corridas salva = corridasRepository.save(corrida);
	        corridaIds.add(salva.getId());
	    }

	    String resposta = firebaseNotificationService.enviarNotificacaoMultipla(
	            firebasetoken.getToken(),
	            "Nova Corrida 🏍️",
	            "Você tem uma nova corrida disponível!",
	            corridaIds,
	            despachanteId);

	    messagingTemplate.convertAndSend("/topic/fila", usuarioId);
	    return ResponseEntity.ok(Map.of("corridaIds", corridaIds, "resposta", resposta));
	}

	@CacheEvict(value = "cacheUser", allEntries = true)
	@CachePut("cacheUser")
	@PostMapping(value = "/lostRace/{usuarioId}/{corridaId}", produces = "application/json")
	public ResponseEntity<?> corridaPerdida(@PathVariable Long usuarioId, @PathVariable Long corridaId) {
		Optional<Usuario> optional = usuarioRepository.findById(usuarioId);
		if (optional.isPresent()) {
			Firebasetoken firebasetoken = firebasetokenRepository.findByUsuarioId(usuarioId);
			if (firebasetoken == null) {
				return ResponseEntity.badRequest().body("Usuário sem tokem FireBase");
			}

			Corridas corrida = corridasRepository.findById(corridaId)
                .orElseThrow(() -> new RuntimeException("Corrida não encontrada"));
			CorridaExpirada expirada = new CorridaExpirada();
			expirada.setMotorista(optional.get());
			expirada.setCorrida(corrida);
			expirada.setDataExpirada(new Timestamp(System.currentTimeMillis()));
			corridaExpiradaRepository.save(expirada);

			String resposta = firebaseNotificationService.enviarNotificacaoPerdida(firebasetoken.getToken(),
					"Corrida Perdida ❌", "Você perdeu uma corrida", corridaId);
			messagingTemplate.convertAndSend("/topic/fila", usuarioId);
			return ResponseEntity.ok(resposta);
		} else {
			return ResponseEntity.badRequest().body("Usuário não encontrado");
		}
	}

	@CacheEvict(value = "cacheUser", allEntries = true)
	@CachePut("cacheUser")
	@PatchMapping(value = "/acceptRace/{corridaId}", produces = "application/json")
	public ResponseEntity<?> corridaAceita(@PathVariable Long corridaId) {
		Corridas corrida = corridasRepository.findById(corridaId)
				.orElseThrow(() -> new RuntimeException("Corrida não encontrada"));

		corrida.setData_aceite(new Timestamp(System.currentTimeMillis()));
		corrida.setStatus_corrida("EM ANDAMENTO");
		corridasRepository.save(corrida);

		messagingTemplate.convertAndSend("/topic/corrida", corrida.getMotorista().getId());
		return ResponseEntity.ok("Corrida aceita pelo motorista " + corrida.getMotorista().getId());
	}

	@CacheEvict(value = "cacheUser", allEntries = true)
	@CachePut("cacheUser")
	@PatchMapping(value = "/acceptMultiple", produces = "application/json")
	public ResponseEntity<?> corridasAceitas(@RequestBody List<Long> corridaIds) {
	    for (Long corridaId : corridaIds) {
	        Corridas corrida = corridasRepository.findById(corridaId)
	                .orElseThrow(() -> new RuntimeException("Corrida não encontrada: " + corridaId));
	        corrida.setData_aceite(new Timestamp(System.currentTimeMillis()));
	        corrida.setStatus_corrida("EM ANDAMENTO");
	        corridasRepository.save(corrida);
	    }

	    Long motoristaId = corridasRepository.findById(corridaIds.get(0))
	            .orElseThrow().getMotorista().getId();

	    messagingTemplate.convertAndSend("/topic/corrida", motoristaId);
	    return ResponseEntity.ok("Corridas aceitas: " + corridaIds);
	}
}