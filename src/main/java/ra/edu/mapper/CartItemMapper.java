package ra.edu.mapper;

import ra.edu.dto.response.CartItemResponse;
import ra.edu.entity.CartItem;

import java.math.BigDecimal;

public class CartItemMapper {
    public static CartItemResponse toResponse(CartItem item) {
        if(item == null) return null;
        return new CartItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getPrice(),
                item.getQuantity(),
                item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
        );
    }
}
