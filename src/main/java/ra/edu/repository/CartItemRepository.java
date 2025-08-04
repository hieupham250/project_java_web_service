package ra.edu.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ra.edu.entity.CartItem;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    Page<CartItem> findByUserId(Integer userId, Pageable pageable);
    Optional<CartItem> findByUserIdAndProductId(Integer userId, Integer productId);
    int deleteByUserId(Integer userId);
}
