package ra.edu.service;

import ra.edu.dto.request.ChangePasswordRequest;
import ra.edu.dto.request.UserProfileUpdateRequest;
import ra.edu.dto.response.UserResponse;

public interface UserService {
    UserResponse getCurrentUser(String username);
    void updateProfile(String username, UserProfileUpdateRequest request);
    void changePassword(String username, ChangePasswordRequest request);
}
