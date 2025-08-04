package ra.edu.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ra.edu.dto.request.UpdateUserStatusRequest;
import ra.edu.dto.request.UserAdminUpdateRequest;
import ra.edu.dto.response.BaseResponse;
import ra.edu.dto.response.PagedData;
import ra.edu.dto.response.UserResponse;
import ra.edu.service.UserService;

import java.time.LocalDateTime;

import static ra.edu.util.ResponseUtil.convertToPagedData;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<BaseResponse<PagedData<UserResponse>>> getUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String search
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<UserResponse> users = userService.getUsers(pageable, search);
        return ResponseEntity.ok(new BaseResponse<>(
                true,
                "Lấy danh sách người dùng thành công",
                convertToPagedData(users),
                null,
                LocalDateTime.now()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<UserResponse>> getUserById(@PathVariable Integer id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(new BaseResponse<>(
                true,
                "Lấy chi tiết người dùng thành công",
                user, null,
                LocalDateTime.now()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<UserResponse>> update(
            @PathVariable int id,
            @RequestBody @Valid UserAdminUpdateRequest request
    ) {
        UserResponse updatedUser = userService.updateUserByAdmin(id, request);
        return ResponseEntity.ok(new BaseResponse<>(
                true,
                "Cập nhật người dùng thành công",
                updatedUser,
                null,
                LocalDateTime.now()));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<BaseResponse<UserResponse>> updateUserStatus(
            @PathVariable int id,
            @RequestBody @Valid UpdateUserStatusRequest request
    ) {
        UserResponse updatedUser = userService.updateStatus(id, request.getStatus());
        return ResponseEntity.ok(new BaseResponse<>(
                true,
                "Cập nhật trạng thái thành công",
                updatedUser,
                null,
                LocalDateTime.now()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<UserResponse>> softDeleteUser(@PathVariable Integer id) {
        UserResponse deletedUser = userService.softDelete(id);
        return ResponseEntity.ok(new BaseResponse<>(
                true,
                "Xóa người dùng thành công",
                deletedUser,
                null,
                LocalDateTime.now()));
    }
}
