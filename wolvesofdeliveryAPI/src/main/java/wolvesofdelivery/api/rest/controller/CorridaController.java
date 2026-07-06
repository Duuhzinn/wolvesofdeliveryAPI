package wolvesofdelivery.api.rest.controller;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import wolvesofdelivery.api.rest.model.Corridas;
import wolvesofdelivery.api.rest.model.Firebasetoken;
import wolvesofdelivery.api.rest.model.Usuario;
import wolvesofdelivery.api.rest.repository.CorridaExpiradaRepository;
import wolvesofdelivery.api.rest.repository.CorridasRecusadaRepository;
import wolvesofdelivery.api.rest.repository.CorridasRepository;
import wolvesofdelivery.api.rest.repository.FirebasetokenRepository;
import wolvesofdelivery.api.rest.repository.UsuarioRepository;
import wolvesofdelivery.api.rest.service.FirebaseNotificationService;
import wolvesofdelivery.api.rest.service.WebSocketService;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/v1/corrida")
@CrossOrigin(origins = "*")
public class CorridaController {

	@Autowired
	private CorridasRecusadaRepository corridasRecusadaRepository;
	@Autowired
	private CorridaExpiradaRepository corridaExpiradaRepository;
	@Autowired
	private CorridasRepository corridasRepository;
	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private WebSocketService webSocketService;
	@Autowired
	private FirebasetokenRepository firebasetokenRepository;
	@Autowired
	private FirebaseNotificationService firebaseNotificationService;

	private final SimpMessagingTemplate messagingTemplate;

	public CorridaController(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}

	// CORRIDAS DOS MOTORISTAS EM ANDAMENTO
	@CacheEvict(value = "cacheUser", allEntries = true)
	@CachePut("cacheUser")
	@GetMapping(value = "/raceDrive/{motoristaId}", produces = "application/json")
	public ResponseEntity<?> corridasMotorista(@PathVariable Long motoristaId,
			@PageableDefault(size = 10) Pageable pageable) {
		Page<Corridas> corridas = corridasRepository.findByMotoristaIdAndStatusOrderByIdDesc(motoristaId,
				"EM ANDAMENTO", pageable);
		return new ResponseEntity<>(corridas, HttpStatus.OK);
	}

	// CORRIDAS DO DESPACHANTE EM ANDAMENTO
	@CacheEvict(value = "cacheUser", allEntries = true)
	@CachePut("cacheUser")
	@GetMapping(value = "/raceDespatcher/{clienteId}", produces = "application/json")
	public ResponseEntity<?> corridasDespachante(@PathVariable Long clienteId,
			@PageableDefault(size = 10) Pageable pageable) {
		Page<Corridas> corridas = corridasRepository.findByClienteIdAndStatusOrderByIdDesc(clienteId, "EM ANDAMENTO",
				pageable);
		return new ResponseEntity<>(corridas, HttpStatus.OK);
	}

	// CORRIDAS DOS ADMIN (TODAS) EM ANDAMENTO
	@CacheEvict(value = "cacheUser", allEntries = true)
	@CachePut("cacheUser")
	@GetMapping(value = "/allRace", produces = "application/json")
	public ResponseEntity<?> todasCorridas(@PageableDefault(size = 10) Pageable pageable) {
		Page<Corridas> corridas = corridasRepository.findByStatusOrderByIdDesc("EM ANDAMENTO", pageable);
		return new ResponseEntity<>(corridas, HttpStatus.OK);
	}

	// CORRIDAS DOS MOTORISTAS FINALIZADA
	@CacheEvict(value = "cacheUser", allEntries = true)
	@CachePut("cacheUser")
	@GetMapping(value = "/raceDriveFinished/{motoristaId}", produces = "application/json")
	public ResponseEntity<?> corridasMotoristaFinalizada(@PathVariable Long motoristaId,
			@PageableDefault(size = 10) Pageable pageable) {
		Page<Corridas> corridas = corridasRepository.findByMotoristaIdAndStatusOrderByIdDesc(motoristaId, "FINALIZADA",
				pageable);
		return new ResponseEntity<>(corridas, HttpStatus.OK);
	}

	// CORRIDAS DO DESPACHANTE FINALIZADA
	@CacheEvict(value = "cacheUser", allEntries = true)
	@CachePut("cacheUser")
	@GetMapping(value = "/raceDespatcherFinished/{clienteId}", produces = "application/json")
	public ResponseEntity<?> corridasDespachanteFinalizada(@PathVariable Long clienteId,
			@PageableDefault(size = 10) Pageable pageable) {
		Page<Corridas> corridas = corridasRepository.findByClienteIdAndStatusOrderByIdDesc(clienteId, "FINALIZADA",
				pageable);
		return new ResponseEntity<>(corridas, HttpStatus.OK);
	}

	// CORRIDAS DOS ADMIN (TODAS) FINALIZADA
	@CacheEvict(value = "cacheUser", allEntries = true)
	@CachePut("cacheUser")
	@GetMapping(value = "/allRaceFinished", produces = "application/json")
	public ResponseEntity<?> todasCorridasFinished(@PageableDefault(size = 10) Pageable pageable) {
		Page<Corridas> corridas = corridasRepository.findByStatusOrderByIdDesc("FINALIZADA", pageable);
		return new ResponseEntity<>(corridas, HttpStatus.OK);
	}

	@CacheEvict(value = "cacheUser", allEntries = true)
	@CachePut("cacheUser")
	@PatchMapping(value = "/updateRace/{corridaId}", produces = "application/json")
	public ResponseEntity<?> atualizarCorrida(@PathVariable Long corridaId) {
		Corridas corrida = corridasRepository.findById(corridaId)
				.orElseThrow(() -> new RuntimeException("Corrida não encontrada"));

		if (corrida.getInicio_corrida() == null) {
			corrida.setInicio_corrida(new Timestamp(System.currentTimeMillis()));
		} else {
			corrida.setTermino_corrida(new Timestamp(System.currentTimeMillis()));
			corrida.setStatus_corrida("FINALIZADA");

			Usuario motorista = corrida.getMotorista();
			if (motorista != null) {
				motorista.setStatus(1L);
				motorista.setPosicaofila(new Timestamp(System.currentTimeMillis()));
				usuarioRepository.save(motorista);
				webSocketService.notificarAtualizacaoFila();
			}
		}

		corridasRepository.save(corrida);
		messagingTemplate.convertAndSend("/topic/corrida", corridaId);
		return ResponseEntity.ok("Corrida atualizada!");
	}

	// CANCELANDO A CORRIDA
	@CacheEvict(value = "cacheUser", allEntries = true)
	@CachePut("cacheUser")
	@PatchMapping(value = "/cancelCall/{motoristaId}", produces = "application/json")
	public ResponseEntity<?> cancelarChamada(@PathVariable Long motoristaId) {
		Usuario motorista = usuarioRepository.findById(motoristaId)
				.orElseThrow(() -> new RuntimeException("Motorista não encontrado"));

		motorista.setStatus(1L);
		usuarioRepository.save(motorista);
		messagingTemplate.convertAndSend("/topic/cancelarChamada", motoristaId);
		webSocketService.notificarAtualizacaoFila();

		// CANCELA A NOTIFICAÇÃO NA BARRA DO MOTORISTA
		Firebasetoken firebasetoken = firebasetokenRepository.findByUsuarioId(motoristaId);
		if (firebasetoken != null) {
			firebaseNotificationService.enviarCancelamento(firebasetoken.getToken());
		}

		return ResponseEntity.ok("Chamada cancelada!");
	}
	
	// CANCELA MÚLTIPLAS CORRIDAS E LIBERA O MOTORISTA
	@CacheEvict(value = "cacheUser", allEntries = true)
	@CachePut("cacheUser")
	@PatchMapping(value = "/cancelMultiple/{motoristaId}", produces = "application/json")
	public ResponseEntity<?> cancelarMultiplas(@PathVariable Long motoristaId, @RequestBody List<Long> corridaIds) {
	    for (Long corridaId : corridaIds) {
	        Corridas corrida = corridasRepository.findById(corridaId)
	                .orElseThrow(() -> new RuntimeException("Corrida não encontrada: " + corridaId));
	        corrida.setStatus_corrida("EXPIRADA");
	        corrida.setTermino_corrida(new Timestamp(System.currentTimeMillis()));
	        corridasRepository.save(corrida);
	    }

	    Usuario motorista = usuarioRepository.findById(motoristaId)
	            .orElseThrow(() -> new RuntimeException("Motorista não encontrado"));
	    motorista.setStatus(1L);
	    usuarioRepository.save(motorista);
	    messagingTemplate.convertAndSend("/topic/cancelarChamada", motoristaId);
	    webSocketService.notificarAtualizacaoFila();

	    // CANCELA A NOTIFICAÇÃO NA BARRA DO MOTORISTA
	    Firebasetoken firebasetoken = firebasetokenRepository.findByUsuarioId(motoristaId);
	    if (firebasetoken != null) {
	        firebaseNotificationService.enviarCancelamento(firebasetoken.getToken());
	    }

	    return ResponseEntity.ok("Chamada cancelada!");
	}

	// CORRIDA EXPIRADA
	@CacheEvict(value = "cacheUser", allEntries = true)
	@CachePut("cacheUser")
	@PatchMapping(value = "/expireRace/{corridaId}", produces = "application/json")
	public ResponseEntity<?> cancelarCorrida(@PathVariable Long corridaId) {
		Corridas corrida = corridasRepository.findById(corridaId)
				.orElseThrow(() -> new RuntimeException("Corrida não encontrada"));
		corrida.setStatus_corrida("EXPIRADA");
		corrida.setTermino_corrida(new Timestamp(System.currentTimeMillis()));
		corridasRepository.save(corrida);
		return ResponseEntity.ok("Corrida cancelada!");
	}
	
	// EXPIRA MÚLTIPLAS CORRIDAS
	@CacheEvict(value = "cacheUser", allEntries = true)
	@CachePut("cacheUser")
	@PatchMapping(value = "/expireMultiple", produces = "application/json")
	public ResponseEntity<?> expirarMultiplas(@RequestBody List<Long> corridaIds) {
	    for (Long corridaId : corridaIds) {
	        Corridas corrida = corridasRepository.findById(corridaId)
	                .orElseThrow(() -> new RuntimeException("Corrida não encontrada: " + corridaId));
	        corrida.setStatus_corrida("EXPIRADA");
	        corrida.setTermino_corrida(new Timestamp(System.currentTimeMillis()));
	        corridasRepository.save(corrida);
	    }
	    return ResponseEntity.ok("Corridas expiradas: " + corridaIds);
	}

	// _________COMEÇA O ENDPOINT DE CIENCIA DE DADOS_________

	// ESTATÍSTICA POR ANO - MOTORISTA
	@Cacheable(value = "estatisticas", key = "#motoristaId + '-' + #ano")
	@GetMapping(value = "/estatisticas/motorista/{motoristaId}/{ano}", produces = "application/json")
	public ResponseEntity<?> estatisticasMotorista(@PathVariable Long motoristaId, @PathVariable int ano) {
		List<Map<String, Object>> resultado = new ArrayList<>();
		String[] meses = {"JANEIRO","FEVEREIRO","MARÇO","ABRIL","MAIO","JUNHO",
				"JULHO","AGOSTO","SETEMBRO","OUTUBRO","NOVEMBRO","DEZEMBRO"};

		for (int mes = 1; mes <= 12; mes++) {
			int diasNoMes = LocalDate.of(ano, mes, 1).lengthOfMonth();

			long totalCorridas = corridasRepository.countByMotoristaIdAndMesAno(motoristaId, mes, ano);
			long totalFinalizadas = corridasRepository.countByMotoristaIdAndStatusAndMesAno(motoristaId, "FINALIZADA", mes, ano);
			BigDecimal totalFaturado = corridasRepository.sumValorByMotoristaIdAndStatusAndMesAno(motoristaId, "FINALIZADA", mes, ano);

			Map<String, Object> card = new HashMap<>();
			card.put("mes", meses[mes - 1]);
			card.put("totalCorridas", totalCorridas);
			card.put("totalFaturado", totalFaturado);
			card.put("mediaDiaria", totalCorridas > 0 ? (double) totalCorridas / diasNoMes : 0);

			long totalPerdidas = corridaExpiradaRepository.countByMotoristaIdAndDataExpiradaBetween(motoristaId,
					Timestamp.valueOf(LocalDate.of(ano, mes, 1).atStartOfDay()),
					Timestamp.valueOf(LocalDate.of(ano, mes, LocalDate.of(ano, mes, 1).lengthOfMonth()).atTime(23, 59, 59)));
			long totalRecusadas = corridasRecusadaRepository.countByMotoristaIdAndDataRecusaBetween(motoristaId,
					Timestamp.valueOf(LocalDate.of(ano, mes, 1).atStartOfDay()),
					Timestamp.valueOf(LocalDate.of(ano, mes, LocalDate.of(ano, mes, 1).lengthOfMonth()).atTime(23, 59, 59)));

			card.put("totalPerdidas", totalPerdidas);
			card.put("totalRecusadas", totalRecusadas);

			long totalOportunidades = totalFinalizadas + totalPerdidas + totalRecusadas;
			double aproveitamento = totalOportunidades > 0 ? (double) totalFinalizadas / totalOportunidades * 100 : 0;
			card.put("aproveitamento", Math.round(aproveitamento * 10.0) / 10.0);
			resultado.add(card);
		}
		return new ResponseEntity<>(resultado, HttpStatus.OK);
	}

	// ESTATÍSTICAS POR ANO - ADMIN (TODOS)
	@Cacheable(value = "estatisticas", key = "'adm-' + #ano")
	@GetMapping(value = "/estatisticas/adm/{ano}", produces = "application/json")
	public ResponseEntity<?> estatisticasAdm(@PathVariable int ano) {
		List<Map<String, Object>> resultado = new ArrayList<>();
		String[] meses = {"JANEIRO","FEVEREIRO","MARÇO","ABRIL","MAIO","JUNHO",
				"JULHO","AGOSTO","SETEMBRO","OUTUBRO","NOVEMBRO","DEZEMBRO"};

		for (int mes = 1; mes <= 12; mes++) {
			int diasNoMes = LocalDate.of(ano, mes, 1).lengthOfMonth();

			long totalCorridas = corridasRepository.countByMesAno(mes, ano);
			long totalFinalizadas = corridasRepository.countByStatusAndMesAno("FINALIZADA", mes, ano);
			BigDecimal totalFaturado = corridasRepository.sumValorByStatusAndMesAno("FINALIZADA", mes, ano);
			String motoristaTop = corridasRepository.findMotoristaTopByMesAno(mes, ano);

			Map<String, Object> card = new HashMap<>();
			card.put("mes", meses[mes - 1]);
			card.put("totalCorridas", totalCorridas);
			card.put("totalFaturado", totalFaturado);
			card.put("mediaDiaria", totalCorridas > 0 ? (double) totalCorridas / diasNoMes : 0);

			long totalPerdidas = corridaExpiradaRepository.countByMesAno(mes, ano);
			long totalRecusadas = corridasRecusadaRepository.countByMesAno(mes, ano);

			long totalOportunidades = totalFinalizadas + totalPerdidas + totalRecusadas;
			double aproveitamento = totalOportunidades > 0 ? (double) totalFinalizadas / totalOportunidades * 100 : 0;

			card.put("totalPerdidas", totalPerdidas);
			card.put("totalRecusadas", totalRecusadas);
			card.put("aproveitamento", Math.round(aproveitamento * 10.0) / 10.0);
			card.put("motoristaTop", motoristaTop != null ? motoristaTop : "-");
			resultado.add(card);
		}

		return new ResponseEntity<>(resultado, HttpStatus.OK);
	}

	// ESTATÍSTICAS POR ANO - CLIENTE
	@Cacheable(value = "estatisticas", key = "#clienteId + '-' + #ano")
	@GetMapping(value = "/estatisticas/cliente/{clienteId}/{ano}", produces = "application/json")
	public ResponseEntity<?> estatisticasCliente(@PathVariable Long clienteId, @PathVariable int ano) {
		List<Map<String, Object>> resultado = new ArrayList<>();
		String[] meses = {"JANEIRO","FEVEREIRO","MARÇO","ABRIL","MAIO","JUNHO",
				"JULHO","AGOSTO","SETEMBRO","OUTUBRO","NOVEMBRO","DEZEMBRO"};

		for (int mes = 1; mes <= 12; mes++) {
			int diasNoMes = LocalDate.of(ano, mes, 1).lengthOfMonth();

			long totalCorridas = corridasRepository.countByClienteIdAndMesAno(clienteId, mes, ano);
			long totalFinalizadas = corridasRepository.countByClienteIdAndStatusAndMesAno(clienteId, "FINALIZADA", mes, ano);
			BigDecimal totalFaturado = corridasRepository.sumValorByClienteIdAndStatusAndMesAno(clienteId, "FINALIZADA", mes, ano);

			Map<String, Object> card = new HashMap<>();
			card.put("mes", meses[mes - 1]);
			card.put("totalCorridas", totalCorridas);
			card.put("totalFaturado", totalFaturado);
			card.put("mediaDiaria", totalCorridas > 0 ? (double) totalCorridas / diasNoMes : 0);

			double aproveitamento = totalCorridas > 0 ? (double) totalFinalizadas / totalCorridas * 100 : 0;
			card.put("aproveitamento", Math.round(aproveitamento * 10.0) / 10.0);
			resultado.add(card);
		}

		return new ResponseEntity<>(resultado, HttpStatus.OK);
	}

	// ESTATÍSTICAS POR PERÍODO - TODOS OS PERFIS
	@GetMapping(value = "/estatisticas/periodo", produces = "application/json")
	public ResponseEntity<?> estatisticasPorPeriodo(
			@RequestParam String inicio,
			@RequestParam String fim,
			@RequestParam(required = false) Long clienteId,
			@RequestParam(required = false) Long motoristaId) {

		Timestamp tsInicio = Timestamp.valueOf(inicio + " 00:00:00");
		Timestamp tsFim = Timestamp.valueOf(fim + " 23:59:59");

		long totalCorridas = 0;
		long totalFinalizadas = 0;
		long totalPerdidas = 0;
		long totalRecusadas = 0;
		BigDecimal totalFaturado = BigDecimal.ZERO;
		String motoristaTop = "-";

		if (clienteId != null) {
			totalCorridas = corridasRepository.countByClienteIdAndDataBetween(clienteId, tsInicio, tsFim);
			totalFinalizadas = corridasRepository.countByClienteIdAndStatusAndDataBetween(clienteId, "FINALIZADA", tsInicio, tsFim);
			totalFaturado = corridasRepository.sumValorByClienteIdAndStatusAndDataBetween(clienteId, "FINALIZADA", tsInicio, tsFim);
		} else if (motoristaId != null) {
			totalCorridas = corridasRepository.countByMotoristaIdAndDataBetween(motoristaId, tsInicio, tsFim);
			totalFinalizadas = corridasRepository.countByMotoristaIdAndStatusAndDataBetween(motoristaId, "FINALIZADA", tsInicio, tsFim);
			totalFaturado = corridasRepository.sumValorByMotoristaIdAndStatusAndDataBetween(motoristaId, "FINALIZADA", tsInicio, tsFim);
			totalPerdidas = corridaExpiradaRepository.countByMotoristaIdAndDataExpiradaBetween(motoristaId, tsInicio, tsFim);
			totalRecusadas = corridasRecusadaRepository.countByMotoristaIdAndDataRecusaBetween(motoristaId, tsInicio, tsFim);
		} else {
			totalCorridas = corridasRepository.countByDataBetween(tsInicio, tsFim);
			totalFinalizadas = corridasRepository.countByStatusAndDataBetween("FINALIZADA", tsInicio, tsFim);
			totalFaturado = corridasRepository.sumValorByStatusAndDataBetween("FINALIZADA", tsInicio, tsFim);
			totalPerdidas = corridaExpiradaRepository.countByDataExpiradaBetween(tsInicio, tsFim);
			totalRecusadas = corridasRecusadaRepository.countByDataRecusaBetween(tsInicio, tsFim);
			motoristaTop = corridasRepository.findMotoristaTopByDataBetween(tsInicio, tsFim);
			if (motoristaTop == null) motoristaTop = "-";
		}

		long diasNoPeriodo = (tsFim.getTime() - tsInicio.getTime()) / (1000 * 60 * 60 * 24) + 1;
		long totalOportunidades = totalFinalizadas + totalPerdidas + totalRecusadas;
		double aproveitamento = totalOportunidades > 0 ? (double) totalFinalizadas / totalOportunidades * 100 : 0;

		Map<String, Object> result = new HashMap<>();
		result.put("totalCorridas", totalCorridas);
		result.put("totalFaturado", totalFaturado);
		result.put("mediaDiaria", totalCorridas > 0 ? (double) totalCorridas / diasNoPeriodo : 0);
		result.put("totalPerdidas", totalPerdidas);
		result.put("totalRecusadas", totalRecusadas);
		result.put("aproveitamento", Math.round(aproveitamento * 10.0) / 10.0);
		result.put("motoristaTop", motoristaTop);

		return new ResponseEntity<>(result, HttpStatus.OK);
	}
}