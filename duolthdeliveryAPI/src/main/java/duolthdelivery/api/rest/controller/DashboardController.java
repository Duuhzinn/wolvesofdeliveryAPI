package duolthdelivery.api.rest.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import duolthdelivery.api.rest.model.Usuario;
import duolthdelivery.api.rest.repository.CorridaExpiradaRepository;
import duolthdelivery.api.rest.repository.CorridasRecusadaRepository;
import duolthdelivery.api.rest.repository.CorridasRepository;
import duolthdelivery.api.rest.repository.UsuarioRepository;

@RestController
@RequestMapping("/v1/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

	@Autowired
	private CorridaExpiradaRepository corridaExpiradaRepository;
	@Autowired
	private CorridasRecusadaRepository corridasRecusadaRepository;
	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private CorridasRepository corridasRepository;

	// _______DASHBOARD DE ADMIN_______
	@CacheEvict(value = "cacheUser", allEntries = true)
	@CachePut("cacheUser")
	@GetMapping(value = "/admin/{adminId}", produces = "application/json")
	public ResponseEntity<?> dashboardAdmin(@PathVariable Long adminId) {
		Usuario admin = usuarioRepository.findById(adminId)
				.orElseThrow(() -> new RuntimeException("Admin não encontrado"));

		Timestamp inicioDia = Timestamp.valueOf(LocalDate.now().atStartOfDay());
		Timestamp fimDia = Timestamp.valueOf(LocalDate.now().atTime(23, 59, 59));

		// TOTAIS
		long totalMotoristas = usuarioRepository.findByTipoUserOrderByNomeAsc("MOTORISTA").size();
		long totalClientes = usuarioRepository.findByTipoUserOrderByNomeAsc("CLIENTE").size();
		long corridasDia = corridasRepository.countByStatusAndDataBetween("FINALIZADA", inicioDia, fimDia);
		long recusadasDia = corridasRecusadaRepository.countByDataRecusaBetween(inicioDia, fimDia);
		long expiradasDia = corridaExpiradaRepository.countByDataExpiradaBetween(inicioDia, fimDia);
		BigDecimal faturamentoDia = corridasRepository.sumValorByStatusAndDataBetween("FINALIZADA", inicioDia, fimDia);

		// MOTORISTAS QUE RECUSARAM
		List<Object[]> recusasPorMotorista = corridasRecusadaRepository.findMotoristasComRecusasNoDia(inicioDia, fimDia);
		List<Map<String, Object>> motoristasRecusaram = new ArrayList<>();
		for (Object[] row : recusasPorMotorista) {
			Map<String, Object> m = new HashMap<>();
			m.put("nome", row[0]);
			m.put("quantidade", row[1]);
			motoristasRecusaram.add(m);
		}

		// MOTORISTAS QUE PERDERAM
		List<Object[]> perdasPorMotorista = corridaExpiradaRepository.findMotoristasComPerdidasNoDia(inicioDia, fimDia);
		List<Map<String, Object>> motoristasPerderem = new ArrayList<>();
		for (Object[] row : perdasPorMotorista) {
			Map<String, Object> m = new HashMap<>();
			m.put("nome", row[0]);
			m.put("quantidade", row[1]);
			motoristasPerderem.add(m);
		}

		// ONLINE
		List<String> motoristaOnline = usuarioRepository.findByTipoUserOrderByNomeAsc("MOTORISTA")
				.stream()
				.filter(m -> m.getStatus() == 1L)
				.map(Usuario::getNome)
				.collect(Collectors.toList());

		List<String> clientesOnline = usuarioRepository.findByTipoUserOrderByNomeAsc("CLIENTE")
				.stream()
				.filter(c -> c.getStatus() != null && c.getStatus() == 1L)
				.map(Usuario::getNome)
				.collect(Collectors.toList());

		Map<String, Object> result = new HashMap<>();
		result.put("nomeAdmin", admin.getNome());
		result.put("totalMotoristas", totalMotoristas);
		result.put("totalMotoristasOnline", motoristaOnline.size());
		result.put("totalClientes", totalClientes);
		result.put("totalClientesOnline", clientesOnline.size());
		result.put("clientesOnline", clientesOnline);
		result.put("motoristasOnline", motoristaOnline);
		result.put("corridasDia", corridasDia);
		result.put("faturamentoDia", faturamentoDia);
		result.put("recusadasDia", recusadasDia);
		result.put("expiradasDia", expiradasDia);
		result.put("motoristasRecusaram", motoristasRecusaram);
		result.put("motoristasPerderem", motoristasPerderem);

		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	// DASHBOARD DO MOTORISTA
	@CacheEvict(value = "cacheUser", allEntries = true)
	@CachePut("cacheUser")
	@GetMapping(value = "/motorista/{motoristaId}", produces = "application/json")
	public ResponseEntity<?> dashboardMotorista(@PathVariable Long motoristaId) {

		Timestamp inicioDia = Timestamp.valueOf(LocalDate.now().atStartOfDay());
		Timestamp fimDia = Timestamp.valueOf(LocalDate.now().atTime(23, 59, 59));

		long totalFinalizadas = corridasRepository.countByMotoristaIdAndStatusAndDataBetween(motoristaId, "FINALIZADA", inicioDia, fimDia);
		long totalPerdidas = corridaExpiradaRepository.countByMotoristaIdAndDataExpiradaBetween(motoristaId, inicioDia, fimDia);
		long totalRecusadas = corridasRecusadaRepository.countByMotoristaIdAndDataRecusaBetween(motoristaId, inicioDia, fimDia);
		BigDecimal totalFaturado = corridasRepository.sumValorByMotoristaIdAndStatusAndDataBetween(motoristaId, "FINALIZADA", inicioDia, fimDia);

		long totalOportunidades = totalFinalizadas + totalPerdidas + totalRecusadas;
		double aproveitamento = totalOportunidades > 0 ? (double) totalFinalizadas / totalOportunidades * 100 : 0;

		Map<String, Object> result = new HashMap<>();
		result.put("totalCorridas", totalFinalizadas);
		result.put("totalFaturado", totalFaturado);
		result.put("totalPerdidas", totalPerdidas);
		result.put("totalRecusadas", totalRecusadas);
		result.put("aproveitamento", Math.round(aproveitamento * 10.0) / 10.0);

		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	// DASHBOARD DO CLIENTE - APENAS FINALIZADAS
	@GetMapping(value = "/cliente/{clienteId}", produces = "application/json")
	public ResponseEntity<?> dashboardCliente(@PathVariable Long clienteId) {

		Timestamp inicioDia = Timestamp.valueOf(LocalDate.now().atStartOfDay());
		Timestamp fimDia = Timestamp.valueOf(LocalDate.now().atTime(23, 59, 59));

		long totalFinalizadas = corridasRepository.countByClienteIdAndStatusAndDataBetween(clienteId, "FINALIZADA", inicioDia, fimDia);
		BigDecimal totalFaturado = corridasRepository.sumValorByClienteIdAndStatusAndDataBetween(clienteId, "FINALIZADA", inicioDia, fimDia);

		double aproveitamento = totalFinalizadas > 0 ? 100.0 : 0.0;

		Map<String, Object> result = new HashMap<>();
		result.put("totalCorridas", totalFinalizadas);
		result.put("totalFaturado", totalFaturado);
		result.put("aproveitamento", aproveitamento);

		return new ResponseEntity<>(result, HttpStatus.OK);
	}
}