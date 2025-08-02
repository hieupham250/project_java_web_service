package ra.edu.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ra.edu.dto.request.*;
import ra.edu.dto.response.BaseResponse;
import ra.edu.dto.response.JWTResponse;
import ra.edu.dto.response.UserResponse;
import ra.edu.entity.User;
import ra.edu.service.AuthService;
import ra.edu.service.EmailService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<User>> register(@RequestBody @Valid UserRegister userRegister){
        authService.register(userRegister);
        return new ResponseEntity<>(
                new BaseResponse<>(
                        true,
                        "Đăng ký thành công. Vui lòng kiểm tra email để xác minh tài khoản trước khi đăng nhập.",
                        null,
                        null,
                        LocalDateTime.now()
                ),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<JWTResponse>> login(@RequestBody UserLogin userLogin){
        return new ResponseEntity<>(
                new BaseResponse<>(
                        true,
                        "Đăng nhập thành công",
                        authService.login(userLogin),
                        null,
                        LocalDateTime.now()
                ),
                HttpStatus.OK
        );
    }

    @PostMapping("/verify")
    public ResponseEntity<BaseResponse<String>> verifyEmail(@RequestBody @Valid VerifyRequest request) {
        boolean verified = authService.verifyEmail(request.getEmail(), request.getCode());
        if (verified) {
            return ResponseEntity.ok(
                    new BaseResponse<>(
                            true,
                            "Xác minh email thành công!",
                            null,
                            null,
                            LocalDateTime.now()
                    )
            );
        } else {
            throw new IllegalArgumentException("Mã xác minh không hợp lệ hoặc đã hết hạn.");
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<BaseResponse<UserResponse>> getProfile(Authentication authentication) {
        String username = authentication.getName();
        UserResponse user = authService.getCurrentUser(username);
        return ResponseEntity.ok(new BaseResponse<>(
                true,
                "Lấy thông tin người dùng thành công",
                user,
                null,
                LocalDateTime.now()));
    }

    @PutMapping("/profile")
    public ResponseEntity<BaseResponse<UserResponse>> updateProfile(
            @RequestBody @Valid UserProfileUpdateRequest request,
            Authentication authentication
    ) {
        String username = authentication.getName();
        UserResponse updatedUser = authService.updateProfile(username, request);
        return ResponseEntity.ok(new BaseResponse<>(
                true,
                "Cập nhật thông tin người dùng thành công",
                updatedUser,
                null,
                LocalDateTime.now()));
    }

    @PutMapping("/change-password")
    public ResponseEntity<BaseResponse<UserResponse>> changePassword(
            @RequestBody @Valid ChangePasswordRequest request,
            Authentication authentication
    ) {
        String username = authentication.getName();
        UserResponse updatedUser = authService.changePassword(username, request);
        return ResponseEntity.ok(new BaseResponse<>(
                true,
                "Thay đổi mật khẩu thành công",
                updatedUser,
                null,
                LocalDateTime.now()));
    }
}
