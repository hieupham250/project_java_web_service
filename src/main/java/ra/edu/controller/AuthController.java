package ra.edu.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ra.edu.dto.request.UserLogin;
import ra.edu.dto.request.UserRegister;
import ra.edu.dto.request.VerifyRequest;
import ra.edu.dto.response.BaseResponse;
import ra.edu.dto.response.JWTResponse;
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
        return new ResponseEntity<>(
                new BaseResponse<>(
                        true,
                        "Đăng ký tài khoản thành công",
                        authService.register(userRegister),
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
}
