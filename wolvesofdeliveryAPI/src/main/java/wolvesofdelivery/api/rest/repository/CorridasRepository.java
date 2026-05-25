package wolvesofdelivery.api.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import wolvesofdelivery.api.rest.model.Corridas;

@Repository
@RepositoryRestResource(path = "racer")
public interface CorridasRepository extends JpaRepository<Corridas, Long> {

}
