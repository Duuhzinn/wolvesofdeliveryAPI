package duolthdelivery.api.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import duolthdelivery.api.rest.model.ConfiguracaoCorrida;
import duolthdelivery.api.rest.repository.ConfiguracaoCorridaRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/v1/configuration")
public class ConfiguracaoCorridaController {

	@Autowired
	private ConfiguracaoCorridaRepository configuracaoCorridaRepository;

	// ___________BUSCAR CONFIG POR USUARIO_ID___________//
	@CacheEvict(value = "cacheUser", allEntries = true)
	@CachePut("cacheUser")
	@GetMapping(value = "/race/{usuarioId}", produces = "application/json")
	public ResponseEntity<?> buscarPorUsuario(@PathVariable Long usuarioId) {
		ConfiguracaoCorrida config = configuracaoCorridaRepository.findByUsuarioId(usuarioId);
		if (config == null) {
			return new ResponseEntity<>("Configuração não encontrada para este usuário", HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(config, HttpStatus.OK);
	}

	// ___________CLIENTE OU ADMIN ATUALIZA UM ESPECÍFICO___________//
	@PatchMapping(value = "/race/update/{usuarioId}", produces = "application/json")
	public ResponseEntity<?> atualizarPorUsuario(@PathVariable Long usuarioId, @RequestBody ConfiguracaoCorrida body) {
		ConfiguracaoCorrida config = configuracaoCorridaRepository.findByUsuarioId(usuarioId);
		if (config == null) {
			return new ResponseEntity<>("Configuração não encontrada para este usuário", HttpStatus.NOT_FOUND);
		}
		config.setValor(body.getValor());
		configuracaoCorridaRepository.save(config);
		return new ResponseEntity<>(config, HttpStatus.OK);
	}

	// ___________ADMIN ATUALIZA TODOS DE UMA VEZ___________//
	@PatchMapping(value = "/race/updateAll", produces = "application/json")
	public ResponseEntity<?> atualizarTodos(@RequestBody ConfiguracaoCorrida body) {
		List<ConfiguracaoCorrida> todos = configuracaoCorridaRepository.findAll();
		for (ConfiguracaoCorrida config : todos) {
			config.setValor(body.getValor());
			configuracaoCorridaRepository.save(config);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	// ___________ADMIN LISTA TODOS OS CLIENTES COM SUA CONFIG DE CORRIDA___________//
	@CacheEvict(value = "cacheUser", allEntries = true)
	@CachePut("cacheUser")
	@GetMapping(value = "/race/clients", produces = "application/json")
	public ResponseEntity<?> listarClientes(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("usuario.nome").ascending());
		Page<ConfiguracaoCorrida> configs = configuracaoCorridaRepository.findByUsuario_TipoUser("CLIENTE", pageable);
		return new ResponseEntity<>(configs, HttpStatus.OK);
	}
}