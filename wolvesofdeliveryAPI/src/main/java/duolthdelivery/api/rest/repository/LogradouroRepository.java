package duolthdelivery.api.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import duolthdelivery.api.rest.model.Logradouro;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LogradouroRepository extends JpaRepository<Logradouro, Long> {
    List<Logradouro> findByRuaContainingIgnoreCase(String rua);
    
    Page<Logradouro> findByRuaContainingIgnoreCase(String rua, Pageable pageable);
}