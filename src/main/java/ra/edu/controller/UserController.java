package ra.edu.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ra.edu.dto.request.ChangePasswordRequest;
import ra.edu.dto.request.UserProfileUpdateRequest;
import ra.edu.dto.response.BaseResponse;
import ra.edu.dto.response.UserResponse;
import ra.edu.service.UserService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<BaseResponse<UserResponse>> getProfile(Authentication authentication) {
        String username = authentication.getName();
        UserResponse user = userService.getCurrentUser(username);
        return ResponseEntity.ok(new BaseResponse<>(
                true,
                "Lấy thông tin người dùng thành công",
                user,
                null,
                LocalDateTime.now()));
    }

    @PutMapping("/profile")
    public ResponseEntity<BaseResponse<String>> updateProfile(@RequestBody @Valid UserProfileUpdateRequest request,
                                                              Authentication authentication) {
        String username = authentication.getName();
        userService.updateProfile(username, request);
        return ResponseEntity.ok(new BaseResponse<>(
                true,
                "Cập nhật thông tin người dùng thành công",
                null,
                null,
                LocalDateTime.now()));
    }

    @PutMapping("/profile/change-password")
    public ResponseEntity<BaseResponse<String>> changePassword(@RequestBody @Valid ChangePasswordRequest request,
                                                               Authentication authentication) {
        String username = authentication.getName();
        userService.changePassword(username, request);
        return ResponseEntity.ok(new BaseResponse<>(
                true,
                "Thay đổi mật khẩu thành công",
                null,
                null,
                LocalDateTime.now()));
    }
}
