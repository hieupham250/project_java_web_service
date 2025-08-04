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
import ra.edu.dto.request.ProductRequest;
import ra.edu.dto.response.BaseResponse;
import ra.edu.dto.response.PagedData;
import ra.edu.dto.response.ProductResponse;
import ra.edu.service.ProductService;

import java.time.LocalDateTime;

import static ra.edu.util.ResponseUtil.convertToPagedData;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<BaseResponse<PagedData<ProductResponse>>> getProducts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer categoryId
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<ProductResponse> products = productService.getProducts(pageable, search, categoryId);
        return ResponseEntity.ok(new BaseResponse<>(
                true,
                "Lấy danh sách sản phẩm thành công",
                convertToPagedData(products),
                null,
                LocalDateTime.now()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ProductResponse>> getProductById(@PathVariable Integer id) {
        ProductResponse product = productService.getById(id);
        return ResponseEntity.ok(new BaseResponse<>(
                true,
                "Lấy chi tiết người dùng thành công",
                product,
                null,
                LocalDateTime.now()));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<ProductResponse>> create(@RequestBody @Valid ProductRequest request) {
        return new ResponseEntity<>(
                new BaseResponse<>(
                        true,
                        "Thêm sản phẩm thành công",
                        productService.create(request),
                        null,
                        LocalDateTime.now()
                ),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<ProductResponse>> update(@PathVariable Integer id, @RequestBody @Valid ProductRequest request) {
        return ResponseEntity.ok(
                new BaseResponse<>(
                        true,
                        "Cập nhật sản phẩm thành công",
                        productService.update(id, request),
                        null,
                        LocalDateTime.now()
                )
        );
    }

    @DeleteMapping("{id}")
    public ResponseEntity<BaseResponse<ProductResponse>> delete(@PathVariable Integer id) {
        return ResponseEntity.ok(new BaseResponse<>(
                true,
                "Xóa sản phẩm thành công",
                productService.delete(id),
                null,
                LocalDateTime.now()
        ));
    }
}
