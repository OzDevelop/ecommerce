package fastcampus.ecommerce.api.service.transaction;

import fastcampus.ecommerce.api.domain.transaction.TransactionStatus;
import fastcampus.ecommerce.api.domain.transaction.TransactionType;
import fastcampus.ecommerce.api.service.order.OrderResult;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

/**
 * 로깅의 호출 시점은 orderService에서 @Transactional이 붙은 로직들이 처리될 때 로깅 메소드가 호출하려 했으나,
 * 하나의 order 서비스가 마무리되지 않으면 트랜잭션이 성공적으로 실행되었는지 알 수 없고, 의존성을 추가시키기 때문에,
 * AOP를 적용하여 분리하려고 함.
 */

@Service
public class TransactionService {

    protected static Logger logger = LoggerFactory.getLogger(TransactionService.class.getName());

    public void logTransaction(TransactionType transactionType,
                               TransactionStatus transactionStatus,
                               String message,
                               OrderResult order
    ) {
        try {
            // 도메인에 특화된 커스텀한 데이터들을 로깅에 넣을 때는 mdc 라는 걸 많이 씀.
            putMdc(transactionType, transactionStatus, order);
            log(transactionStatus, message);
        } finally {
            MDC.clear();
        }

    }

    private void putMdc(TransactionType transactionType, TransactionStatus transactionStatus, OrderResult order) {
        Optional.ofNullable(order)
                        .ifPresentOrElse(this::putOrder, this::putNAOrder);
        putTransaction(transactionType, transactionStatus);
    }

    private void putNAOrder() {
        MDC.put("orderId", "N/A");
        MDC.put("customerId", "N/A");
        MDC.put("totalAmount", "N/A");
        MDC.put("paymentMethod", "N/A");
        MDC.put("productCount", "N/A");
        MDC.put("totalItemQuantity", "N/A");
    }

    private void putOrder(OrderResult order) {
        MDC.put("orderId", order.getOrderId().toString());
        MDC.put("customerId", order.getCustomerId().toString());
        MDC.put("totalAmount", String.valueOf(order.getTotalAmount()));
        MDC.put("paymentMethod", order.getPaymentMethod().toString());
        MDC.put("productCount", order.getProductCount().toString());
        MDC.put("totalItemQuantity", order.getTotalItemQuantity().toString());
    }

    private void putTransaction(TransactionType transactionType,
                                TransactionStatus transactionStatus) {
        MDC.put("transactionType", transactionType.name());
        MDC.put("transactionStatus", transactionStatus.name());
    }


    private void log(TransactionStatus transactionStatus, String message){
        if (transactionStatus == TransactionStatus.SUCCESS) {
            logger.info(message);
        } else {
            logger.error(message);
        }
    }


}
