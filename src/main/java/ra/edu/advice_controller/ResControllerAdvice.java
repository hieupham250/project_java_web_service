package ra.edu.advice_controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.apache.coyote.BadRequestException;

import ra.edu.dto.response.BaseResponse;
import ra.edu.exception.ConflictException;
import ra.edu.exception.NotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ResControllerAdvice {
    // Xử lý lỗi @Valid trong @RequestBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<?>> handleValidationException(MethodArgumentNotValidException ex) {
        List<BaseResponse.FieldError> errorList = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> new BaseResponse.FieldError(err.getField(), err.getDefaultMessage()))
                .collect(Collectors.toList());

        return buildErrorResponse("Dữ liệu không hợp lệ", errorList, HttpStatus.BAD_REQUEST);
    }

    // Xử lý lỗi @Validated trong @PathVariable, @RequestParam
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BaseResponse<?>> handleConstraintViolation(ConstraintViolationException ex) {
        List<BaseResponse.FieldError> errorList = ex.getConstraintViolations().stream()
                .map(err -> new BaseResponse.FieldError(
                        err.getPropertyPath().toString(),
                        err.getMessage()))
                .collect(Collectors.toList());

        return buildErrorResponse("Dữ liệu không hợp lệ", errorList, HttpStatus.BAD_REQUEST);
    }

    // Lỗi do tham số không hợp lệ
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse<?>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildErrorResponse("Tham số không hợp lệ", buildSingleError("error", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    // Lỗi định dạng thời gian
    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<BaseResponse<?>> handleDateTimeParse(DateTimeParseException ex) {
        return buildErrorResponse("Sai định dạng thời gian", buildSingleError("date", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    // Không tìm thấy resource (thường là URL sai)
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<BaseResponse<?>> handleNoResourceFound(NoResourceFoundException ex) {
        return buildErrorResponse("Không tìm thấy tài nguyên", buildSingleError("path", ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    // Lỗi BadRequestException của Apache
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<BaseResponse<?>> handleBadRequest(BadRequestException ex) {
        return buildErrorResponse("Yêu cầu không hợp lệ", buildSingleError("error", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    // Lỗi not found từ JPA (ChangeSetPersister)
    @ExceptionHandler(ChangeSetPersister.NotFoundException.class)
    public ResponseEntity<BaseResponse<?>> handleNotFound(ChangeSetPersister.NotFoundException ex) {
        return buildErrorResponse("Không tìm thấy", buildSingleError("error", ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    // Bắt tất cả lỗi chưa xác định
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<?>> handleAllUnhandled(Exception ex) {
        return buildErrorResponse("Lỗi không xác định", buildSingleError("error", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<BaseResponse<?>> handleNotFoundCustom(NotFoundException ex) {
        return buildErrorResponse("Không tìm thấy tài nguyên", buildSingleError("error", ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<BaseResponse<?>> handleConflict(ConflictException ex) {
        return buildErrorResponse("Xung đột dữ liệu", buildSingleError("error", ex.getMessage()), HttpStatus.CONFLICT);
    }

    // Helper tạo lỗi đơn (1 field + message)
    private List<BaseResponse.FieldError> buildSingleError(String field, String message) {
        return List.of(new BaseResponse.FieldError(field, message));
    }

    // Tạo response lỗi
    private ResponseEntity<BaseResponse<?>> buildErrorResponse(String message, List<BaseResponse.FieldError> errors, HttpStatus status) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        BaseResponse<?> response = new BaseResponse<>(
                false,
                message,
                null,
                errors,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(response, status);
    }
}
