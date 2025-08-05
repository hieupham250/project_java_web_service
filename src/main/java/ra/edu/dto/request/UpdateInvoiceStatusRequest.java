package ra.edu.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ra.edu.enums.InvoiceStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInvoiceStatusRequest {
    @NotNull(message = "Trạng thái hóa đơn không được để trống")
    private InvoiceStatus status;
}
