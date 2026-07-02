package wolvesofdelivery.api.rest.controller;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import wolvesofdelivery.api.rest.model.CorridaRecusada;
import wolvesofdelivery.api.rest.model.Corridas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wolvesofdelivery.api.rest.model.Firebasetoken;
import wolvesofdelivery.api.rest.model.Usuario;
import wolvesofdelivery.api.rest.repository.CorridasRecusadaRepository;
import wolvesofdelivery.api.rest.repository.CorridasRepository;
import wolvesofdelivery.api.rest.repository.FirebasetokenRepository;
import wolvesofdelivery.api.rest.repository.UsuarioRepository;
import wolvesofdelivery.api.rest.service.FirebaseNotificationService;
import wolvesofdelivery.api.rest.service.MotoristaBloqueadoService;
import wolvesofdelivery.api.rest.service.WebSocketService;

//LIBERANDO O ACESSO PARA QUALQUER SISTEMA
@CrossOrigin(origins = "*")

@RestController
@RequestMapping(value = "/v1/drive")
public class MotoristaController {

	@Autowired
	private CorridasRecusadaRepository corridasRecusadaRepository;
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	@Autowired
	private FirebasetokenRepository firebasetokenRepository;
	@Autowired
	private FirebaseNotificationService firebaseNotificationService;
	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private CorridasRepository corridasRepository;
	@Autowired
	private WebSocketService webSocketService;
	@Autowired
	private MotoristaBloqueadoService motoristaBloqueadoService;
	
	//BUSCAO O ID DO USUARIO ONLINE
	@CacheEvict(value = "cacheUser", allEntries = true) // SE TIVER CACHE QUE NAO É USADO VAI REMOVER
	@CachePut("cacheUser") // TEM MUDANÇA? VAI TRAZER E COLOCAR NO CACHE
	@GetMapping(value = "/status/{id}", produces = "application/json")
	public ResponseEntity<?> buscarUsuarioPorId(@PathVariable Long id) {
	    Optional<Usuario> optional = usuarioRepository.findById(id);
	    if (!optional.isPresent()) {
	        return ResponseEntity.badRequest().body("Usuário não encontrado");
	    }
	    return ResponseEntity.ok(optional.get());
	}

	// ____________CONSULTANDO USUÁRIO(MOTORISTA ONLINE e OFFLINE)_____________________//
	@CacheEvict(value = "cacheUser", allEntries = true) // SE TIVER CACHE QUE NAO É USADO VAI REMOVER
	@CachePut("cacheUser") // TEM MUDANÇA? VAI TRAZER E COLOCAR NO CACHE
	@GetMapping(value = "/allDrive", produces = "application/json")
	public ResponseEntity<List<Usuario>> allDriver() {
		List<Usuario> list = usuarioRepository.findByTipoUserOrderByNomeAsc("MOTORISTA");
		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}

	// ____________ALTERANDO STATUS DO USUÁRIO
	// SELECIONADO____________________________//
	@CacheEvict(value = "cacheUser", allEntries = true) // SE TIVER CACHE QUE NAO É USADO VAI REMOVER
	@CachePut("cacheUser") // TEM MUDANÇA? VAI TRAZER E COLOCAR NO CACHE
	@PatchMapping(value = "/changeStatus/{id}", produces = "application/json")
	public ResponseEntity<Usuario> alterarStatus(@PathVariable Long id) {
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
		if (usuario.getStatus() == 1L) {
			usuario.setStatus(0L);
		} else {
			usuario.setStatus(1L);
			usuario.setPosicaofila(new Timestamp(System.currentTimeMillis()));
		}
		Usuario atualizarusuario = usuarioRepository.save(usuario);
		webSocketService.notificarAtualizacaoFila();
		return new ResponseEntity<Usuario>(atualizarusuario, HttpStatus.OK);
	}

	// _____________LISTANDO ORDEM DA FILA DOS MOTORISTAS_____________________//
	@CacheEvict(value = "cacheUser", allEntries = true) // SE TIVER CACHE QUE NAO É USADO VAI REMOVER
	@CachePut("cacheUser") // TEM MUDANÇA? VAI TRAZER E COLOCAR NO CACHE
	@GetMapping(value = "/driverQueue", produces = "application/json")
	public ResponseEntity<List<Usuario>> driverQueue() {
		List<Usuario> list = usuarioRepository.findByTipoUserAndStatusOrderByPosicaofilaAsc("MOTORISTA", 1L);
		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}

	// _____________BUSCANDO O PRIMEIRO DA FILA DOS MOTORISTAS
	// ONLINE_____________________//
	@CacheEvict(value = "cacheUser", allEntries = true) // SE TIVER CACHE QUE NAO É USADO VAI REMOVER
	@CachePut("cacheUser") // TEM MUDANÇA? VAI TRAZER E COLOCAR NO CACHE
	@GetMapping(value = "/driverQueue/firstid", produces = "application/json")
	public ResponseEntity<Long> getNextDriverId() {
		Usuario usuario = usuarioRepository.findTop1ByTipoUserAndStatusOrderByPosicaofilaAsc("MOTORISTA", 1L);
		if (usuario == null) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(usuario.getId());
	}

	// ____________ALTERANDO STATUS DO MOTORISTA PARA OCUPADO___________________//
	@CacheEvict(value = "cacheUser", allEntries = true) // SE TIVER CACHE QUE NAO É USADO VAI REMOVER
	@CachePut("cacheUser") // TEM MUDANÇA? VAI TRAZER E COLOCAR NO CACHE
	@PatchMapping(value = "/busy/{id}", produces = "application/json")
	public ResponseEntity<Usuario> ocupado(@PathVariable Long id) {
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

		usuario.setStatus(2L);

		Usuario atualizarusuario = usuarioRepository.save(usuario);
		webSocketService.notificarAtualizacaoFila();
		return new ResponseEntity<Usuario>(atualizarusuario, HttpStatus.OK);
	}

	// ALTERANDO O STATUS PARA CHAMANDO O MOTORISTA
	@CacheEvict(value = "cacheUser", allEntries = true) // SE TIVER CACHE QUE NAO É USADO VAI REMOVER
	@CachePut("cacheUser") // TEM MUDANÇA? VAI TRAZER E COLOCAR NO CACHE
	@PatchMapping(value = "/callingDrive/{id}", produces = "application/json")
	public ResponseEntity<?> chamandoMotorista(@PathVariable Long id) {
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Motorista não encontrado"));
		usuario.setStatus(3L);
		usuarioRepository.save(usuario);
		return ResponseEntity.ok().build();
	}

	// ALTERA O STATUS DO MOTORISTA PARA OFFLINE
	@CacheEvict(value = "cacheUser", allEntries = true) // SE TIVER CACHE QUE NAO É USADO VAI REMOVER
	@CachePut("cacheUser") // TEM MUDANÇA? VAI TRAZER E COLOCAR NO CACHE
	@PatchMapping(value = "/signOffline/{id}", produces = "application/json")
	public ResponseEntity<?> marcarOffline(@PathVariable Long id) {
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Motorista não encontrado"));
		usuario.setStatus(0L);
		usuarioRepository.save(usuario);
		webSocketService.notificarAtualizacaoFila();
		return ResponseEntity.ok().build();
	}

	// MOTORISTA RECUSOU A CORRIDA - VOLTA PARA O FIM DA FILA
	@CacheEvict(value = "cacheUser", allEntries = true)
	@PatchMapping(value = "/recusarCorrida/{motoristaId}/{corridaId}/{despachanteId}", produces = "application/json")
	public ResponseEntity<?> recusarCorrida(@PathVariable Long motoristaId, @PathVariable Long corridaId,
			@PathVariable Long despachanteId) {

		// 1 - JOGA O MOTORISTA QUE RECUSOU PARA O FIM DA FILA
		Usuario motorista = usuarioRepository.findById(motoristaId)
				.orElseThrow(() -> new RuntimeException("Motorista não encontrado"));
		motorista.setStatus(1L);
		motorista.setPosicaofila(new Timestamp(System.currentTimeMillis()));
		usuarioRepository.save(motorista);
		webSocketService.notificarAtualizacaoFila();

		// REGISTRA A RECUSA
		Corridas corrida = corridasRepository.findById(corridaId)
				.orElseThrow(() -> new RuntimeException("Corrida não encontrada"));
		CorridaRecusada corridaRecusada = new CorridaRecusada();
		corridaRecusada.setMotorista(motorista);
		corridaRecusada.setCorrida(corrida);
		corridaRecusada.setDataRecusa(new Timestamp(System.currentTimeMillis()));
		corridasRecusadaRepository.save(corridaRecusada);

		// 2 - BUSCA O PROXIMO MOTORISTA DA FILA, PULANDO O QUE RECUSOU E OS BLOQUEADOS PARA ESSE ESTABELECIMENTO
	    List<Usuario> filaOnline = usuarioRepository
	            .findByTipoUserAndStatusOrderByPosicaofilaAsc("MOTORISTA", 1L);

	    Usuario proximoMotorista = null;
	    for (Usuario usuario : filaOnline) {
	        if (usuario.getId().equals(motoristaId)) {
	            continue;
	        }
	        if (motoristaBloqueadoService.isMotoristaBloqueado(despachanteId, usuario.getId())) {
	            continue;
	        }
	        proximoMotorista = usuario;
	        break;
	    }
	    webSocketService.notificarRecusaMotorista(proximoMotorista != null ? proximoMotorista.getId() : null);

		if (proximoMotorista != null) {
			// 3 - ATUALIZA A CORRIDA COM O NOVO MOTORISTA
			corrida.setMotorista(proximoMotorista);
			corridasRepository.save(corrida);

			// 4 - MARCA O PROXIMO COMO CHAMANDO
			proximoMotorista.setStatus(3L);
			usuarioRepository.save(proximoMotorista);

			// 5 - ENVIA NOTIFICAÇÃO PARA O PROXIMO MOTORISTA
			Firebasetoken firebasetoken = firebasetokenRepository.findByUsuarioId(proximoMotorista.getId());
			if (firebasetoken != null) {
				firebaseNotificationService.enviarNotificacao(firebasetoken.getToken(), "Nova Corrida 🏍️",
						"Você tem uma nova corrida disponível!", 0L, despachanteId);
			}

			return ResponseEntity.ok(Map.of("proximoMotoristaId", proximoMotorista.getId()));

		} else {
			return ResponseEntity.ok(Map.of("proximoMotoristaId", (Object) null));
		}
	}

	// MOTORISTA RECUSOU MÚLTIPLAS CORRIDAS
	@CacheEvict(value = "cacheUser", allEntries = true)
	@PatchMapping(value = "/recusarMultiplas/{motoristaId}/{despachanteId}", produces = "application/json")
	public ResponseEntity<?> recusarMultiplas(@PathVariable Long motoristaId, @PathVariable Long despachanteId,
			@RequestBody List<Long> corridaIds) {

		// 1 - JOGA O MOTORISTA QUE RECUSOU PARA O FIM DA FILA
		Usuario motorista = usuarioRepository.findById(motoristaId)
				.orElseThrow(() -> new RuntimeException("Motorista não encontrado"));
		motorista.setStatus(1L);
		motorista.setPosicaofila(new Timestamp(System.currentTimeMillis()));
		usuarioRepository.save(motorista);
		webSocketService.notificarAtualizacaoFila();

		// 2 - REGISTRA A RECUSA DE CADA CORRIDA
		for (Long corridaId : corridaIds) {
			Corridas corrida = corridasRepository.findById(corridaId)
					.orElseThrow(() -> new RuntimeException("Corrida não encontrada: " + corridaId));
			CorridaRecusada corridaRecusada = new CorridaRecusada();
			corridaRecusada.setMotorista(motorista);
			corridaRecusada.setCorrida(corrida);
			corridaRecusada.setDataRecusa(new Timestamp(System.currentTimeMillis()));
			corridasRecusadaRepository.save(corridaRecusada);
		}

		// 3 - BUSCA O PRÓXIMO MOTORISTA DA FILA, PULANDO O QUE RECUSOU E OS BLOQUEADOS PARA ESSE ESTABELECIMENTO
	    List<Usuario> filaOnline = usuarioRepository
	            .findByTipoUserAndStatusOrderByPosicaofilaAsc("MOTORISTA", 1L);

	    Usuario proximoMotorista = null;
	    for (Usuario usuario : filaOnline) {
	        if (usuario.getId().equals(motoristaId)) {
	            continue;
	        }
	        if (motoristaBloqueadoService.isMotoristaBloqueado(despachanteId, usuario.getId())) {
	            continue;
	        }
	        proximoMotorista = usuario;
	        break;
	    }
	    webSocketService.notificarRecusaMotorista(proximoMotorista != null ? proximoMotorista.getId() : null);

		if (proximoMotorista != null) {
			// 4 - ATUALIZA TODAS AS CORRIDAS COM O NOVO MOTORISTA
			for (Long corridaId : corridaIds) {
				Corridas corrida = corridasRepository.findById(corridaId).orElseThrow();
				corrida.setMotorista(proximoMotorista);
				corridasRepository.save(corrida);
			}

			// 5 - MARCA O PRÓXIMO COMO CHAMANDO
			proximoMotorista.setStatus(3L);
			usuarioRepository.save(proximoMotorista);

			// 6 - ENVIA NOTIFICAÇÃO PARA O PRÓXIMO MOTORISTA
			Firebasetoken firebasetoken = firebasetokenRepository.findByUsuarioId(proximoMotorista.getId());
			if (firebasetoken != null) {
				firebaseNotificationService.enviarNotificacao(firebasetoken.getToken(), "Nova Corrida 🏍️",
						corridaIds.size() + " entrega(s) disponível!", corridaIds.get(0), despachanteId);
			}

			return ResponseEntity.ok(Map.of("proximoMotoristaId", proximoMotorista.getId()));
		} else {
			return ResponseEntity.ok(Map.of("proximoMotoristaId", (Object) null));
		}
	}
}
