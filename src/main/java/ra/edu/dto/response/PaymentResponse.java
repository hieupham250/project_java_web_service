package ra.edu.dto.response;

import lombok.*;
import ra.edu.enums.PaymentMethod;
import ra.edu.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
    private Integer id;
    private PaymentMethod method;
    private BigDecimal amount;
    private PaymentStatus status;
    private String transactionId;
    private LocalDate createdAt;
}
