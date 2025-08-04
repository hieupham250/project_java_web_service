package ra.edu.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ra.edu.dto.request.UserAdminUpdateRequest;
import ra.edu.dto.response.UserResponse;

public interface UserService {
    Page<UserResponse> getUsers(Pageable pageable, String search);
    UserResponse getUserById(Integer id);
    UserResponse updateUserByAdmin(Integer id, UserAdminUpdateRequest request);
    UserResponse updateStatus(Integer id, Boolean status);
    UserResponse softDelete(Integer id);
}
