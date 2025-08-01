package ra.edu.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ra.edu.dto.request.UserAdminUpdateRequest;
import ra.edu.dto.response.PagedData;
import ra.edu.dto.response.UserResponse;
import ra.edu.entity.User;
import ra.edu.exception.ConflictException;
import ra.edu.exception.NotFoundException;
import ra.edu.mapper.UserMapper;
import ra.edu.repository.UserRepository;
import ra.edu.service.AdminUserService;

import java.time.LocalDate;
import java.util.List;

@Service
public class AdminUserServiceImp implements AdminUserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public PagedData<UserResponse> getUsers(Pageable pageable) {
        Page<User> page = userRepository.findAllExcludeAdmin(pageable);
        List<UserResponse> users = page.getContent()
                .stream()
                .map(UserMapper::toResponse)
                .toList();

        PagedData.Pagination pagination = new PagedData.Pagination(
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements()
        );

        return new PagedData<>(users, pagination);
    }

    @Override
    public UserResponse getUserById(int id) {
        User user = userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));
        return UserMapper.toResponse(user);
    }

    @Override
    public void updateUserByAdmin(int id, UserAdminUpdateRequest request) {
        User user = userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));

        boolean isEmailChanged = !user.getEmail().equalsIgnoreCase(request.getEmail());
        if (isEmailChanged && userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ConflictException("Email đã được sử dụng bởi người dùng khác");
        }

        boolean isPhoneChanged = !user.getPhone().equalsIgnoreCase(request.getPhone());
        if (isPhoneChanged && userRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new ConflictException("Số điện thoại đã được sử dụng bởi người dùng khác");
        }

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setStatus(request.getStatus());
        user.setAvatar(request.getAvatar());
        user.setUpdatedAt(LocalDate.now());

        userRepository.save(user);
    }

    @Override
    public void updateStatus(int id, Boolean status) {
        User user = userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));
        user.setStatus(status);
        user.setUpdatedAt(LocalDate.now());
        userRepository.save(user);
    }

    @Override
    public void softDelete(int id) {
        User user = userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));
        user.setIsDeleted(true);
        user.setDeletedAt(LocalDate.now());
        user.setUpdatedAt(LocalDate.now());
        userRepository.save(user);
    }
}
