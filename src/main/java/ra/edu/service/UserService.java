package ra.edu.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ra.edu.dto.request.UserAdminUpdateRequest;
import ra.edu.dto.response.UserResponse;

public interface UserService {
    Page<UserResponse> getUsers(Pageable pageable, String search);
    UserResponse getUserById(int id);
    UserResponse updateUserByAdmin(int id, UserAdminUpdateRequest request);
    UserResponse updateStatus(int id, Boolean status);
    UserResponse softDelete(int id);
}
