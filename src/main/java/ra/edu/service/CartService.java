package ra.edu.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ra.edu.dto.request.CartRequest;
import ra.edu.dto.response.CartItemResponse;

public interface CartService {
    Page<CartItemResponse> getUserCart(Integer userId, Pageable pageable);
    CartItemResponse addToCart(Integer userId, CartRequest request);
    CartItemResponse updateQuantity(Integer userId, CartRequest request);
    CartItemResponse deleteItem(Integer userId, Integer id);
    boolean clearCart(Integer userId);
}
