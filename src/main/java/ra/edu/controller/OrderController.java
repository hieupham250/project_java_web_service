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
import ra.edu.dto.request.UpdateOrderStatusRequest;
import ra.edu.dto.response.BaseResponse;
import ra.edu.dto.response.OrderResponse;
import ra.edu.dto.response.PagedData;
import ra.edu.service.OrderService;
import ra.edu.util.SecurityUtil;

import java.time.LocalDateTime;

import static ra.edu.util.ResponseUtil.convertToPagedData;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<BaseResponse<PagedData<OrderResponse>>> getOrders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        Integer userId = SecurityUtil.getCurrentUserId();
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<OrderResponse> orders = orderService.getOrdersByUserId(userId, pageable);

        return ResponseEntity.ok(new BaseResponse<>(
                true,
                "Lấy danh sách đơn hàng thành công",
                convertToPagedData(orders),
                null,
                LocalDateTime.now()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<OrderResponse>> getOrderById(@PathVariable Integer id) {
        Integer userId = SecurityUtil.getCurrentUserId();
        OrderResponse order = orderService.getOrderById(id, userId);
        return ResponseEntity.ok(new BaseResponse<>(
                true,
                "Lấy chi tiết đơn hàng thành công",
                order,
                null,
                LocalDateTime.now()));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<OrderResponse>> createOrderFromCart() {
        Integer userId = SecurityUtil.getCurrentUserId();
        OrderResponse order = orderService.createOrderFromCart(userId);
        return new ResponseEntity<>(
                new BaseResponse<>(
                        true,
                        "Thêm đơn hàng thành công",
                        order,
                        null,
                        LocalDateTime.now()
                ),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Integer id,
            @RequestBody @Valid UpdateOrderStatusRequest request
    ) {
        OrderResponse response = orderService.updateOrderStatus(id, request.getStatus());
        return ResponseEntity.ok(response);
    }
}
