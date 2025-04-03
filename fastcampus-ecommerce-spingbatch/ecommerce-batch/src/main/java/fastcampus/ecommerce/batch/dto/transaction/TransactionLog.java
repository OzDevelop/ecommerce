package fastcampus.ecommerce.batch.dto.transaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  Json 형태의 transaction.log를 읽기위한 DTO
 *  transaction.log 형태
 *  {
 *   "timestamp": "2025-04-03 11:19:43.623",
 *   "level": "INFO",
 *   "thread": "http-nio-8080-exec-1",
 *   "mdc": {
 *     "transactionType": "ORDER_CREATION",
 *     "totalAmount": "662105",
 *     "orderId": "5",
 *     "transactionStatus": "SUCCESS",
 *     "customerId": "5",
 *     "paymentMethod": "CREDIT_CARD",
 *     "productCount": "1",
 *     "totalItemQuantity": "5"
 *   },
 *   "logger": "fastcampus.ecommerce.api.service.transaction.TransactionService",
 *   "message": "주문이 성공적으로 생성되었습니다. 결제를 완료해주세요.",
 *   "context": "default"
 * }
 */

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TransactionLog {
    private String timestamp;
    private String level;
    private String logger;
    private String thread;
    private String message;
    private TransactionLogMdc mdc;

    public String getTransactionType() {
        return mdc.getTransactionType();
    }

    public String getTransactionStatus() {
        return mdc.getTransactionStatus();
    }

    public String getOrderId() {
        return mdc.getOrderId();
    }

    public String getCustomerId() {
        return mdc.getCustomerId();
    }

    public String getTotalAmount() {
     return mdc.getTotalAmount();
    }

    public String getPaymentMethod() {
        return mdc.getPaymentMethod();
    }

    public String getProductCount() {
        return mdc.getProductCount();
    }

    public String getTotalItemQuantity() {
        return mdc.getTotalItemQuantity();
    }
}
