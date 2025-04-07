package fastcampus.ecommerce.api.domain.transaction.report;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transaction_reports")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@IdClass(TransactionReportId.class)
public class TransactionReport {
    //transaction report는 date와 type이 합쳐서 복합키로 작용하기 떄문에 아래와 같이 설정.
    // JPA에서는 복합키를 사용할 경우 별도의 키 클래스를 만들어줘야함.
    // 따라서 TransactionReportId 클래스를 만들어서 @IdClass로 설정해줌.
//    @Id
//    private LocalDate transactionDate;
//    @Id
//    private String transactionType;

    @Id
    private LocalDate transactionDate;
    @Id
    private String transactionType;
    private Long transactionCount;
    private Long totalAmount;
    private Long customerCount;
    private Long orderCount;
    private Long paymentMethodCount;
    private BigDecimal avgProductCount;
    private Long totalItemQuantity;

}
