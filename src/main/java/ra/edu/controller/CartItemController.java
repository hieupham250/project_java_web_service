package ra.edu.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ra.edu.dto.request.CartRequest;
import ra.edu.dto.response.BaseResponse;
import ra.edu.dto.response.CartItemResponse;
import ra.edu.dto.response.PagedData;
import ra.edu.service.CartService;
import ra.edu.util.SecurityUtil;

import java.time.LocalDateTime;

import static ra.edu.util.ResponseUtil.convertToPagedData;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartItemController {
    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<BaseResponse<PagedData<CartItemResponse>>> getCartItems(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Integer userId = SecurityUtil.getCurrentUserId();
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<CartItemResponse> cartItems = cartService.getUserCart(userId, pageable);
        return ResponseEntity.ok(new BaseResponse<>(
                true,
                "Lấy danh sách giỏ hàng thành công",
                convertToPagedData(cartItems),
                null,
                LocalDateTime.now()
        ));
    }

    @PostMapping("/items")
    public ResponseEntity<BaseResponse<CartItemResponse>> addCartItem(@RequestBody @Valid CartRequest request) {
        Integer userId = SecurityUtil.getCurrentUserId();
        return new ResponseEntity<>(
                new BaseResponse<>(
                        true,
                        "Thêm sản phẩm vào giỏ hàng thành công",
                        cartService.addToCart(userId, request),
                        null,
                        LocalDateTime.now()
                ),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/items")
    public ResponseEntity<BaseResponse<CartItemResponse>> updateCartItem(@RequestBody @Valid CartRequest request) {
        Integer userId = SecurityUtil.getCurrentUserId();
        return new ResponseEntity<>(
                new BaseResponse<>(
                        true,
                        "Cập nhật số lượng sản phẩm vào giỏ hàng thành công",
                        cartService.updateQuantity(userId, request),
                        null,
                        LocalDateTime.now()
                ),
                HttpStatus.CREATED
        );
    }

    @DeleteMapping("items/{id}")
    public ResponseEntity<BaseResponse<CartItemResponse>> deleteCartItem(@PathVariable Integer id) {
        Integer userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(new BaseResponse<>(
                true,
                "Xóa sản phẩm thành công",
                cartService.deleteItem(userId, id),
                null,
                LocalDateTime.now()
        ));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<BaseResponse<Void>> clearCart() {
        Integer userId = SecurityUtil.getCurrentUserId();
        boolean isCleared = cartService.clearCart(userId);

        if (isCleared) {
            return ResponseEntity.ok(new BaseResponse<>(
                    true,
                    "Đã xóa toàn bộ giỏ hàng",
                    null,
                    null,
                    LocalDateTime.now()
            ));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse<>(
                    false,
                    "Không có sản phẩm nào trong giỏ hàng",
                    null,
                    null,
                    LocalDateTime.now()
            ));
        }
    }
}
