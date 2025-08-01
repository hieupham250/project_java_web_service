package ra.edu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Integer id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private String avatar;
    private Boolean status;
    private Boolean isVerify;
    private String roleName;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
