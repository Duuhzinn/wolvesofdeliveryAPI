package wolvesofdelivery.api.rest.controller;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wolvesofdelivery.api.rest.model.Usuario;
import wolvesofdelivery.api.rest.repository.CorridasRepository;
import wolvesofdelivery.api.rest.repository.UsuarioRepository;

@RestController
@RequestMapping("/v1/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private CorridasRepository corridasRepository;
	
	//_______DASHBOARD DE ADMIN_______
	@CacheEvict(value = "cacheUser", allEntries = true) //SE TIVER CACHE QUE NAO É USADO VAI REMOVER
	@CachePut("cacheUser") //TEM MUDANÇA? VAI TRAZER E COLOCAR NO CACHE
	@GetMapping(value = "/admin/{adminId}", produces = "application/json")
	public ResponseEntity<?> dashboardAdmin(@PathVariable Long adminId){
		Usuario admin = usuarioRepository.findById(adminId)
	            .orElseThrow(() -> new RuntimeException("Admin não encontrado"));
		// HOJE
		Timestamp inicioDia = Timestamp.valueOf(LocalDate.now().atStartOfDay());
	    Timestamp fimDia = Timestamp.valueOf(LocalDate.now().atTime(23, 59, 59));
	    // TOTAL DE MOTORISTAS
	    long totalMotoristas = usuarioRepository.findByTipoUserOrderByNomeAsc("MOTORISTA").size();
	    // CORRIDAS DO DIA
	    long corridasDia = corridasRepository.countByDataBetween(inicioDia, fimDia);
	    // FINALIZADAS DO DIA
	    long finalizadasDia = corridasRepository.countByStatusAndDataBetween("FINALIZADA", inicioDia, fimDia);
	    // FATURAMENTO DO DIA
	    double faturamentoDia = finalizadasDia * 10.0;
	    
	    List<Usuario> nomesOnline = usuarioRepository.findByTipoUserOrderByNomeAsc("MOTORISTA");
	    
	    
	    Map<String, Object> result = new HashMap<>();
	    result.put("nomeAdmin", admin.getNome());
	    result.put("totalMotoristas", totalMotoristas);
	    result.put("corridasDia", corridasDia);
	    result.put("faturamentoDia", faturamentoDia);
	    result.put("motoristasOnline", nomesOnline);
	    
	    return new ResponseEntity<>(result, HttpStatus.OK);
		
	}
	
	

}
