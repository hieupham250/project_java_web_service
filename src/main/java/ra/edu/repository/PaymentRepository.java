package ra.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ra.edu.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

}
