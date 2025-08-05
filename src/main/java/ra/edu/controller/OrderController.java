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
import ra.edu.dto.request.CancelOrderRequest;
import ra.edu.dto.request.UpdateOrderInfoRequest;
import ra.edu.dto.request.UpdateOrderStatusRequest;
import ra.edu.dto.response.*;
import ra.edu.service.InvoiceService;
import ra.edu.service.OrderService;
import ra.edu.util.SecurityUtil;

import java.time.LocalDateTime;
import java.util.List;

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

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<OrderResponse>> updateOrderByCustomer(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateOrderInfoRequest request
    ) {
        Integer userId = SecurityUtil.getCurrentUserId();
        OrderResponse response = orderService.updateOrderByCustomer(id, userId, request);

        return ResponseEntity.ok(new BaseResponse<>(
                true,
                "Cập nhật đơn hàng thành công",
                response,
                null,
                LocalDateTime.now()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<OrderResponse>> cancelOrder(
            @PathVariable Integer id,
            @RequestBody @Valid CancelOrderRequest request
    ) {
        OrderResponse response = orderService.cancelOrder(id, request);
        return ResponseEntity.ok(new BaseResponse<>(
                true,
                "Đơn hàng đã bị hủy",
                response,
                null,
                LocalDateTime.now()
        ));
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<BaseResponse<List<OrderItemResponse>>> getOrderItems(@PathVariable Integer id) {
        Integer userId = SecurityUtil.getCurrentUserId();
        List<OrderItemResponse> items = orderService.getOrderItems(id, userId);
        return ResponseEntity.ok(new BaseResponse<>(
                true,
                "Lấy danh sách sản phẩm trong đơn hàng thành công",
                items,
                null,
                LocalDateTime.now()
        ));
    }

    @GetMapping("/orders/{orderId}/invoice")
    public ResponseEntity<BaseResponse<InvoiceResponse>> getInvoiceByOrderId(@PathVariable Integer orderId) {
        InvoiceResponse invoice = orderService.getInvoiceByOrderId(orderId);
        return ResponseEntity.ok(new BaseResponse<>(
                true,
                "Lấy hóa đơn theo đơn hàng thành công",
                invoice,
                null,
                LocalDateTime.now()
        ));
    }
}
