package wolvesofdelivery.api.rest.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

import wolvesofdelivery.api.rest.model.Corridas;

@Repository
@RepositoryRestResource(path = "racer")
public interface CorridasRepository extends JpaRepository<Corridas, Long> {

	@Query("SELECT c FROM Corridas c WHERE c.status_corrida = :status ORDER BY c.id DESC")
	Page <Corridas> findByStatusOrderByIdDesc(@Param("status") String status, Pageable pageable);

	@Query("SELECT c FROM Corridas c WHERE c.motorista.id = :motoristaId AND c.status_corrida = :status ORDER BY c.id DESC")
	Page <Corridas> findByMotoristaIdAndStatusOrderByIdDesc(@Param("motoristaId") Long motoristaId, @Param("status") String status, Pageable pageable);

	@Query("SELECT c FROM Corridas c WHERE c.cliente.id = :clienteId AND c.status_corrida = :status ORDER BY c.id DESC")
	Page <Corridas> findByClienteIdAndStatusOrderByIdDesc(@Param("clienteId") Long clienteId, @Param("status") String status, Pageable pageable);
	
	//________________ESTATISTICAS_______________
	
	// CONTA CORRIDAS DO MOTORISTA POR MÊS/ANO
	@Query("SELECT COUNT(c) FROM Corridas c WHERE c.motorista.id = :motoristaId AND MONTH(c.data_chamada) = :mes AND YEAR(c.data_chamada) = :ano")
	long countByMotoristaIdAndMesAno(@Param("motoristaId") Long motoristaId, @Param("mes") int mes, @Param("ano") int ano);

	// CONTA CORRIDAS DO MOTORISTA POR STATUS E MÊS/ANO
	@Query("SELECT COUNT(c) FROM Corridas c WHERE c.motorista.id = :motoristaId AND c.status_corrida = :status AND MONTH(c.data_chamada) = :mes AND YEAR(c.data_chamada) = :ano")
	long countByMotoristaIdAndStatusAndMesAno(@Param("motoristaId") Long motoristaId, @Param("status") String status, @Param("mes") int mes, @Param("ano") int ano);

	// CONTA TODAS AS CORRIDAS POR MÊS/ANO (ADM)
	@Query("SELECT COUNT(c) FROM Corridas c WHERE MONTH(c.data_chamada) = :mes AND YEAR(c.data_chamada) = :ano")
	long countByMesAno(@Param("mes") int mes, @Param("ano") int ano);

	// CONTA CORRIDAS POR STATUS E MÊS/ANO (ADM)
	@Query("SELECT COUNT(c) FROM Corridas c WHERE c.status_corrida = :status AND MONTH(c.data_chamada) = :mes AND YEAR(c.data_chamada) = :ano")
	long countByStatusAndMesAno(@Param("status") String status, @Param("mes") int mes, @Param("ano") int ano);

	// MOTORISTA QUE MAIS RODOU NO MÊS (ADM)
	@Query("SELECT c.motorista.nome FROM Corridas c WHERE MONTH(c.data_chamada) = :mes AND YEAR(c.data_chamada) = :ano AND c.motorista IS NOT NULL GROUP BY c.motorista.id, c.motorista.nome ORDER BY COUNT(c) DESC LIMIT 1")
	String findMotoristaTopByMesAno(@Param("mes") int mes, @Param("ano") int ano);
	
	// CONTA CORRIDAS DO CLIENTE POR MÊS/ANO
	@Query("SELECT COUNT(c) FROM Corridas c WHERE c.cliente.id = :clienteId AND MONTH(c.data_chamada) = :mes AND YEAR(c.data_chamada) = :ano")
	long countByClienteIdAndMesAno(@Param("clienteId") Long clienteId, @Param("mes") int mes, @Param("ano") int ano);

	// CONTA CORRIDAS DO CLIENTE POR STATUS E MÊS/ANO
	@Query("SELECT COUNT(c) FROM Corridas c WHERE c.cliente.id = :clienteId AND c.status_corrida = :status AND MONTH(c.data_chamada) = :mes AND YEAR(c.data_chamada) = :ano")
	long countByClienteIdAndStatusAndMesAno(@Param("clienteId") Long clienteId, @Param("status") String status, @Param("mes") int mes, @Param("ano") int ano);
	
	// FILTRO POR DATA - ADMIN
	@Query("SELECT c FROM Corridas c WHERE c.data_chamada BETWEEN :inicio AND :fim ORDER BY c.id DESC")
	Page<Corridas> findByDataBetween(@Param("inicio") Timestamp inicio, @Param("fim") Timestamp fim, Pageable pageable);

	// FILTRO POR DATA - CLIENTE
	@Query("SELECT c FROM Corridas c WHERE c.cliente.id = :clienteId AND c.data_chamada BETWEEN :inicio AND :fim ORDER BY c.id DESC")
	Page<Corridas> findByClienteIdAndDataBetween(@Param("clienteId") Long clienteId, @Param("inicio") Timestamp inicio, @Param("fim") Timestamp fim, Pageable pageable);

	// FILTRO POR DATA - MOTORISTA
	@Query("SELECT c FROM Corridas c WHERE c.motorista.id = :motoristaId AND c.data_chamada BETWEEN :inicio AND :fim ORDER BY c.id DESC")
	Page<Corridas> findByMotoristaIdAndDataBetween(@Param("motoristaId") Long motoristaId, @Param("inicio") Timestamp inicio, @Param("fim") Timestamp fim, Pageable pageable);
	
}
