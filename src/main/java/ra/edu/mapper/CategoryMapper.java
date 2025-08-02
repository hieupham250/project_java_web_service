package ra.edu.mapper;

import ra.edu.dto.response.CategoryResponse;
import ra.edu.entity.Category;

public class CategoryMapper {
    public static CategoryResponse toResponse(Category category) {
        if (category == null) return null;
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getCreatedAt(),
                category.getUpdatedAt(),
                category.getDeletedAt()
        );
    }
}
