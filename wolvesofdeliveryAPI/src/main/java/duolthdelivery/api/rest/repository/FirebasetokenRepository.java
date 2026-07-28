package duolthdelivery.api.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import duolthdelivery.api.rest.model.Firebasetoken;

@Repository
public interface FirebasetokenRepository extends JpaRepository<Firebasetoken, Long> {
	
    // BUSCA TOKEN PELO USUÁRIO
    Firebasetoken findByUsuarioId(Long usuarioId);

    // BUSCA PELO TOKEN PARA REMOÇÃO QUANDO INVÁLIDO
    Firebasetoken findByToken(String token);
	
}
