package wolvesofdelivery.api.rest.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;

import wolvesofdelivery.api.rest.model.Corridas;

@Repository
@RepositoryRestResource(path = "racer")
public interface CorridasRepository extends JpaRepository<Corridas, Long> {

	@Query("SELECT c FROM Corridas c WHERE c.status_corrida = :status ORDER BY c.id DESC")
	Page<Corridas> findByStatusOrderByIdDesc(@Param("status") String status, Pageable pageable);

	@Query("SELECT c FROM Corridas c WHERE c.motorista.id = :motoristaId AND c.status_corrida = :status ORDER BY c.id DESC")
	Page<Corridas> findByMotoristaIdAndStatusOrderByIdDesc(@Param("motoristaId") Long motoristaId, @Param("status") String status, Pageable pageable);

	@Query("SELECT c FROM Corridas c WHERE c.cliente.id = :clienteId AND c.status_corrida = :status ORDER BY c.id DESC")
	Page<Corridas> findByClienteIdAndStatusOrderByIdDesc(@Param("clienteId") Long clienteId, @Param("status") String status, Pageable pageable);

	//________________ESTATISTICAS_______________

	// CONTA CORRIDAS DO MOTORISTA POR MÊS/ANO
	@Query("SELECT COUNT(c) FROM Corridas c WHERE c.motorista.id = :motoristaId AND EXTRACT(MONTH FROM c.data_chamada) = :mes AND EXTRACT(YEAR FROM c.data_chamada) = :ano")
	long countByMotoristaIdAndMesAno(@Param("motoristaId") Long motoristaId, @Param("mes") int mes, @Param("ano") int ano);

	// CONTA CORRIDAS DO MOTORISTA POR STATUS E MÊS/ANO
	@Query("SELECT COUNT(c) FROM Corridas c WHERE c.motorista.id = :motoristaId AND c.status_corrida = :status AND EXTRACT(MONTH FROM c.data_chamada) = :mes AND EXTRACT(YEAR FROM c.data_chamada) = :ano")
	long countByMotoristaIdAndStatusAndMesAno(@Param("motoristaId") Long motoristaId, @Param("status") String status, @Param("mes") int mes, @Param("ano") int ano);

	// CONTA TODAS AS CORRIDAS POR MÊS/ANO (ADM)
	@Query("SELECT COUNT(c) FROM Corridas c WHERE EXTRACT(MONTH FROM c.data_chamada) = :mes AND EXTRACT(YEAR FROM c.data_chamada) = :ano")
	long countByMesAno(@Param("mes") int mes, @Param("ano") int ano);

	// CONTA CORRIDAS POR STATUS E MÊS/ANO (ADM)
	@Query("SELECT COUNT(c) FROM Corridas c WHERE c.status_corrida = :status AND EXTRACT(MONTH FROM c.data_chamada) = :mes AND EXTRACT(YEAR FROM c.data_chamada) = :ano")
	long countByStatusAndMesAno(@Param("status") String status, @Param("mes") int mes, @Param("ano") int ano);

	// MOTORISTA QUE MAIS RODOU NO MÊS (ADM)
	@Query("SELECT c.motorista.nome FROM Corridas c WHERE EXTRACT(MONTH FROM c.data_chamada) = :mes AND EXTRACT(YEAR FROM c.data_chamada) = :ano AND c.motorista IS NOT NULL GROUP BY c.motorista.id, c.motorista.nome ORDER BY COUNT(c) DESC LIMIT 1")
	String findMotoristaTopByMesAno(@Param("mes") int mes, @Param("ano") int ano);

	// CONTA CORRIDAS DO CLIENTE POR MÊS/ANO
	@Query("SELECT COUNT(c) FROM Corridas c WHERE c.cliente.id = :clienteId AND EXTRACT(MONTH FROM c.data_chamada) = :mes AND EXTRACT(YEAR FROM c.data_chamada) = :ano")
	long countByClienteIdAndMesAno(@Param("clienteId") Long clienteId, @Param("mes") int mes, @Param("ano") int ano);

	// CONTA CORRIDAS DO CLIENTE POR STATUS E MÊS/ANO
	@Query("SELECT COUNT(c) FROM Corridas c WHERE c.cliente.id = :clienteId AND c.status_corrida = :status AND EXTRACT(MONTH FROM c.data_chamada) = :mes AND EXTRACT(YEAR FROM c.data_chamada) = :ano")
	long countByClienteIdAndStatusAndMesAno(@Param("clienteId") Long clienteId, @Param("status") String status, @Param("mes") int mes, @Param("ano") int ano);

	// CONTAGEM POR PERÍODO - ADMIN
	@Query("SELECT COUNT(c) FROM Corridas c WHERE c.data_chamada BETWEEN :inicio AND :fim")
	long countByDataBetween(@Param("inicio") Timestamp inicio, @Param("fim") Timestamp fim);

	@Query("SELECT COUNT(c) FROM Corridas c WHERE c.status_corrida = :status AND c.data_chamada BETWEEN :inicio AND :fim")
	long countByStatusAndDataBetween(@Param("status") String status, @Param("inicio") Timestamp inicio, @Param("fim") Timestamp fim);

	@Query("SELECT c.motorista.nome FROM Corridas c WHERE c.data_chamada BETWEEN :inicio AND :fim AND c.motorista IS NOT NULL GROUP BY c.motorista.id, c.motorista.nome ORDER BY COUNT(c) DESC LIMIT 1")
	String findMotoristaTopByDataBetween(@Param("inicio") Timestamp inicio, @Param("fim") Timestamp fim);

	// CONTAGEM POR PERÍODO - CLIENTE
	@Query("SELECT COUNT(c) FROM Corridas c WHERE c.cliente.id = :clienteId AND c.data_chamada BETWEEN :inicio AND :fim")
	long countByClienteIdAndDataBetween(@Param("clienteId") Long clienteId, @Param("inicio") Timestamp inicio, @Param("fim") Timestamp fim);

	@Query("SELECT COUNT(c) FROM Corridas c WHERE c.cliente.id = :clienteId AND c.status_corrida = :status AND c.data_chamada BETWEEN :inicio AND :fim")
	long countByClienteIdAndStatusAndDataBetween(@Param("clienteId") Long clienteId, @Param("status") String status, @Param("inicio") Timestamp inicio, @Param("fim") Timestamp fim);

	// CONTAGEM POR PERÍODO - MOTORISTA
	@Query("SELECT COUNT(c) FROM Corridas c WHERE c.motorista.id = :motoristaId AND c.data_chamada BETWEEN :inicio AND :fim")
	long countByMotoristaIdAndDataBetween(@Param("motoristaId") Long motoristaId, @Param("inicio") Timestamp inicio, @Param("fim") Timestamp fim);

	@Query("SELECT COUNT(c) FROM Corridas c WHERE c.motorista.id = :motoristaId AND c.status_corrida = :status AND c.data_chamada BETWEEN :inicio AND :fim")
	long countByMotoristaIdAndStatusAndDataBetween(@Param("motoristaId") Long motoristaId, @Param("status") String status, @Param("inicio") Timestamp inicio, @Param("fim") Timestamp fim);

	//________________SOMA DO VALOR DAS CORRIDAS_______________

	// SOMA VALOR DAS CORRIDAS DO MOTORISTA POR STATUS E MÊS/ANO
	@Query(value = "SELECT COALESCE(SUM(c.valor_corrida), 0) FROM corridas c WHERE c.motorista_id = :motoristaId AND c.status_corrida = :status AND EXTRACT(MONTH FROM c.data_chamada) = :mes AND EXTRACT(YEAR FROM c.data_chamada) = :ano", nativeQuery = true)
	BigDecimal sumValorByMotoristaIdAndStatusAndMesAno(@Param("motoristaId") Long motoristaId, @Param("status") String status, @Param("mes") int mes, @Param("ano") int ano);

	// SOMA VALOR DE TODAS AS CORRIDAS POR STATUS E MÊS/ANO (ADM)
	@Query(value = "SELECT COALESCE(SUM(c.valor_corrida), 0) FROM corridas c WHERE c.status_corrida = :status AND EXTRACT(MONTH FROM c.data_chamada) = :mes AND EXTRACT(YEAR FROM c.data_chamada) = :ano", nativeQuery = true)
	BigDecimal sumValorByStatusAndMesAno(@Param("status") String status, @Param("mes") int mes, @Param("ano") int ano);

	// SOMA VALOR DAS CORRIDAS DO CLIENTE POR STATUS E MÊS/ANO
	@Query(value = "SELECT COALESCE(SUM(c.valor_corrida), 0) FROM corridas c WHERE c.cliente_id = :clienteId AND c.status_corrida = :status AND EXTRACT(MONTH FROM c.data_chamada) = :mes AND EXTRACT(YEAR FROM c.data_chamada) = :ano", nativeQuery = true)
	BigDecimal sumValorByClienteIdAndStatusAndMesAno(@Param("clienteId") Long clienteId, @Param("status") String status, @Param("mes") int mes, @Param("ano") int ano);

	// SOMA VALOR POR PERÍODO - ADMIN
	@Query(value = "SELECT COALESCE(SUM(c.valor_corrida), 0) FROM corridas c WHERE c.status_corrida = :status AND c.data_chamada BETWEEN :inicio AND :fim", nativeQuery = true)
	BigDecimal sumValorByStatusAndDataBetween(@Param("status") String status, @Param("inicio") Timestamp inicio, @Param("fim") Timestamp fim);

	// SOMA VALOR POR PERÍODO - CLIENTE
	@Query(value = "SELECT COALESCE(SUM(c.valor_corrida), 0) FROM corridas c WHERE c.cliente_id = :clienteId AND c.status_corrida = :status AND c.data_chamada BETWEEN :inicio AND :fim", nativeQuery = true)
	BigDecimal sumValorByClienteIdAndStatusAndDataBetween(@Param("clienteId") Long clienteId, @Param("status") String status, @Param("inicio") Timestamp inicio, @Param("fim") Timestamp fim);

	// SOMA VALOR POR PERÍODO - MOTORISTA
	@Query(value = "SELECT COALESCE(SUM(c.valor_corrida), 0) FROM corridas c WHERE c.motorista_id = :motoristaId AND c.status_corrida = :status AND c.data_chamada BETWEEN :inicio AND :fim", nativeQuery = true)
	BigDecimal sumValorByMotoristaIdAndStatusAndDataBetween(@Param("motoristaId") Long motoristaId, @Param("status") String status, @Param("inicio") Timestamp inicio, @Param("fim") Timestamp fim);
	}