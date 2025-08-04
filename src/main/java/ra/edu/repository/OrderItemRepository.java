    package ra.edu.repository;

    import org.springframework.data.jpa.repository.JpaRepository;
    import ra.edu.entity.OrderItem;

    public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
        boolean existsByProductId(Integer productId);
    }
