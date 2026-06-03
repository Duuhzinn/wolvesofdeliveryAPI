package wolvesofdelivery.api.rest.repository;

import java.sql.Timestamp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import wolvesofdelivery.api.rest.model.CorridaExpirada;

@Repository
public interface CorridaExpiradaRepository extends JpaRepository<CorridaExpirada, Long> {

    long countByMotoristaId(Long motoristaId);

    long countByMotoristaIdAndDataExpiradaBetween(Long motoristaId, Timestamp inicio, Timestamp fim);
    
    @Query("SELECT COUNT(c) FROM CorridaExpirada c WHERE MONTH(c.dataExpirada) = :mes AND YEAR(c.dataExpirada) = :ano")
    long countByMesAno(@Param("mes") int mes, @Param("ano") int ano);
    
    long countByDataExpiradaBetween(Timestamp inicio, Timestamp fim);

}