package duolthdelivery.api.rest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import duolthdelivery.api.rest.model.MotoristaBloqueado;

public interface MotoristaBloqueadoRepository extends JpaRepository<MotoristaBloqueado, Long> {
	
	 List<MotoristaBloqueado> findByRestauranteId(Long restauranteId);
	 
	 Optional<MotoristaBloqueado> findByRestauranteIdAndMotoristaId(Long restauranteId, Long motoristaId);
	 
	 boolean existsByRestauranteIdAndMotoristaId(Long restauranteId, Long motoristaId);
	 
	 void deleteByRestauranteIdAndMotoristaId(Long restauranteId, Long motoristaId);
	 
	 List<MotoristaBloqueado> findByMotoristaId(Long motoristaId);

}
