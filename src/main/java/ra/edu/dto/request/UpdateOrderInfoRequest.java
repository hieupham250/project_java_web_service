package ra.edu.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderInfoRequest {
    @NotBlank(message = "Địa chỉ nhận hàng không được để trống")
    private String shippingAddress;
    private String internalNotes;
}
