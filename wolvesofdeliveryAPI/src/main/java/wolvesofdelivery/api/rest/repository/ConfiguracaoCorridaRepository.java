package wolvesofdelivery.api.rest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import wolvesofdelivery.api.rest.model.ConfiguracaoCorrida;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ConfiguracaoCorridaRepository extends JpaRepository<ConfiguracaoCorrida, Long> {
    ConfiguracaoCorrida findByUsuarioId(Long usuarioId);
    List<ConfiguracaoCorrida> findByUsuario_TipoUser(String tipoUser);
    Page<ConfiguracaoCorrida> findByUsuario_TipoUser(String tipoUser, Pageable pageable);
}
