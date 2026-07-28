package duolthdelivery.api.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import duolthdelivery.api.rest.model.Role;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
	Role findByNomeRole(String nomeRole);

}
