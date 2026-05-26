package wolvesofdelivery.api.rest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import wolvesofdelivery.api.rest.model.Corridas;

@Repository
@RepositoryRestResource(path = "racer")
public interface CorridasRepository extends JpaRepository<Corridas, Long> {

	//SELECIONA AS CORRIDAS POR MOTORISTA
	List<Corridas> findByMotorista_IdOrderByIdDesc(Long motoristaId);
	
	//SELECIONA AS CORRIDAS DO DESPACHANTE
	List<Corridas> findByCliente_IdOrderByIdDesc(Long clienteId);
	
	//SELECIONA TODAS AS CORRIDAS PARA O ADMIN
	List<Corridas> findAllByOrderByIdDesc();
}
