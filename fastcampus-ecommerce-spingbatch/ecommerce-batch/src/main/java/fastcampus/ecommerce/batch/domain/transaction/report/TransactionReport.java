package fastcampus.ecommerce.batch.domain.transaction.report;


import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class TransactionReport {
    private LocalDate transactionDate;
    private String transactionType;

    private Long transactionCount;
    private Long totalAmount;
    private Long customerCount;
    private Long orderCount;
    private Long paymentMethodCount;
    private BigDecimal avgProductCount;
    private Long totalItemQuantity;
}
