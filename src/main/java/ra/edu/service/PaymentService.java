package ra.edu.service;

import ra.edu.dto.request.PaymentRequest;
import ra.edu.dto.response.PaymentResponse;

public interface PaymentService {
    PaymentResponse createPayment(PaymentRequest request);
    PaymentResponse getPaymentById(Integer id);
}
