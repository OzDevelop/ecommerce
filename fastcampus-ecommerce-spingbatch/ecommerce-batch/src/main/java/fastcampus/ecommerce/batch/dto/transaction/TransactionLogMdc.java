package fastcampus.ecommerce.batch.dto.transaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TransactionLogMdc {

    private String transactionType;
    private String transactionStatus;
    private String orderId;
    private String customerId;
    private String totalAmount;
    private String paymentMethod;
    private String productCount;
    private String totalItemQuantity;

    public String getTotalAmount() {
        if (totalAmount.equals("N/A")) {
            return "0";
        }
        return totalAmount;
    }
}
