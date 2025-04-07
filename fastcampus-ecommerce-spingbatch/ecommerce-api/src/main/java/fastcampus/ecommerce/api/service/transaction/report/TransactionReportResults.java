package fastcampus.ecommerce.api.service.transaction.report;

import fastcampus.ecommerce.api.domain.transaction.report.TransactionReport;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TransactionReportResults {

    private List<TransactionReportResult> results;

    public static TransactionReportResults from(List<TransactionReport> reports) {
        return new TransactionReportResults(reports.stream()
                .map(TransactionReportResult::from)
                .collect(Collectors.toList()));
    }

}
