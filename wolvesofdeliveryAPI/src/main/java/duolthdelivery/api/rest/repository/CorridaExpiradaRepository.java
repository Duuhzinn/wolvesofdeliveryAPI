package duolthdelivery.api.rest.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import duolthdelivery.api.rest.model.CorridaExpirada;

@Repository
public interface CorridaExpiradaRepository extends JpaRepository<CorridaExpirada, Long> {

    long countByMotoristaId(Long motoristaId);

    long countByMotoristaIdAndDataExpiradaBetween(Long motoristaId, Timestamp inicio, Timestamp fim);
    
    @Query("SELECT COUNT(c) FROM CorridaExpirada c WHERE MONTH(c.dataExpirada) = :mes AND YEAR(c.dataExpirada) = :ano")
    long countByMesAno(@Param("mes") int mes, @Param("ano") int ano);
    
    long countByDataExpiradaBetween(Timestamp inicio, Timestamp fim);
    
    @Query("SELECT c.motorista.nome, COUNT(c) FROM CorridaExpirada c WHERE c.dataExpirada BETWEEN :inicio AND :fim GROUP BY c.motorista.nome ORDER BY COUNT(c) DESC")
    List<Object[]> findMotoristasComPerdidasNoDia(@Param("inicio") Timestamp inicio, @Param("fim") Timestamp fim);

}