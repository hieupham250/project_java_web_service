package ra.edu.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ra.edu.dto.request.CategoryRequest;
import ra.edu.dto.response.CategoryResponse;
import ra.edu.entity.Category;

public interface CategoryService {
    Page<Category> findAll(Pageable pageable);
    Page<CategoryResponse> getCategories(Pageable pageable, String search);
    CategoryResponse create(CategoryRequest request);
    CategoryResponse update(Integer id, CategoryRequest request);
    CategoryResponse delete(Integer id);
}
