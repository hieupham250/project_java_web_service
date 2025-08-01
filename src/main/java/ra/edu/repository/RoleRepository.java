package ra.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ra.edu.entity.Role;
import ra.edu.enums.RoleName;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
