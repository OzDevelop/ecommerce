package fastcampus.ecommerce.batch.domain.transaction.report;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionReportMapRepository {

    private final ConcurrentMap<String, TransactionReport> reportMap = new ConcurrentHashMap<>();

    public void put(TransactionReport report) {
        String key = getKey(report);
        reportMap.compute(key, (k, r) -> {
            if (r == null) {
                return report;
            }
            r.add(report);
            return r;
        });

    }

    private static String getKey(TransactionReport report) {
        return report.getTransactionDate() + "|" + report.getTransactionType();
    }
}
