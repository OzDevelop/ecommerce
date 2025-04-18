package fastcampus.ecommerce.api.service.product;

import fastcampus.ecommerce.api.domain.product.InsufficientStockException;
import fastcampus.ecommerce.api.domain.product.Product;
import fastcampus.ecommerce.api.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResult findProduct(String productId) {

        return ProductResult.from(findProductById(productId));
    }

    private Product findProductById(String productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    public Page<ProductResult> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(ProductResult::from);
    }

    @Transactional
    public void decreaseStock(String productId, int quantity) {
        Product product = findProductById(productId);
        product.decreaseStock(quantity);
        productRepository.save(product);
    }

    @Transactional
    public void increaseStock(String productId, int quantity) {
        Product product = findProductById(productId);
        product.increaseStock(quantity);
        productRepository.save(product);
    }

}
