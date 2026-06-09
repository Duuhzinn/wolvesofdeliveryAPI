package wolvesofdelivery.api.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wolvesofdelivery.api.rest.model.ConfiguracaoCorrida;

public interface ConfiguracaoCorridaRepository extends JpaRepository<ConfiguracaoCorrida, Long> {
    ConfiguracaoCorrida findByUsuarioId(Long usuarioId);
}
