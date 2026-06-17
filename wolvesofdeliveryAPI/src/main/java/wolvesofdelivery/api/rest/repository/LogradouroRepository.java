package wolvesofdelivery.api.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wolvesofdelivery.api.rest.model.Logradouro;
import java.util.List;

public interface LogradouroRepository extends JpaRepository<Logradouro, Long> {
    List<Logradouro> findByRuaContainingIgnoreCase(String rua);
}