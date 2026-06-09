package wolvesofdelivery.api.rest.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import wolvesofdelivery.api.rest.model.ConfiguracaoCorrida;
import wolvesofdelivery.api.rest.model.Firebasetoken;
import wolvesofdelivery.api.rest.model.Role;
import wolvesofdelivery.api.rest.model.Usuario;
import wolvesofdelivery.api.rest.repository.ConfiguracaoCorridaRepository;
import wolvesofdelivery.api.rest.repository.FirebasetokenRepository;
import wolvesofdelivery.api.rest.repository.RoleRepository;
import wolvesofdelivery.api.rest.repository.UsuarioRepository;


//LIBERANDO O ACESSO PARA QUALQUER SISTEMA
@CrossOrigin(origins = "*")

@RestController
@RequestMapping(value = "/v1/users")
public class IndexController {

	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private ConfiguracaoCorridaRepository configuracaoCorridaRepository;

	// ________________________CONSULTANDO USUÁRIO NOME_______________________________//

	@CacheEvict(value = "cacheUser", allEntries = true) // SE TIVER CACHE QUE NÃO É USADO, VAI REMOVER
	@CachePut("cacheUser") // TEM MUDANÇA?, VOU TRAZER E COLOCAR NO CACHE
	@GetMapping(value = "/pesqName/{nome}", produces = "application/json")
	public ResponseEntity<List<Usuario>> usuarioPorNome(@PathVariable("nome") String nome,
			@RequestParam("tipoUser") String tipoUser) throws InterruptedException {

		List<Usuario> list = usuarioRepository.findByNomeContainingIgnoreCaseAndTipoUser(nome, tipoUser);
		return new ResponseEntity<>(list, HttpStatus.OK);

	}

	// __________________CONSULTANDO USUÁRIO POR ID_____________________//

	@CacheEvict(value = "cacheUser", allEntries = true) // SE TIVER CACHE QUE NAO É USADO, VAI REMOVER
	@CachePut("cacheUser") // TEM MUDANÇA? VOU TRAZER E COLOCAR NO CACHE
	@GetMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<Usuario> init(@PathVariable(value = "id") Long id) {

		Optional<Usuario> usuario = usuarioRepository.findById(id);
		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);

	}

	// _________________LISTANDO TODOS USUÁRIO____________________//

	@CacheEvict(value = "cacheUser", allEntries = true) // se tiver cache que nao é usado, vai remover
	@CachePut("cacheUser") // Tem mudanças, vou trazer e colocar no chache
	@GetMapping(value = "/allUser", produces = "application/json")
	public ResponseEntity<List<Usuario>> usuario() {
		List<Usuario> list = (List<Usuario>) usuarioRepository.findAll();
		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}

	// ____________________CADASTRANDO USUÁRIO___________________________//

	@CacheEvict(value = "cacheUser", allEntries = true) // se tiver cache que nao é usado, vai remover
	@CachePut("cacheUser") // Tem mudanças, vou trazer e colocar no chache
	@PostMapping(value = "/createUser", produces = "Application/json")
	public ResponseEntity<?> cadastrarusuario(@RequestBody Usuario usuario) {

		// Criptografa a senha
		usuario.setSenha(new BCryptPasswordEncoder().encode(usuario.getSenha()));

		// Associa a role baseada no tipoUser
		String nomeRole;
		switch (usuario.getTipoUser()) {
		case "ADMIN":
			nomeRole = "ROLE_ADMIN";
			break;
		case "MOTORISTA":
			nomeRole = "ROLE_MOTORISTA";
			break;
		case "CLIENTE":
			nomeRole = "ROLE_CLIENTE";
			break;
		default:
			return new ResponseEntity<>("tipoUser inválido. Use: Admin, Motorista ou Cliente", HttpStatus.BAD_REQUEST);
		}
		Role role = roleRepository.findByNomeRole(nomeRole);
		usuario.setRoles(List.of(role));

		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		//CRIA CONFIG DA CORRIDA PARA CADA USUARIO CADASTRADO
		if (usuario.getTipoUser().equals("CLIENTE") || usuario.getTipoUser().equals("ADMIN")) {
		    ConfiguracaoCorrida configuracaoCorrida = new ConfiguracaoCorrida();
		    configuracaoCorrida.setUsuario(usuarioSalvo);
		    configuracaoCorrida.setValor(null);
		    configuracaoCorridaRepository.save(configuracaoCorrida);
		}
		
		return new ResponseEntity<>(usuarioSalvo, HttpStatus.OK);
	}

	// ___________________ATUALIZANDO DADOS USUÁRIO________________________//

	@CacheEvict(value = "cacheUser", allEntries = true) // SE TIVER CACHE QUE NAO É USADO, VAI REMOVER
	@CachePut("cacheUser") // TEM MUDANÇA? VOU TRAZER E COLOCAR NO CACHE
	@PutMapping(value = "/updateUser", produces = "Application/json")
	public ResponseEntity<?> atualizausuario(@RequestBody Usuario usuario) {

		// Busca o usuário atual do banco
		Usuario usuarioAtual = usuarioRepository.findById(usuario.getId())
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

		// Atualiza apenas os campos permitidos
		usuarioAtual.setEmail(usuario.getEmail());
		usuarioAtual.setEndereco(usuario.getEndereco());
		usuarioAtual.setLogin(usuario.getLogin());
		usuarioAtual.setNome(usuario.getNome());
		usuarioAtual.setTelefone(usuario.getTelefone());
		usuarioAtual.setTipoUser(usuario.getTipoUser());

		if (usuario.getSenha() != null && !usuario.getSenha().isEmpty()) {
			usuarioAtual.setSenha(new BCryptPasswordEncoder().encode(usuario.getSenha()));
		}

		Usuario usuarioAtualizado = usuarioRepository.save(usuarioAtual);
		return new ResponseEntity<>(usuarioAtualizado, HttpStatus.OK);

	}

	// ______________SALVANDO TOKEN FIREBASE NO BANCO DE DADOS DO USUARIO_____________//

	@Autowired
	private FirebasetokenRepository firebasetokenRepository;

	@CacheEvict(value = { "usuarios", "listaUsuarios" }, allEntries = true)
	@PostMapping(value = "/saveToken/{usuarioId}", produces = "application/json")
	public ResponseEntity<?> salvarToken(@PathVariable Long usuarioId, @RequestBody Firebasetoken firebasetoken) {

		Usuario usuario = usuarioRepository.findById(usuarioId)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

		// VERIFICA SE JA EXISTE TOKEN PARA ESSE USUÁRIO
		Firebasetoken tokenExistente = firebasetokenRepository.findByUsuarioId(usuarioId);

		if (tokenExistente != null) {
			// ATUALIZA O TOKEN EXISTENTE
			tokenExistente.setToken(firebasetoken.getToken());
			firebasetokenRepository.save(tokenExistente);
		} else {
			// INSERE UM NOVO TOKEN
			firebasetoken.setUsuario(usuario);
			firebasetokenRepository.save(firebasetoken);
		}

		return ResponseEntity.ok().build();

	}

	// ___________________CONSULTANDO USUÁRIO LOGADO______________________//
	@GetMapping(value = "/userLogado", produces = "application/json")
	public ResponseEntity<Usuario> getUsuarioLogado(Authentication authentication) {
		String login = authentication.getName();
		Usuario usuario = usuarioRepository.findUserByLogin(login);

		return new ResponseEntity<>(usuario, HttpStatus.OK);

	}
	
	//SETANDO STATUS 1 - ONLINE E 2 OFFLINE
	@CacheEvict(value = "cacheUser", allEntries = true) // SE TIVER CACHE QUE NAO É USADO, VAI REMOVER
	@CachePut("cacheUser") // TEM MUDANÇA? VOU TRAZER E COLOCAR NO CACHE
	@PatchMapping(value = "/status/{id}/{status}", produces = "application/json")
	public ResponseEntity<?> atualizarStatus(@PathVariable Long id, @PathVariable Long status) {
	    Usuario usuario = usuarioRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
	    usuario.setStatus(status);
	    usuarioRepository.save(usuario);
	    return ResponseEntity.ok().build();
	}
	

}
