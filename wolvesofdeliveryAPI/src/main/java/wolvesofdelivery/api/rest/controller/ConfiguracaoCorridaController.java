package wolvesofdelivery.api.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wolvesofdelivery.api.rest.model.ConfiguracaoCorrida;
import wolvesofdelivery.api.rest.repository.ConfiguracaoCorridaRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/v1/configuracaocorrida")
public class ConfiguracaoCorridaController {
	
	@Autowired
	private ConfiguracaoCorridaRepository configuracaoCorridaRepository;
	
	// ___________BUSCAR CONFIG POR USUARIO_ID___________//
	@CacheEvict(value = "cacheUser", allEntries = true) // SE TIVER CACHE QUE NÃO É USADO, VAI REMOVER
	@CachePut("cacheUser") // TEM MUDANÇA?, VOU TRAZER E COLOCAR NO CACHE
	@GetMapping(value ="/{usuarioId}", produces = "application/json")
	public ResponseEntity<?> buscarPorUsuario(@PathVariable Long usuarioId){
		ConfiguracaoCorrida config = configuracaoCorridaRepository.findByUsuarioId(usuarioId);
		
		if (config == null) {
            return new ResponseEntity<>("Configuração não encontrada para este usuário", HttpStatus.NOT_FOUND);
        }
		
		return new ResponseEntity<>(config, HttpStatus.OK);
	}
	
	// ___________CLIENTE OU ADMIN ATUALIZA UM ESPECÍFICO___________//
	@PatchMapping(value = "/update/{usuarioId}", produces = "application/json")
	public ResponseEntity<?> atualizarPorUsuario(@PathVariable Long usuarioId, @RequestBody ConfiguracaoCorrida body) {
		ConfiguracaoCorrida config = configuracaoCorridaRepository.findByUsuarioId(usuarioId);
		
		if (config == null) {
            return new ResponseEntity<>("Configuração não encontrada para este usuário", HttpStatus.NOT_FOUND);
        }
		config.setValor(body.getValor());
        configuracaoCorridaRepository.save(config);
        return new ResponseEntity<>(config, HttpStatus.OK);
	}
	
	// ___________ADMIN LISTA TODOS OS CLIENTES COM SUA CONFIG DE CORRIDA___________//
	@CacheEvict(value = "cacheUser", allEntries = true) // SE TIVER CACHE QUE NÃO É USADO, VAI REMOVER
	@CachePut("cacheUser") // TEM MUDANÇA?, VOU TRAZER E COLOCAR NO CACHE
	@GetMapping(value = "/clientes", produces = "application/json")
	public ResponseEntity<?> listarClientes() {
	    List<ConfiguracaoCorrida> configs = configuracaoCorridaRepository.findByUsuario_TipoUser("CLIENTE");
	    return new ResponseEntity<>(configs, HttpStatus.OK);
	}
	

}
