package ra.edu.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ra.edu.dto.request.CategoryRequest;
import ra.edu.entity.Category;
import ra.edu.exception.ConflictException;
import ra.edu.exception.NotFoundException;
import ra.edu.mapper.CategoryMapper;
import ra.edu.repository.CategoryRepository;
import ra.edu.service.CategoryService;

import java.time.LocalDate;

@Service
public class CategoryServiceImp implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Page<Category> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Override
    public Page<Category> getCategories(Pageable pageable) {
        return categoryRepository.findByIsDeletedFalse(pageable);
    }

    @Override
    public Category create(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new ConflictException("Tên danh mục đã tồn tại");
        }
        Category category = CategoryMapper.toEntity(request);
        return categoryRepository.save(category);
    }

    @Override
    public Category update(int id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tồn tại category"));

        boolean isNameChanged = !category.getName().equalsIgnoreCase(request.getName());
        if (isNameChanged && categoryRepository.existsByName(request.getName())) {
            throw new ConflictException("Tên danh mục đã tồn tại");
        }

        CategoryMapper.updateEntity(category, request);
        return categoryRepository.save(category);
    }

    @Override
    public void delete(int id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tồn tại category"));

        boolean hasActiveProducts = category.getProducts().stream()
                .anyMatch(product -> !Boolean.TRUE.equals(product.getIsDeleted()));

        if (hasActiveProducts) {
            throw new ConflictException("Không thể xóa vì còn sản phẩm trong danh mục này");
        }

        category.setIsDeleted(true);
        category.setDeletedAt(LocalDate.now());
        categoryRepository.save(category);

    }
}
