package ra.edu.service;

import ra.edu.dto.request.ChangePasswordRequest;
import ra.edu.dto.request.UserLogin;
import ra.edu.dto.request.UserProfileUpdateRequest;
import ra.edu.dto.request.UserRegister;
import ra.edu.dto.response.JWTResponse;
import ra.edu.dto.response.UserResponse;
import ra.edu.entity.User;

public interface AuthService {
    User register(UserRegister userRegister);
    JWTResponse login(UserLogin userLogin);
    boolean verifyEmail(String email, String code);
    UserResponse getCurrentUser(String username);
    UserResponse updateProfile(String username, UserProfileUpdateRequest request);
    UserResponse changePassword(String username, ChangePasswordRequest request);
}
