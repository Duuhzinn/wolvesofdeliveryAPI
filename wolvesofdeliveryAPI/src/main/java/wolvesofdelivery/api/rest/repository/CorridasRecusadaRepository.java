package wolvesofdelivery.api.rest.repository;

import java.sql.Timestamp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import wolvesofdelivery.api.rest.model.CorridaRecusada;

@Repository
@RepositoryRestResource(path = "refused") //REFUSED = RECUSADA
public interface CorridasRecusadaRepository extends JpaRepository<CorridaRecusada, Long> {

	//TOTAL CORRIDA RECUSADA POR MOTORISTA
	long countByMotoristaId(Long motoristaId);
	
	//TOTAL DE CORRIDA RECUSADA POR MOTORISTA EM UM PERIODO
	long countByMotoristaIdAndDataRecusaBetween(Long motoristaId, Timestamp inicio, Timestamp fim);
	
	@Query("SELECT COUNT(c) FROM CorridaRecusada c WHERE MONTH(c.dataRecusa) = :mes AND YEAR(c.dataRecusa) = :ano")
	long countByMesAno(@Param("mes") int mes, @Param("ano") int ano);
	
	long countByDataRecusaBetween(Timestamp inicio, Timestamp fim);
}
