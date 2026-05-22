package wolvesofdelivery.api.rest.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wolvesofdelivery.api.rest.model.Firebasetoken;
import wolvesofdelivery.api.rest.model.Usuario;
import wolvesofdelivery.api.rest.repository.FirebasetokenRepository;
import wolvesofdelivery.api.rest.repository.UsuarioRepository;
import wolvesofdelivery.api.rest.service.FirebaseNotificationService;

//LIBERANDO O ACESSO PARA QUALQUER SISTEMA
@CrossOrigin(origins = "*")

@RestController
@RequestMapping(value = "/v1/pushnotification")
public class PushNotificationController {

	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private FirebasetokenRepository firebasetokenRepository;
	@Autowired
	private FirebaseNotificationService firebaseNotificationService;

	@CacheEvict(value = "cacheUser", allEntries = true) // SE TIVER CACHE QUE NAO É USADO VAI REMOVER
	@CachePut("cacheUser") // TEM MUDANÇA? VAI TRAZER E COLOCAR NO CACHE
	@PostMapping(value = "/send/{usuarioId}", produces = "application/json")
	public ResponseEntity<?> enviarNotificacao(@PathVariable Long usuarioId) {
		Optional<Usuario> optional = usuarioRepository.findById(usuarioId);
		if (optional.isPresent()) {
			Usuario usuario = optional.get();
			Firebasetoken firebasetoken = firebasetokenRepository.findByUsuarioId(usuarioId);
			if (firebasetoken == null) {
				return ResponseEntity.badRequest().body("Usuário sem token Firebase");
			}
			String resposta = firebaseNotificationService.enviarNotificacao(firebasetoken.getToken(),
					"Nova Corrida 🏍️", "Você recebeu uma nova corrida!");
			return ResponseEntity.ok(resposta);
		} else {
			return ResponseEntity.badRequest().body("Usuário não encontrado");
		}
	}
	
	@CacheEvict(value = "cacheUser", allEntries = true) // SE TIVER CACHE QUE NAO É USADO VAI REMOVER
	@CachePut("cacheUser") // TEM MUDANÇA? VAI TRAZER E COLOCAR NO CACHE
	@PostMapping(value = "/lostRace/{usuarioId}", produces = "application/json")
	public ResponseEntity<?> corridaPerdida(@PathVariable Long usuarioId){
		Optional<Usuario> optional = usuarioRepository.findById(usuarioId);
		if (optional.isPresent()) {
			Usuario usuario = optional.get();
			Firebasetoken firebasetoken = firebasetokenRepository.findByUsuarioId(usuarioId);
			if(firebasetoken == null) {
				return ResponseEntity.badRequest().body("Usuário sem tokem FireBase");
			}
			String resposta = firebaseNotificationService.enviarNotificacao(firebasetoken.getToken(), 
					"Corrida Perdida ❌", "Você perdeu uma corrida!");
			return ResponseEntity.ok(resposta);
		} else {
			return ResponseEntity.badRequest().body("Usuário não encontrado");

		}
		
	}

}
