package ra.edu.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ra.edu.dto.request.CartRequest;
import ra.edu.dto.response.CartItemResponse;
import ra.edu.entity.CartItem;
import ra.edu.entity.Product;
import ra.edu.entity.User;
import ra.edu.exception.NotFoundException;
import ra.edu.mapper.CartItemMapper;
import ra.edu.repository.CartItemRepository;
import ra.edu.repository.ProductRepository;
import ra.edu.repository.UserRepository;
import ra.edu.service.CartService;
import ra.edu.util.SecurityUtil;

import java.time.LocalDate;

@Service
public class CartServiceImp implements CartService {
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;


    @Override
    public Page<CartItemResponse> getUserCart(Integer userId, Pageable pageable) {
        return cartItemRepository.findByUserId(userId, pageable)
                .map(CartItemMapper::toResponse);
    }

    @Override
    public CartItemResponse addToCart(Integer userId, CartRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NotFoundException("Sản phẩm không tồn tại"));

        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, request.getProductId())
                .map(existing -> {
                    existing.setQuantity(existing.getQuantity() + request.getQuantity());
                    existing.setUpdatedAt(LocalDate.now());
                    return cartItemRepository.save(existing);
                })
                .orElseGet(() -> cartItemRepository.save(CartItem.builder()
                        .user(user)
                        .product(product)
                        .quantity(request.getQuantity())
                        .createdAt(LocalDate.now())
                        .updatedAt(LocalDate.now())
                        .build()));
        return CartItemMapper.toResponse(cartItem);
    }

    @Override
    public CartItemResponse updateQuantity(Integer userId, CartRequest request) {
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, request.getProductId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy giỏ hàng"));
        cartItem.setQuantity(request.getQuantity());
        cartItem.setUpdatedAt(LocalDate.now());
        cartItemRepository.save(cartItem);
        return CartItemMapper.toResponse(cartItem);
    }

    @Override
    public CartItemResponse deleteItem(Integer userId, Integer id) {
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Giỏ hàng không tồn tại"));

        if (!cartItem.getUser().getId().equals(userId)) {
            throw new NotFoundException("Bạn không có quyền xóa mục này");
        }

        cartItemRepository.delete(cartItem);
        return CartItemMapper.toResponse(cartItem);
    }

    @Override
    @Transactional
    public boolean clearCart(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Người dùng không tồn tại");
        }

        int deletedCount = cartItemRepository.deleteByUserId(userId);
        return deletedCount > 0;
    }
}
