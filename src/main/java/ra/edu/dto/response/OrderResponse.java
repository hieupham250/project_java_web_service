package ra.edu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ra.edu.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Integer id;
    private Integer userId;
    private String fullName;
    private String email;
    private String phone;
    private String shippingAddress;
    private String internalNotes;
    private BigDecimal totalPrice;
    private LocalDate createdAt;
    private OrderStatus status;
    private List<OrderItemResponse> orderItems;
}
