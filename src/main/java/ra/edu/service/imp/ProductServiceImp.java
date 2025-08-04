package ra.edu.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ra.edu.dto.request.ProductRequest;
import ra.edu.dto.response.ProductResponse;
import ra.edu.entity.Category;
import ra.edu.entity.Product;
import ra.edu.exception.ConflictException;
import ra.edu.exception.NotFoundException;
import ra.edu.mapper.ProductMapper;
import ra.edu.repository.CategoryRepository;
import ra.edu.repository.OrderItemRepository;
import ra.edu.repository.ProductRepository;
import ra.edu.service.ProductService;

import java.time.LocalDate;

@Service
public class ProductServiceImp implements ProductService {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    OrderItemRepository orderItemRepository;

    @Override
    public Page<ProductResponse> getProducts(Pageable pageable, String search, Integer categoryId) {
        Page<Product> productsPage;
        if (search != null && !search.trim().isEmpty() || categoryId != null && categoryId > 0) {
            productsPage = productRepository.searchByNameAndCategory(search, categoryId, pageable);
        } else {
            productsPage = productRepository.findByIsDeletedFalse(pageable);
        }
        return productsPage.map(ProductMapper::toResponse);
    }

    @Override
    public ProductResponse getById(Integer id) {
        Product product = productRepository.
                findByIdAndIsDeletedFalse(id).orElseThrow(() -> new NotFoundException("Sản phẩm không tồn tại"));
        return ProductMapper.toResponse(product);
    }

    @Override
    public ProductResponse create(ProductRequest request) {
        if (productRepository.existsByName(request.getName())) {
            throw new ConflictException("Tên sản phẩm đã tồn tại");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                        .orElseThrow(() -> new NotFoundException("Không tìm thấy danh mục"));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .category(category)
                .isDeleted(false)
                .createdAt(LocalDate.now())
                .build();

        productRepository.save(product);

        return ProductMapper.toResponse(product);
    }

    @Override
    public ProductResponse update(Integer id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tồn tại product"));

        boolean isNameChanged = !product.getName().equalsIgnoreCase(request.getName());
        if (isNameChanged && productRepository.existsByName(request.getName())) {
            throw new ConflictException("Tên sản phẩm đã tồn tại");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy danh mục"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(category);
        product.setIsDeleted(false);
        product.setUpdatedAt(LocalDate.now());

        productRepository.save(product);
        return ProductMapper.toResponse(product);
    }

    @Override
    public ProductResponse delete(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tồn tại product"));

        boolean hasOrders = orderItemRepository.existsByProductId(id);
        if (hasOrders) {
            throw new ConflictException("Sản phẩm có đơn hàng, không thể xóa");
        }
        product.setIsDeleted(true);
        product.setDeletedAt(LocalDate.now());
        productRepository.save(product);
        return ProductMapper.toResponse(product);
    }
}
