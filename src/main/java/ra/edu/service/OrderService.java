package ra.edu.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ra.edu.dto.request.CancelOrderRequest;
import ra.edu.dto.request.UpdateOrderInfoRequest;
import ra.edu.dto.response.InvoiceResponse;
import ra.edu.dto.response.OrderItemResponse;
import ra.edu.dto.response.OrderResponse;
import ra.edu.enums.OrderStatus;

import java.util.List;

public interface OrderService {
    Page<OrderResponse> getOrdersByUserId(Integer userId, Pageable pageable);
    OrderResponse getOrderById(Integer id, Integer userId);
    OrderResponse createOrderFromCart(Integer userId);
    OrderResponse updateOrderStatus(Integer orderId, OrderStatus status);
    OrderResponse updateOrderByCustomer(Integer orderId, Integer userId, UpdateOrderInfoRequest request);
    OrderResponse cancelOrder(Integer orderId, CancelOrderRequest request);
    List<OrderItemResponse> getOrderItems(Integer orderId, Integer userId);
    InvoiceResponse getInvoiceByOrderId(Integer orderId);
}
