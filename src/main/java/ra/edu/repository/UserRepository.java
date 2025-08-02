package ra.edu.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ra.edu.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);

    @Query("SELECT u FROM User u WHERE u.isDeleted = false AND u.role.name <> 'ADMIN'")
    Page<User> findAllExcludeAdmin(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.isDeleted = false AND u.role.name <> 'ADMIN' " +
            "AND (LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<User> searchByNameOrEmail(@Param("keyword") String keyword, Pageable pageable);

    Optional<User> findByIdAndIsDeletedFalse(Integer id);
}
