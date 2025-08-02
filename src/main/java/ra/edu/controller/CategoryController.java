package ra.edu.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ra.edu.dto.request.CategoryRequest;
import ra.edu.dto.response.BaseResponse;
import ra.edu.dto.response.CategoryResponse;
import ra.edu.dto.response.PagedData;
import ra.edu.entity.Category;
import ra.edu.service.CategoryService;

import java.time.LocalDateTime;

import static ra.edu.util.ResponseUtil.convertToPagedData;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<BaseResponse<PagedData<CategoryResponse>>> getCategories(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<CategoryResponse> categories = categoryService.getCategories(pageable, search);
        return ResponseEntity.ok(new BaseResponse<>(
                true,
                "Lấy danh sách danh mục thành công",
                convertToPagedData(categories),
                null,
                LocalDateTime.now()
        ));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<CategoryResponse>> create(@RequestBody @Valid CategoryRequest request) {
        return new ResponseEntity<>(
                new BaseResponse<>(
                        true,
                        "Thêm danh mục thành công",
                        categoryService.create(request),
                        null,
                        LocalDateTime.now()
                ),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<CategoryResponse>> update(@PathVariable int id,
                                                         @RequestBody @Valid CategoryRequest request) {
        return ResponseEntity.ok(new BaseResponse<>(
                true,
                "Cập nhật danh mục thành công",
                categoryService.update(id, request),
                null,
                LocalDateTime.now()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<CategoryResponse>> delete(@PathVariable int id) {
        categoryService.delete(id);
        return ResponseEntity.ok(new BaseResponse<>(
                true,
                "Xóa danh mục thành công",
                categoryService.delete(id),
                null,
                LocalDateTime.now()
        ));
    }
}
