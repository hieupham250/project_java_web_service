package ra.edu.mapper;

import ra.edu.dto.response.OrderItemResponse;
import ra.edu.dto.response.OrderResponse;
import ra.edu.entity.Order;
import ra.edu.entity.OrderItem;

import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {
    public static OrderResponse toResponse(Order order) {
        if (order == null) return null;

        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(OrderMapper::toItemResponse)
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                order.getUser().getFullName(),
                order.getUser().getEmail(),
                order.getUser().getPhone(),
                order.getShippingAddress(),
                order.getInternalNotes(),
                order.getTotalPrice(),
                order.getCreatedAt(),
                order.getStatus(),
                itemResponses
        );
    }

    private static OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getPrice(),
                item.getQuantity()
        );
    }
}
