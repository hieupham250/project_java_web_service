package ra.edu.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ra.edu.dto.request.CategoryRequest;
import ra.edu.dto.response.CategoryResponse;
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
    public Page<CategoryResponse> getCategories(Pageable pageable, String search) {
        Page<Category> categoriesPage;
        if (search == null || search.trim().isEmpty()) {
            categoriesPage = categoryRepository.findByIsDeletedFalse(pageable);
        } else {
            categoriesPage = categoryRepository.searchByName(search.trim(), pageable);
        }

        return categoriesPage.map(CategoryMapper::toResponse);
    }

    @Override
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new ConflictException("Tên danh mục đã tồn tại");
        }
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .createdAt(LocalDate.now())
                .isDeleted(false)
                .build();

        categoryRepository.save(category);

        return CategoryMapper.toResponse(category);
    }

    @Override
    public CategoryResponse update(Integer id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tồn tại category"));

        boolean isNameChanged = !category.getName().equalsIgnoreCase(request.getName());
        if (isNameChanged && categoryRepository.existsByName(request.getName())) {
            throw new ConflictException("Tên danh mục đã tồn tại");
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setUpdatedAt(LocalDate.now());
        categoryRepository.save(category);

        return CategoryMapper.toResponse(category);
    }

    @Override
    public CategoryResponse delete(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tồn tại category"));

        boolean hasActiveProducts = category.getProducts().stream()
                .anyMatch(product -> !Boolean.TRUE.equals(product.getIsDeleted()));

        if (hasActiveProducts) {
            throw new ConflictException("Không thể xóa vì còn sản phẩm trong danh mục này");
        }

        category.setIsDeleted(true);
        category.setDeletedAt(LocalDate.now());
        return CategoryMapper.toResponse(categoryRepository.save(category));
    }
}
