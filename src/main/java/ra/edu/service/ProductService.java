package ra.edu.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ra.edu.dto.request.ProductRequest;
import ra.edu.dto.response.ProductResponse;

public interface ProductService {
    Page<ProductResponse> getProducts(Pageable pageable, String search, Integer categoryId);
    ProductResponse getById(Integer id);
    ProductResponse create(ProductRequest request);
    ProductResponse update(Integer id, ProductRequest request);
    ProductResponse delete(Integer id);
}
