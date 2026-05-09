package wolvesofdelivery.api.rest.repository;

import java.util.List;
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
    
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.roles WHERE u.login = ?1")
    Usuario findUserByLogin(String login);
}