package wolvesofdelivery.api.rest.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import wolvesofdelivery.api.rest.model.Usuario;

@Repository
@RepositoryRestResource(path = "users")
public interface UsuarioRepository extends CrudRepository<Usuario, Long> {
	
	List<Usuario> findByTipoUser(String string, Sort ascending);
	List<Usuario> findByTipoUserOrderByNomeAsc(String tipoUser);

	@Query("select u from Usuario u where u.login = ?1")
	Usuario findUserByLogin(String login);

}
