package ra.edu.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ra.edu.dto.request.PaymentRequest;
import ra.edu.dto.response.BaseResponse;
import ra.edu.dto.response.PaymentResponse;
import ra.edu.service.PaymentService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<BaseResponse<PaymentResponse>> createPayment(@RequestBody @Valid PaymentRequest request) {
        PaymentResponse response = paymentService.createPayment(request);
        return new ResponseEntity<>(new BaseResponse<>(
                true,
                "Thanh toán thành công",
                response,
                null,
                LocalDateTime.now()
        ), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<PaymentResponse>> getPaymentById(@PathVariable Integer id) {
        PaymentResponse response = paymentService.getPaymentById(id);
        return ResponseEntity.ok(new BaseResponse<>(
                true,
                "Lấy chi tiết thanh toán thành công",
                response,
                null,
                LocalDateTime.now()
        ));
    }
}
