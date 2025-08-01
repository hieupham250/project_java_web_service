package ra.edu.mapper;

import ra.edu.dto.request.CategoryRequest;
import ra.edu.entity.Category;

import java.time.LocalDate;

public class CategoryMapper {
    public static Category toEntity(CategoryRequest request) {
        return Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .createdAt(LocalDate.now())
                .isDeleted(false)
                .build();
    }

    public static void updateEntity(Category category, CategoryRequest request) {
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setUpdatedAt(LocalDate.now());
    }
}
