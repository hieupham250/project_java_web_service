package ra.edu.service;

import org.springframework.data.domain.Pageable;
import ra.edu.dto.request.UserAdminUpdateRequest;
import ra.edu.dto.response.PagedData;
import ra.edu.dto.response.UserResponse;

public interface AdminUserService {
    PagedData<UserResponse> getUsers(Pageable pageable);
    UserResponse getUserById(int id);
    void updateUserByAdmin(int id, UserAdminUpdateRequest request);
    void updateStatus(int id, Boolean status);
    void softDelete(int id);
}
