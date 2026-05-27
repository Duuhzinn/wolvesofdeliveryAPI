package wolvesofdelivery.api.rest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import wolvesofdelivery.api.rest.model.Usuario;

@Repository
@RepositoryRestResource(path = "users")
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	List<Usuario> findByTipoUser(String string, Sort ascending);
	List<Usuario> findByTipoUserOrderByNomeAsc(String tipoUser);
	List<Usuario> findByTipoUserAndStatusOrderByPosicaofilaAsc(String tipoUser, Long status);

	@Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.roles WHERE u.login = ?1")
	Usuario findUserByLogin(String login);

	@Query("SELECT u FROM Usuario u WHERE LOWER(u.nome) LIKE LOWER(CONCAT('%', ?1, '%'))")
	List<Usuario> findUserByNome(String nome);
	
	//SELECIONA O PRIMEIRO DA LISTA DOS MOTORISTAS ONLINE
	Usuario findTop1ByTipoUserAndStatusOrderByPosicaofilaAsc(String tipoUser, Long status);
	
	List<Usuario> findByNomeContainingIgnoreCaseAndTipoUser(String nome, String tipoUser);
	
}