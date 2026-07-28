package duolthdelivery.api.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import duolthdelivery.api.rest.model.Usuario;
import duolthdelivery.api.rest.repository.UsuarioRepository;


@Service
public class ImplementacaoUserDetailsService implements UserDetailsService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		//consultar no banco o usuario
		Usuario usuario = usuarioRepository.findUserByLogin(username);
		
		if (usuario == null) {
			throw new UsernameNotFoundException("Usuário não encontrado");
			
		} else {
			return new User(usuario.getLogin(), usuario.getSenha(), usuario.getAuthorities());
		}
	}

}
