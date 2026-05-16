package wolvesofdelivery.api.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wolvesofdelivery.api.rest.model.Usuario;
import wolvesofdelivery.api.rest.repository.RoleRepository;
import wolvesofdelivery.api.rest.repository.UsuarioRepository;

//LIBERANDO O ACESSO PARA QUALQUER SISTEMA
@CrossOrigin(origins = "*")

@RestController
@RequestMapping(value = "/v1/drive")
public class MotoristaController {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private RoleRepository roleRepository;
	
	//________________________________CONSULTANDO USUÁRIO(MOTORISTA ONLINE e OFFLINE)_______________________________________________//
	@CacheEvict(value = "cacheUser", allEntries = true) //SE TIVER CACHE QUE NAO É USADO VAI REMOVER
	@CachePut("cacheUser") //TEM MUDANÇA? VAI TRAZER E COLOCAR NO CACHE
	@GetMapping(value = "/allDrive", produces = "application/json")
	public ResponseEntity<List<Usuario>> usuario(){
		List<Usuario> list = usuarioRepository.findByTipoUserOrderByStatusAsc("MOTORISTA");
		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);//teste
	}

}
