package wolvesofdelivery.api.rest.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

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
}
