package wolvesofdelivery.api.rest.controller;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import wolvesofdelivery.api.rest.model.Corridas;
import wolvesofdelivery.api.rest.model.Usuario;
import wolvesofdelivery.api.rest.repository.CorridasRepository;
import wolvesofdelivery.api.rest.repository.UsuarioRepository;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/v1/corrida")
@CrossOrigin(origins = "*")
public class CorridaController {

	@Autowired
	private CorridasRepository corridasRepository;
	@Autowired
	private UsuarioRepository usuarioRepository;

	private final SimpMessagingTemplate messagingTemplate;

	public CorridaController(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}

	@SuppressWarnings("null")
	@CacheEvict(value = "cacheUser", allEntries = true) // SE TIVER CACHE QUE NAO É USADO VAI REMOVER
	@CachePut("cacheUser") // TEM MUDANÇA? VAI TRAZER E COLOCAR NO CACHE
	@PostMapping(value = "/createRace/{usuarioId}", produces = "application/json")
	public ResponseEntity<?> criarCorrida(@PathVariable Long usuarioId) {
		Corridas corridas = new Corridas();
		corridas.setData_chamada(new Timestamp(System.currentTimeMillis()));
		corridas.setStatus_corrida("EM ANDAMENTO");

		// BUSCA O USUARIO
		Usuario usuario = usuarioRepository.findById(usuarioId)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

		// SALVA O ID NO ID USUARIO E NO ID CLIENTE
		corridas.setUsuario(usuario);
		corridas.setCliente(usuario);

		Corridas criada = (Corridas) corridasRepository.save(corridas);

		// RETORNA A CORRIDA COM O NOME DO ESTABELECIMENTO
		Map<String, Object> response = new HashMap<>();
		response.put("corrida", criada);
		response.put("estabelecimento", usuario.getNome());

		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	// CORRIDAS DOS MOTORISTAS
	@CacheEvict(value = "cacheUser", allEntries = true) // SE TIVER CACHE QUE NAO É USADO VAI REMOVER
	@CachePut("cacheUser") // TEM MUDANÇA? VAI TRAZER E COLOCAR NO CACHE
	@GetMapping(value = "/raceDrive/{motoristaId}", produces = "application/json")
	public ResponseEntity<?> corridasMotorista(@PathVariable Long motoristaId) {
		List<Corridas> corridas = corridasRepository.findByMotorista_IdOrderByIdDesc(motoristaId);
		return new ResponseEntity<>(corridas, HttpStatus.OK);
	}

	// CORRIDAS DO DESPACHANTE
	@CacheEvict(value = "cacheUser", allEntries = true) // SE TIVER CACHE QUE NAO É USADO VAI REMOVER
	@CachePut("cacheUser") // TEM MUDANÇA? VAI TRAZER E COLOCAR NO CACHE
	@GetMapping(value = "/raceDespatcher/{clienteId}", produces = "application/json")
	public ResponseEntity<?> corridasDespachante(@PathVariable Long clienteId) {
		List<Corridas> corridas = corridasRepository.findByCliente_IdOrderByIdDesc(clienteId);
		return new ResponseEntity<>(corridas, HttpStatus.OK);
	}

	// CORRIDAS DOS ADMIN (TODAS)
	@CacheEvict(value = "cacheUser", allEntries = true) // SE TIVER CACHE QUE NAO É USADO VAI REMOVER
	@CachePut("cacheUser") // TEM MUDANÇA? VAI TRAZER E COLOCAR NO CACHE
	@GetMapping(value = "/allRace", produces = "application/json")
	public ResponseEntity<?> todasCorridas() {
		List<Corridas> corridas = corridasRepository.findAllByOrderByIdDesc();
		return new ResponseEntity<>(corridas, HttpStatus.OK);
	}

	@CacheEvict(value = "cacheUser", allEntries = true) // SE TIVER CACHE QUE NAO É USADO VAI REMOVER
	@CachePut("cacheUser") // TEM MUDANÇA? VAI TRAZER E COLOCAR NO CACHE
	@PatchMapping(value = "/updateRace/{corridaId}", produces = "application/json")
	public ResponseEntity<?> atualizarCorrida(@PathVariable Long corridaId) {
		// busca a corrida
		Corridas corrida = corridasRepository.findById(corridaId)
				.orElseThrow(() -> new RuntimeException("Corrida não encontrada"));

		if (corrida.getInicio_corrida() == null) {
			// primeira vez — adiciona inicio
			corrida.setInicio_corrida(new Timestamp(System.currentTimeMillis()));
		} else {
			// segunda vez — adiciona termino
			corrida.setTermino_corrida(new Timestamp(System.currentTimeMillis()));
			corrida.setStatus_corrida("FINALIZADA");
		}

		corridasRepository.save(corrida);
		// avisa o despachante via WebSocket
		messagingTemplate.convertAndSend("/topic/corrida", corridaId);
		return ResponseEntity.ok("Corrida atualizada!");
	}

}