package ra.edu.service;

import ra.edu.dto.request.UserLogin;
import ra.edu.dto.request.UserRegister;
import ra.edu.dto.response.JWTResponse;
import ra.edu.entity.User;

public interface AuthService {
    User register(UserRegister userRegister);
    JWTResponse login(UserLogin userLogin);
    boolean verifyEmail(String email, String code);
}
