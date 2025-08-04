package ra.edu.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ra.edu.dto.response.OrderResponse;
import ra.edu.enums.OrderStatus;

public interface OrderService {
    Page<OrderResponse> getOrdersByUserId(Integer userId, Pageable pageable);
    OrderResponse getOrderById(Integer id, Integer userId);
    OrderResponse createOrderFromCart(Integer userId);
    OrderResponse updateOrderStatus(Integer orderId, OrderStatus status);
}
