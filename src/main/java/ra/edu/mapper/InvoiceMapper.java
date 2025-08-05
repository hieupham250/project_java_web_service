package ra.edu.mapper;

import ra.edu.dto.response.InvoiceResponse;
import ra.edu.dto.response.PaymentResponse;
import ra.edu.entity.Invoice;
import ra.edu.entity.Payment;

import java.util.stream.Collectors;

public class InvoiceMapper {
    public static InvoiceResponse toResponse(Invoice invoice) {
        return new InvoiceResponse(
                invoice.getId(),
                invoice.getOrder().getId(),
                invoice.getTotalAmount(),
                invoice.getStatus(),
                invoice.getCreatedAt(),
                invoice.getPayments() == null ? null :
                        invoice.getPayments().stream().map(InvoiceMapper::toPaymentResponse).collect(Collectors.toList())
        );
    }

    public static PaymentResponse toPaymentResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getMethod(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getTransactionId(),
                payment.getCreatedAt()
        );
    }
}
