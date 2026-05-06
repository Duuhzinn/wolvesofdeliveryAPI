package wolvesofdelivery.api.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wolvesofdelivery.api.rest.model.Firebasetoken;

@Repository
public interface FirebasetokenRepository extends JpaRepository<Firebasetoken, Long> {
	
	 // Busca token pelo usuário
    Firebasetoken findByUsuarioId(Long usuarioId);
	
}
