package ra.edu.dto.response;

import lombok.*;
import ra.edu.enums.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceResponse {
    private Integer id;
    private Integer orderId;
    private BigDecimal totalAmount;
    private InvoiceStatus status;
    private LocalDate createdAt;
    private List<PaymentResponse> payments;
}
