package ra.edu.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ra.edu.dto.response.OrderResponse;
import ra.edu.entity.*;
import ra.edu.enums.OrderStatus;
import ra.edu.enums.RoleName;
import ra.edu.exception.NotFoundException;
import ra.edu.mapper.OrderMapper;
import ra.edu.repository.CartItemRepository;
import ra.edu.repository.OrderRepository;
import ra.edu.repository.ProductRepository;
import ra.edu.repository.UserRepository;
import ra.edu.service.OrderService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImp implements OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartItemRepository cartItemRepository;

    @Override
    public Page<OrderResponse> getOrdersByUserId(Integer userId, Pageable pageable) {
        Page<Order> ordersPage;

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));

        switch (user.getRole().getName()) {
            case ADMIN:
            case SALES:
                ordersPage = orderRepository.findAll(pageable);
                break;
            case CUSTOMER:
                ordersPage = orderRepository.findByUserId(userId, pageable);
                break;
            default:
                throw new NotFoundException("Vai trò người dùng không hợp lệ");
        }
        return ordersPage.map(OrderMapper::toResponse);
    }

    @Override
    public OrderResponse getOrderById(Integer id, Integer userId) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Đơn hàng không tồn tại"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));

        switch (user.getRole().getName()) {
            case ADMIN:
            case SALES:
                return OrderMapper.toResponse(order);
            case CUSTOMER:
                if (!order.getUser().getId().equals(userId)) {
                    throw new NotFoundException("Bạn không có quyền xem đơn hàng này");
                }
                return OrderMapper.toResponse(order);
            default:
                throw new NotFoundException("Vai trò người dùng không hợp lệ");
        }
    }

    @Override
    public OrderResponse createOrderFromCart(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));

        if (user.getRole().getName() != RoleName.CUSTOMER) {
            throw new NotFoundException("Chỉ khách hàng mới có thể tạo đơn hàng");
        }

        List<CartItem> cartItems = user.getCartItems();
        if (cartItems.isEmpty()) {
            throw new NotFoundException("Giỏ hàng trống, không thể tạo đơn hàng");
        }

        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
            Product product = cartItem.getProduct();
            int orderedQuantity = cartItem.getQuantity();

            if (product.getStock() < orderedQuantity) {
                throw new NotFoundException("Sản phẩm '" + product.getName() + "' không đủ hàng trong kho");
            }
            // Trừ tồn kho
            product.setStock(product.getStock() - orderedQuantity);
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());

            return orderItem;
        }).collect(Collectors.toList());

        BigDecimal totalPrice = orderItems.stream()
                .map(oi -> oi.getPrice().multiply(BigDecimal.valueOf(oi.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(user.getAddress());
        order.setInternalNotes("Đơn hàng tạo tự động từ giỏ hàng");
        order.setCreatedAt(LocalDate.now());
        order.setTotalPrice(totalPrice);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderItems(orderItems);
        orderItems.forEach(item -> item.setOrder(order));

        orderRepository.save(order);

        // xóa giỏ hàng sau khi order xong
        cartItemRepository.deleteByUserId(userId);

        return OrderMapper.toResponse(order);
    }

    @Override
    public OrderResponse updateOrderStatus(Integer orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đơn hàng"));

        order.setStatus(status);
        orderRepository.save(order);

        return OrderMapper.toResponse(order);
    }
}
