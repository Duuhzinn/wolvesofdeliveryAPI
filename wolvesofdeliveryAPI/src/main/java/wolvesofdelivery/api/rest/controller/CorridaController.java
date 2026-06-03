package wolvesofdelivery.api.rest.controller;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import wolvesofdelivery.api.rest.model.Corridas;
import wolvesofdelivery.api.rest.model.Usuario;
import wolvesofdelivery.api.rest.repository.CorridaExpiradaRepository;
import wolvesofdelivery.api.rest.repository.CorridasRecusadaRepository;
import wolvesofdelivery.api.rest.repository.CorridasRepository;
import wolvesofdelivery.api.rest.repository.UsuarioRepository;
import wolvesofdelivery.api.rest.service.WebSocketService;

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

	private final SimpMessagingTemplate messagingTemplate;

	public CorridaController(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}

	// CORRIDAS DOS MOTORISTAS EM ANDAMENTO
	@CacheEvict(value = "cacheUser", allEntries = true) // SE TIVER CACHE QUE NAO É USADO VAI REMOVER
	@CachePut("cacheUser") // TEM MUDANÇA? VAI TRAZER E COLOCAR NO CACHE
	@GetMapping(value = "/raceDrive/{motoristaId}", produces = "application/json")
	public ResponseEntity<?> corridasMotorista(@PathVariable Long motoristaId,
			@PageableDefault(size = 10) Pageable pageable) {
		Page<Corridas> corridas = corridasRepository.findByMotoristaIdAndStatusOrderByIdDesc(motoristaId,
				"EM ANDAMENTO", pageable);
		return new ResponseEntity<>(corridas, HttpStatus.OK);
	}

	// CORRIDAS DO DESPACHANTE EM ANDAMENTO
	@CacheEvict(value = "cacheUser", allEntries = true) // SE TIVER CACHE QUE NAO É USADO VAI REMOVER
	@CachePut("cacheUser") // TEM MUDANÇA? VAI TRAZER E COLOCAR NO CACHE
	@GetMapping(value = "/raceDespatcher/{clienteId}", produces = "application/json")
	public ResponseEntity<?> corridasDespachante(@PathVariable Long clienteId,
			@PageableDefault(size = 10) Pageable pageable) {
		Page<Corridas> corridas = corridasRepository.findByClienteIdAndStatusOrderByIdDesc(clienteId, "EM ANDAMENTO",
				pageable);
		return new ResponseEntity<>(corridas, HttpStatus.OK);
	}

	// CORRIDAS DOS ADMIN (TODAS) EM ANDAMENTO
	@CacheEvict(value = "cacheUser", allEntries = true) // SE TIVER CACHE QUE NAO É USADO VAI REMOVER
	@CachePut("cacheUser") // TEM MUDANÇA? VAI TRAZER E COLOCAR NO CACHE
	@GetMapping(value = "/allRace", produces = "application/json")
	public ResponseEntity<?> todasCorridas(@PageableDefault(size = 10) Pageable pageable) {
		Page<Corridas> corridas = corridasRepository.findByStatusOrderByIdDesc("EM ANDAMENTO", pageable);
		return new ResponseEntity<>(corridas, HttpStatus.OK);
	}

	// CORRIDAS DOS MOTORISTAS FINALIZADA
	@CacheEvict(value = "cacheUser", allEntries = true) // SE TIVER CACHE QUE NAO É USADO VAI REMOVER
	@CachePut("cacheUser") // TEM MUDANÇA? VAI TRAZER E COLOCAR NO CACHE
	@GetMapping(value = "/raceDriveFinished/{motoristaId}", produces = "application/json")
	public ResponseEntity<?> corridasMotoristaFinalizada(@PathVariable Long motoristaId,
			@PageableDefault(size = 10) Pageable pageable) {
		Page<Corridas> corridas = corridasRepository.findByMotoristaIdAndStatusOrderByIdDesc(motoristaId, "FINALIZADA",
				pageable);
		return new ResponseEntity<>(corridas, HttpStatus.OK);
	}

	// CORRIDAS DO DESPACHANTE FINALIZADA
	@CacheEvict(value = "cacheUser", allEntries = true) // SE TIVER CACHE QUE NAO É USADO VAI REMOVER
	@CachePut("cacheUser") // TEM MUDANÇA? VAI TRAZER E COLOCAR NO CACHE
	@GetMapping(value = "/raceDespatcherFinished/{clienteId}", produces = "application/json")
	public ResponseEntity<?> corridasDespachanteFinalizada(@PathVariable Long clienteId,
			@PageableDefault(size = 10) Pageable pageable) {
		Page<Corridas> corridas = corridasRepository.findByClienteIdAndStatusOrderByIdDesc(clienteId, "FINALIZADA",
				pageable);
		return new ResponseEntity<>(corridas, HttpStatus.OK);
	}

	// CORRIDAS DOS ADMIN (TODAS) FINALIZADA
	@CacheEvict(value = "cacheUser", allEntries = true) // SE TIVER CACHE QUE NAO É USADO VAI REMOVER
	@CachePut("cacheUser") // TEM MUDANÇA? VAI TRAZER E COLOCAR NO CACHE
	@GetMapping(value = "/allRaceFinished", produces = "application/json")
	public ResponseEntity<?> todasCorridasFinished(@PageableDefault(size = 10) Pageable pageable) {
		Page<Corridas> corridas = corridasRepository.findByStatusOrderByIdDesc("FINALIZADA", pageable);
		return new ResponseEntity<>(corridas, HttpStatus.OK);
	}

	@CacheEvict(value = "cacheUser", allEntries = true) // SE TIVER CACHE QUE NAO É USADO VAI REMOVER
	@CachePut("cacheUser") // TEM MUDANÇA? VAI TRAZER E COLOCAR NO CACHE
	@PatchMapping(value = "/updateRace/{corridaId}", produces = "application/json")
	public ResponseEntity<?> atualizarCorrida(@PathVariable Long corridaId) {
		// BUSCA A CORRIDA
		Corridas corrida = corridasRepository.findById(corridaId)
				.orElseThrow(() -> new RuntimeException("Corrida não encontrada"));

		if (corrida.getInicio_corrida() == null) {
			// PRIMEIRA VEZ - ADICIONA INICIO
			corrida.setInicio_corrida(new Timestamp(System.currentTimeMillis()));
		} else {
			// SEGUNDA VEZ - ADICIONA O TERMINO DA CORRIDA
			corrida.setTermino_corrida(new Timestamp(System.currentTimeMillis()));
			corrida.setStatus_corrida("FINALIZADA");

			// ATUALIZA MOTORISTA
			Usuario motorista = corrida.getMotorista();
			if (motorista != null) {
				motorista.setStatus(1L);// ONLINE
				motorista.setPosicaofila(new Timestamp(System.currentTimeMillis())); // VOLTA PARA A FILA
				usuarioRepository.save(motorista);
				webSocketService.notificarAtualizacaoFila(); // AVISA A FILA
			}
		}

		corridasRepository.save(corrida);
		// AVISA O DESPACHANTE VIA WEBSOCKET
		messagingTemplate.convertAndSend("/topic/corrida", corridaId);
		return ResponseEntity.ok("Corrida atualizada!");
	}
	
	//CANCELANDO A CORRIDA
	@CacheEvict(value = "cacheUser", allEntries = true) // SE TIVER CACHE QUE NAO É USADO VAI REMOVER
	@CachePut("cacheUser") // TEM MUDANÇA? VAI TRAZER E COLOCAR NO CACHE
	@PatchMapping(value = "/cancelCall/{motoristaId}", produces = "application/json")
	public ResponseEntity<?> cancelarChamada(@PathVariable Long motoristaId){
		Usuario motorista = usuarioRepository.findById(motoristaId)
	            .orElseThrow(() -> new RuntimeException("Motorista não encontrado"));
		
		motorista.setStatus(1L); // ONLINE
		usuarioRepository.save(motorista);
		messagingTemplate.convertAndSend("/topic/cancelarChamada", motoristaId);
		webSocketService.notificarAtualizacaoFila();
		
		return ResponseEntity.ok("Chamada cancelada!");
		
	}
	
	//CORRIDA EXPIRADA
	@CacheEvict(value = "cacheUser", allEntries = true) // SE TIVER CACHE QUE NAO É USADO VAI REMOVER
	@CachePut("cacheUser") // TEM MUDANÇA? VAI TRAZER E COLOCAR NO CACHE
	@PatchMapping(value = "/expireRace/{corridaId}", produces = "application/json")
	public ResponseEntity<?> cancelarCorrida(@PathVariable Long corridaId) {
		Corridas corrida = corridasRepository.findById(corridaId)
	            .orElseThrow(() -> new RuntimeException("Corrida não encontrada"));
		corrida.setStatus_corrida("EXPIRADA");
		corrida.setTermino_corrida(new Timestamp(System.currentTimeMillis()));
		corridasRepository.save(corrida);
		return ResponseEntity.ok("Corrida cancelada!");
	}
	
//_________________________________________________________________________

	// _________COMEÇA O ENDPOINT DE CIENCIA DE DADOS_________
	
	//ESTASTISTICA POR ANO - MOTORISTA
	@Cacheable(value = "estatisticas", key = "#motoristaId + '-' + #ano")
	@GetMapping(value = "/estatisticas/motorista/{motoristaId}/{ano}", produces = "application/json")
	public ResponseEntity<?> estatisticasMotorista (@PathVariable Long motoristaId, @PathVariable int ano){
		List<Map<String, Object>> resultado = new ArrayList<>();
		String [] meses = {"JANEIRO","FEVEREIRO","MARÇO","ABRIL","MAIO","JUNHO",
                "JULHO","AGOSTO","SETEMBRO","OUTUBRO","NOVEMBRO","DEZEMBRO"};
		
		for (int mes = 1; mes <= 12; mes ++) {
			int diasNoMes = LocalDate.of(ano, mes, 1).lengthOfMonth();
			
			long totalCorridas = corridasRepository.countByMotoristaIdAndMesAno(motoristaId, mes, ano);
			long totalFinalizadas = corridasRepository.countByMotoristaIdAndStatusAndMesAno(motoristaId, "FINALIZADA", mes, ano);
			
	        Map<String, Object> card = new HashMap<>();
	        card.put("mes", meses[mes - 1]);
	        card.put("totalCorridas", totalCorridas);
	        card.put("totalFaturado", totalFinalizadas * 10.0);
	        card.put("mediaDiaria", totalCorridas > 0 ? (double) totalCorridas / diasNoMes : 0);
			long totalPerdidas = corridaExpiradaRepository.countByMotoristaIdAndDataExpiradaBetween(motoristaId,
					Timestamp.valueOf(LocalDate.of(ano, mes, 1).atStartOfDay()), Timestamp.valueOf(
							LocalDate.of(ano, mes, LocalDate.of(ano, mes, 1).lengthOfMonth()).atTime(23, 59, 59)));
			long totalRecusadas = corridasRecusadaRepository.countByMotoristaIdAndDataRecusaBetween(motoristaId,
					Timestamp.valueOf(LocalDate.of(ano, mes, 1).atStartOfDay()), Timestamp.valueOf(
							LocalDate.of(ano, mes, LocalDate.of(ano, mes, 1).lengthOfMonth()).atTime(23, 59, 59)));
			card.put("totalPerdidas", totalPerdidas);
			card.put("totalRecusadas", totalRecusadas);
			//CALCULANDO O PAROVEITAMENTO DO MOTORISTA
			double aproveitamento = totalCorridas > 0 ? (double) totalFinalizadas / totalCorridas * 100 : 0;
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
	        String motoristaTop = corridasRepository.findMotoristaTopByMesAno(mes, ano);

	        Map<String, Object> card = new HashMap<>();
	        card.put("mes", meses[mes - 1]);
	        card.put("totalCorridas", totalCorridas);
	        card.put("totalFaturado", totalFinalizadas * 10.0);
	        card.put("mediaDiaria", totalCorridas > 0 ? (double) totalCorridas / diasNoMes : 0);
	        long totalPerdidas = corridaExpiradaRepository.countByMesAno(mes, ano);
	        long totalRecusadas = corridasRecusadaRepository.countByMesAno(mes, ano);
	        double aproveitamento = totalCorridas > 0 ? (double) totalFinalizadas / totalCorridas * 100 : 0;

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

	        Map<String, Object> card = new HashMap<>();
	        card.put("mes", meses[mes - 1]);
	        card.put("totalCorridas", totalCorridas);
	        card.put("totalFaturado", totalFinalizadas * 10.0);
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
	    String motoristaTop = "-";

	    if (clienteId != null) {
	        totalCorridas = corridasRepository.countByClienteIdAndDataBetween(clienteId, tsInicio, tsFim);
	        totalFinalizadas = corridasRepository.countByClienteIdAndStatusAndDataBetween(clienteId, "FINALIZADA", tsInicio, tsFim);
	    } else if (motoristaId != null) {
	        totalCorridas = corridasRepository.countByMotoristaIdAndDataBetween(motoristaId, tsInicio, tsFim);
	        totalFinalizadas = corridasRepository.countByMotoristaIdAndStatusAndDataBetween(motoristaId, "FINALIZADA", tsInicio, tsFim);
	        totalPerdidas = corridaExpiradaRepository.countByMotoristaIdAndDataExpiradaBetween(motoristaId, tsInicio, tsFim);
	        totalRecusadas = corridasRecusadaRepository.countByMotoristaIdAndDataRecusaBetween(motoristaId, tsInicio, tsFim);
	    } else {
	        totalCorridas = corridasRepository.countByDataBetween(tsInicio, tsFim);
	        totalFinalizadas = corridasRepository.countByStatusAndDataBetween("FINALIZADA", tsInicio, tsFim);
	        totalPerdidas = corridaExpiradaRepository.countByDataExpiradaBetween(tsInicio, tsFim);
	        totalRecusadas = corridasRecusadaRepository.countByDataRecusaBetween(tsInicio, tsFim);
	        motoristaTop = corridasRepository.findMotoristaTopByDataBetween(tsInicio, tsFim);
	        if (motoristaTop == null) motoristaTop = "-";
	    }

	    long diasNoPeriodo = (tsFim.getTime() - tsInicio.getTime()) / (1000 * 60 * 60 * 24) + 1;
	    double aproveitamento = totalCorridas > 0 ? (double) totalFinalizadas / totalCorridas * 100 : 0;

	    Map<String, Object> result = new HashMap<>();
	    result.put("totalCorridas", totalCorridas);
	    result.put("totalFaturado", totalFinalizadas * 10.0);
	    result.put("mediaDiaria", totalCorridas > 0 ? (double) totalCorridas / diasNoPeriodo : 0);
	    result.put("totalPerdidas", totalPerdidas);
	    result.put("totalRecusadas", totalRecusadas);
	    result.put("aproveitamento", Math.round(aproveitamento * 10.0) / 10.0);
	    result.put("motoristaTop", motoristaTop);

	    return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	
		
}	

	