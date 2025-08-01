package ra.edu.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ra.edu.dto.request.UserAdminUpdateRequest;
import ra.edu.dto.response.BaseResponse;
import ra.edu.dto.response.PagedData;
import ra.edu.dto.response.UserResponse;
import ra.edu.service.AdminUserService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AdminUserController {
    @Autowired
    private AdminUserService adminUserService;

    @GetMapping
    public ResponseEntity<BaseResponse<PagedData<UserResponse>>> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);
        PagedData<UserResponse> users = adminUserService.getUsers(pageable);
        return ResponseEntity.ok(new BaseResponse<>(
                true,
                "Lấy danh sách người dùng thành công",
                users, null,
                LocalDateTime.now()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<UserResponse>> getUserById(@PathVariable int id) {
        UserResponse user = adminUserService.getUserById(id);
        return ResponseEntity.ok(new BaseResponse<>(
                true,
                "Lấy chi tiết người dùng thành công",
                user, null,
                LocalDateTime.now()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<String>> update(
            @PathVariable int id,
            @RequestBody @Valid UserAdminUpdateRequest request
    ) {
        adminUserService.updateUserByAdmin(id, request);
        return ResponseEntity.ok(new BaseResponse<>(
                true,
                "Cập nhật người dùng thành công",
                null,
                null,
                LocalDateTime.now()));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<BaseResponse<String>> updateUserStatus(
            @PathVariable int id,
            @RequestParam Boolean status
    ) {
        adminUserService.updateStatus(id, status);
        return ResponseEntity.ok(new BaseResponse<>(
                true,
                "Cập nhật trạng thái thành công",
                null,
                null,
                LocalDateTime.now()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<String>> softDeleteUser(@PathVariable int id) {
        adminUserService.softDelete(id);
        return ResponseEntity.ok(new BaseResponse<>(
                true,
                "Xóa người dùng thành công",
                null,
                null,
                LocalDateTime.now()));
    }
}
