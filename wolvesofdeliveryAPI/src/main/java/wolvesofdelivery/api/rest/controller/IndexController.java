package wolvesofdelivery.api.rest.controller;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wolvesofdelivery.api.rest.model.Firebasetoken;
import wolvesofdelivery.api.rest.model.Role;
import wolvesofdelivery.api.rest.model.Usuario;
import wolvesofdelivery.api.rest.repository.FirebasetokenRepository;
import wolvesofdelivery.api.rest.repository.RoleRepository;
import wolvesofdelivery.api.rest.repository.UsuarioRepository;

//liberando o acesso para qualquer sistema sera permitido
@CrossOrigin(origins = "*")

@RestController
@RequestMapping(value = "/v1/users")
public class IndexController {

	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private RoleRepository roleRepository;
	
	//__________________________________________CONSULTANDO USUÁRIO NOME_________________________________________________________//
	
	@CacheEvict(value = "cacheUser", allEntries = true)//SE TIVER CACHE QUE NÃO É USADO, VAI REMOVER
	@CachePut("cacheUser")//TEM MUDANÇA?, VOU TRAZER E COLOCAR NO CACHE
	@GetMapping(value = "/pesqName/{nome}", produces = "application/json")
	public ResponseEntity<List<Usuario>> usuarioPorNome(@PathVariable("nome") String nome) throws InterruptedException{  
		
		List<Usuario> list = (List<Usuario>) usuarioRepository.findUserByNome(nome);
		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
		
	}

	//__________________________________________CONSULTANDO USUÁRIO POR ID_________________________________________________________//
	
	@CacheEvict(value = "cacheUser", allEntries = true) //se tiver cache que nao é usado, vai remover
	@CachePut("cacheUser") // Tem mudanças, vou trazer e colocar no chache
	@GetMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<Usuario> init(@PathVariable(value = "id") Long id) {

		Optional<Usuario> usuario = usuarioRepository.findById(id);
		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);

	}

	//__________________________________________CONSULTANDO TODOS USUÁRIO_________________________________________________________//
	
	@CacheEvict(value = "cacheUser", allEntries = true) //se tiver cache que nao é usado, vai remover
	@CachePut("cacheUser") // Tem mudanças, vou trazer e colocar no chache
	@GetMapping(value = "/allUser", produces = "application/json")
	public ResponseEntity<List<Usuario>> usuario() {
		List<Usuario> list = (List<Usuario>) usuarioRepository.findAll();
		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}
	
	//__________________________________________CADASTRANDO USUÁRIO_________________________________________________________//
	
	@CacheEvict(value = "cacheUser", allEntries = true) //se tiver cache que nao é usado, vai remover
	@CachePut("cacheUser") // Tem mudanças, vou trazer e colocar no chache
	@PostMapping(value = "/createUser", produces = "Application/json")
	public ResponseEntity<?> cadastrarusuario(@RequestBody Usuario usuario) {

	    // Criptografa a senha
	    usuario.setSenha(new BCryptPasswordEncoder().encode(usuario.getSenha()));

	    // Associa a role baseada no tipoUser
	    String nomeRole;
	    switch (usuario.getTipoUser()) {
        case "ADMIN":     nomeRole = "ROLE_ADMIN"; break;
        case "MOTORISTA": nomeRole = "ROLE_MOTORISTA"; break;
        case "CLIENTE":   nomeRole = "ROLE_CLIENTE"; break;
        default:
            return new ResponseEntity<>("tipoUser inválido. Use: Admin, Motorista ou Cliente", HttpStatus.BAD_REQUEST);
    }
	    Role role = roleRepository.findByNomeRole(nomeRole);
	    usuario.setRoles(List.of(role));

	    Usuario usuarioSalvo = usuarioRepository.save(usuario);
	    return new ResponseEntity<>(usuarioSalvo, HttpStatus.OK);
	}
	
	//__________________________________________ATUALIZANDO DADOS USUÁRIO_________________________________________________________//
	
	@CacheEvict(value = "cacheUser", allEntries = true) //se tiver cache que nao é usado, vai remover
	@CachePut("cacheUser") // Tem mudanças, vou trazer e colocar no chache
	@PutMapping(value = "/updateUser", produces = "Application/json")
	public ResponseEntity<?> atualizausuario(@RequestBody Usuario usuario){
		
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
	
	//__________________________________________ALTERANDO STATUS DO USUÁRIO SELECIONADO_________________________________________________________//

	@CacheEvict(value = "cacheUser", allEntries = true) //se tiver cache que nao é usado, vai remover
	@CachePut("cacheUser") // Tem mudanças, vou trazer e colocar no chache
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
	
	//__________________________________________SALVANDO TOKEN FIREBASE NO BANCO DE DADOS DO USUARIO_________________________________________________________//
	
	@Autowired
	private FirebasetokenRepository firebasetokenRepository;

	@CacheEvict(value = {"usuarios", "listaUsuarios"}, allEntries = true)
	@PostMapping(value = "/saveToken/{usuarioId}", produces = "application/json")
	public ResponseEntity<?> salvarToken(@PathVariable Long usuarioId,
											@RequestBody Firebasetoken firebasetoken){
		
		Usuario usuario = usuarioRepository.findById(usuarioId)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
		
		// VERIFICA SE JA EXISTE TOKEN PARA ESSE USUÁRIO
		Firebasetoken tokenExistente = firebasetokenRepository.findByUsuarioId(usuarioId);
		
		if (tokenExistente != null) {
			//ATUALIZA O TOKEN EXISTENTE
			tokenExistente.setToken(firebasetoken.getToken());
			firebasetokenRepository.save(tokenExistente);
		} else {
			//INSERE UM NOVO TOKEN
			firebasetoken.setUsuario(usuario);
			firebasetokenRepository.save(firebasetoken);
		}
		
		return new ResponseEntity<>("Token salvo com sucesso!", HttpStatus.OK);
		
	}
	
}


