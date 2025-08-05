package ra.edu.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ra.edu.dto.request.PaymentRequest;
import ra.edu.dto.response.PaymentResponse;
import ra.edu.entity.Invoice;
import ra.edu.entity.Payment;
import ra.edu.enums.InvoiceStatus;
import ra.edu.enums.PaymentStatus;
import ra.edu.exception.ConflictException;
import ra.edu.exception.NotFoundException;
import ra.edu.mapper.InvoiceMapper;
import ra.edu.repository.InvoiceRepository;
import ra.edu.repository.PaymentRepository;
import ra.edu.service.PaymentService;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class PaymentServiceImp implements PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private InvoiceRepository invoiceRepository;

    @Override
    public PaymentResponse createPayment(PaymentRequest request) {
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new NotFoundException("Hóa đơn không tồn tại"));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new ConflictException("Hóa đơn này đã được thanh toán.");
        }

        Payment payment = Payment.builder()
                .invoice(invoice)
                .method(request.getMethod())
                .amount(invoice.getTotalAmount())
                .status(PaymentStatus.PENDING)
                .transactionId(UUID.randomUUID().toString())
                .createdAt(LocalDate.now())
                .build();

        paymentRepository.save(payment);

        return InvoiceMapper.toPaymentResponse(payment);
    }

    @Override
    public PaymentResponse getPaymentById(Integer id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Payment không tồn tại"));

        return InvoiceMapper.toPaymentResponse(payment);
    }
}
