package wolvesofdelivery.api.rest.controller;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wolvesofdelivery.api.rest.model.Usuario;
import wolvesofdelivery.api.rest.repository.UsuarioRepository;

//liberando o acesso para qualquer sistema sera permitido
@CrossOrigin(origins = "*")

@RestController
@RequestMapping(value = "/users")
public class IndexController {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@GetMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<Usuario> init(@PathVariable(value = "id") Long id) {

		Optional<Usuario> usuario = usuarioRepository.findById(id);

		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);

	}

	@GetMapping(value = "/allUser", produces = "application/json")
	public ResponseEntity<List<Usuario>> usuario() {
		List<Usuario> list = (List<Usuario>) usuarioRepository.findAll();

		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}

	// Cadastrando usuario
	@PostMapping(value = "/createUser", produces = "Application/json")
	public ResponseEntity<Usuario> cadastrarusuario(@RequestBody Usuario usuario) {

		// cadastro de usuario
		Usuario usuarioSalvo = usuarioRepository.save(usuario);

		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);

	}

	// atualizando usuario
	@PutMapping(value = "/updateUser", produces = "Application/json")
	public ResponseEntity<Usuario> atualizarusuario(@RequestBody Usuario usuario) {
		Usuario atualizarusuario = usuarioRepository.save(usuario);

		return new ResponseEntity<Usuario>(atualizarusuario, HttpStatus.OK);
	}

	// setando usuario do tipo motorista com ordem crecscente na posição da fila
	@GetMapping(value = "/queuePosition/", produces = "Application/json")
	public ResponseEntity<List<Usuario>> listarposicaofila() {
		List<Usuario> list = usuarioRepository.findByTipoUser("Motorista", Sort.by("posicaofila").ascending());

		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}

	// setando todos os usuarios em ordem crescente por nome
	@GetMapping(value = "/allDrive", produces = "Application/json")
	public ResponseEntity<List<Usuario>> listarMotoristaPorNome() {
		List<Usuario> list = usuarioRepository.findByTipoUserOrderByNomeAsc("Motorista");

		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}

	// altera status do id selecionado
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

		return new ResponseEntity<Usuario>(atualizarusuario, HttpStatus.OK);

	}

}
