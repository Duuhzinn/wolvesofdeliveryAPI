package duolthdelivery.api.rest.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import duolthdelivery.api.rest.model.CorridaRecusada;

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
	
	@Query("SELECT c.motorista.nome, COUNT(c) FROM CorridaRecusada c WHERE c.dataRecusa BETWEEN :inicio AND :fim GROUP BY c.motorista.nome ORDER BY COUNT(c) DESC")
	List<Object[]> findMotoristasComRecusasNoDia(@Param("inicio") Timestamp inicio, @Param("fim") Timestamp fim);
}
