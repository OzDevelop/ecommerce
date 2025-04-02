package fastcampus.ecommerce.api.domain.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ProductTest {
    private Product product;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        product = Product.of(
                "PROD001", 1L, "Electronics", "Test Product",
                LocalDate.now(), LocalDate.now().plusMonths(1), ProductStatus.AVAILABLE,
                "TestBrand", "TestManufacturer",
                1000, 100,
                now, now
        );
    }

    @Test
    @DisplayName("재고 증가가 올바르게 동작해야 함")
    void testIncreaseStock() {
        product.increaseStock(50);

        assertThat(product.getStockQuantity()).isEqualTo(150);
    }


//    @ValueSource(strings = {Integer.MIN_VALUE, Integer.MAX_VALUE})
//    @ParameterizedTest
    @DisplayName("재고가 음수가 된다면 에러를 반환.")
    @Test
    void testIncreaseStockNegativeResult() {
        assertThatThrownBy(() -> product.increaseStock(Integer.MAX_VALUE))
                .isInstanceOf(StockQuantityArithmeticException.class);
    }

    @DisplayName("0 이하의 숫자가 들어오면 에러를 반환.")
    @ParameterizedTest
    @ValueSource(ints = { -10, -1, 0})
    void testIncreaseStockPositiveParameter(int notPositiveQuantity) {
        assertThatThrownBy(() -> product.increaseStock(notPositiveQuantity))
                .isInstanceOf(InvalidStockQuantityException.class);
    }


    @Test
    void testDecreaseStock() {
        product.decreaseStock(50);

        assertThat(product.getStockQuantity()).isEqualTo(50);
    }

    @ParameterizedTest
    @ValueSource(ints = { -10, -1, 0})
    void testDecreaseStockPositiveParameter(int notPositiveQuantity) {
        assertThatThrownBy(() -> product.decreaseStock(notPositiveQuantity))
                .isInstanceOf(InvalidStockQuantityException.class);
    }

    @Test
    void testDecreaseStockWithInsufficientStock() {
        assertThatThrownBy(() -> product.decreaseStock(101))
                .isInstanceOf(InsufficientStockException.class);
    }
}