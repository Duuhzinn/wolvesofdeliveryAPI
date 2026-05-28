package wolvesofdelivery.api.rest.controller;

import java.sql.Timestamp;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wolvesofdelivery.api.rest.model.Usuario;
import wolvesofdelivery.api.rest.repository.RoleRepository;
import wolvesofdelivery.api.rest.repository.UsuarioRepository;
import wolvesofdelivery.api.rest.service.WebSocketService;

//LIBERANDO O ACESSO PARA QUALQUER SISTEMA
@CrossOrigin(origins = "*")

@RestController
@RequestMapping(value = "/v1/drive")
public class MotoristaController {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private WebSocketService webSocketService; 
	
	//____________CONSULTANDO USUÁRIO(MOTORISTA ONLINE e OFFLINE)_____________________//
	@CacheEvict(value = "cacheUser", allEntries = true) //SE TIVER CACHE QUE NAO É USADO VAI REMOVER
	@CachePut("cacheUser") //TEM MUDANÇA? VAI TRAZER E COLOCAR NO CACHE
	@GetMapping(value = "/allDrive", produces = "application/json")
	public ResponseEntity<List<Usuario>> allDriver(){
		List<Usuario> list = usuarioRepository.findByTipoUserOrderByNomeAsc("MOTORISTA");
		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}
	
	//____________ALTERANDO STATUS DO USUÁRIO SELECIONADO____________________________//
	@CacheEvict(value = "cacheUser", allEntries = true) //SE TIVER CACHE QUE NAO É USADO VAI REMOVER
	@CachePut("cacheUser") //TEM MUDANÇA? VAI TRAZER E COLOCAR NO CACHE
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
	
	//_____________LISTANDO ORDEM DA FILA DOS MOTORISTAS_____________________//
	@CacheEvict(value = "cacheUser", allEntries = true) //SE TIVER CACHE QUE NAO É USADO VAI REMOVER
	@CachePut("cacheUser") //TEM MUDANÇA? VAI TRAZER E COLOCAR NO CACHE
	@GetMapping(value = "/driverQueue", produces = "application/json")
	public ResponseEntity<List<Usuario>> driverQueue(){
		List<Usuario> list = usuarioRepository.findByTipoUserAndStatusOrderByPosicaofilaAsc("MOTORISTA", 1L);
		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}
	
	//_____________BUSCANDO O PRIMEIRO DA FILA DOS MOTORISTAS ONLINE_____________________//
	@CacheEvict(value = "cacheUser", allEntries = true) //SE TIVER CACHE QUE NAO É USADO VAI REMOVER
	@CachePut("cacheUser") //TEM MUDANÇA? VAI TRAZER E COLOCAR NO CACHE
	@GetMapping(value = "/driverQueue/firstid", produces = "application/json")
	public ResponseEntity<Long> getNextDriverId(){
		Usuario usuario = usuarioRepository.findTop1ByTipoUserAndStatusOrderByPosicaofilaAsc("MOTORISTA", 1L);
		if(usuario == null) {
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
	
	//ALTERANDO O STATUS PARA CHAMANDO O MOTORISTA
	@CacheEvict(value = "cacheUser", allEntries = true) // SE TIVER CACHE QUE NAO É USADO VAI REMOVER
	@CachePut("cacheUser") // TEM MUDANÇA? VAI TRAZER E COLOCAR NO CACHE
	@PatchMapping(value = "/callingDrive/{id}", produces = "application/json")
	public ResponseEntity<?> chamandoMotorista(@PathVariable Long id){
		Usuario usuario = usuarioRepository.findById(id)
		        .orElseThrow(() -> new RuntimeException("Motorista não encontrado"));
		usuario.setStatus(3L);
		usuarioRepository.save(usuario);
		return ResponseEntity.ok().build();
	}
	
	//ALTERA O STATUS DO MOTORISTA PARA OFFLINE
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
	@CachePut("cacheUser")
	@PatchMapping(value = "/recusarCorrida/{id}", produces = "application/json")
	public ResponseEntity<?> recusarCorrida(@PathVariable Long id) {
		Usuario usuario = usuarioRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Motorista não encontrado"));
		usuario.setStatus(1L);
		usuario.setPosicaofila(new Timestamp(System.currentTimeMillis()));
	    usuarioRepository.save(usuario);
	    webSocketService.notificarAtualizacaoFila(); // AVISA A FILA
	    return ResponseEntity.ok().build();
	    
	}
}
