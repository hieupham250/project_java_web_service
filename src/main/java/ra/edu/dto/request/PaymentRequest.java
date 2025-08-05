package ra.edu.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ra.edu.enums.PaymentMethod;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    @NotNull(message = "Invoice ID không được để trống")
    private Integer invoiceId;

    @NotNull(message = "Phương thức thanh toán không được để trống")
    private PaymentMethod method;
}
