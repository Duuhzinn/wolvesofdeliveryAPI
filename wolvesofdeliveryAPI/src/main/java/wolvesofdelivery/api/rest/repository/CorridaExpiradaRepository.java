package wolvesofdelivery.api.rest.repository;

import java.sql.Timestamp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import wolvesofdelivery.api.rest.model.CorridaExpirada;

@Repository
public interface CorridaExpiradaRepository extends JpaRepository<CorridaExpirada, Long> {

    long countByMotoristaId(Long motoristaId);

    long countByMotoristaIdAndDataExpiradaBetween(Long motoristaId, Timestamp inicio, Timestamp fim);

}