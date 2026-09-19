package ma.enset.digitalbanking.repositories;

import ma.enset.digitalbanking.entities.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppRoleRepository extends JpaRepository<AppRole, String> {
}
