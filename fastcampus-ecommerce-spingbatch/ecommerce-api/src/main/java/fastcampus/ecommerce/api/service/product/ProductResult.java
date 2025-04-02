package fastcampus.ecommerce.api.service.product;

import fastcampus.ecommerce.api.domain.product.InsufficientStockException;
import fastcampus.ecommerce.api.domain.product.InvalidStockQuantityException;
import fastcampus.ecommerce.api.domain.product.Product;
import fastcampus.ecommerce.api.domain.product.ProductStatus;
import fastcampus.ecommerce.api.domain.product.StockQuantityArithmeticException;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductResult {

    private String productId;
    private Long sellerId;

    private String category;
    private String productName;
    private LocalDate salesStartDate;
    private LocalDate salesEndDate;

    private ProductStatus productStatus;
    private String brand;
    private String manufacturer;

    private int salesPrice;
    private int stockQuantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProductResult of(String productId, Long sellerId, String category, String productName,
                                   LocalDate salesStartDate,
                                   LocalDate salesEndDate, ProductStatus productStatus, String brand,
                                   String manufacturer, int salesPrice,
                                   int stockQuantity,
                                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new ProductResult(productId, sellerId, category, productName, salesStartDate, salesEndDate,
                productStatus, brand, manufacturer,
                salesPrice, stockQuantity,
                createdAt,
                updatedAt);
    }

    public static ProductResult from(Product product) {
        return new ProductResult(
                product.getProductId(),
                product.getSellerId(),
                product.getCategory(),
                product.getProductName(),
                product.getSalesStartDate(),
                product.getSalesEndDate(),
                product.getProductStatus(),
                product.getBrand(),
                product.getManufacturer(),
                product.getSalesPrice(),
                product.getStockQuantity(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

}

