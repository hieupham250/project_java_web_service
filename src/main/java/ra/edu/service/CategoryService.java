package ra.edu.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ra.edu.dto.request.CategoryRequest;
import ra.edu.entity.Category;

public interface CategoryService {
    Page<Category> findAll(Pageable pageable);
    Page<Category> getCategories(Pageable pageable);
    Category create(CategoryRequest request);
    Category update(int id, CategoryRequest request);
    void delete(int id);
}
