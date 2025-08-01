package ra.edu.mapper;

import ra.edu.dto.response.UserResponse;
import ra.edu.entity.User;

public class UserMapper {
    public static UserResponse toResponse(User user) {
        if (user == null) return null;

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getAddress(),
                user.getAvatar(),
                user.getStatus(),
                user.getIsVerify(),
                user.getRole() != null ? user.getRole().getName().name() : null,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
