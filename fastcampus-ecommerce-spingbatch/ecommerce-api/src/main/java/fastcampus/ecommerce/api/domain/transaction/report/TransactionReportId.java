package fastcampus.ecommerce.api.domain.transaction.report;

import jakarta.persistence.Id;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionReportId implements Serializable {

    private LocalDate transactionDate;
    private String transactionType;
}
