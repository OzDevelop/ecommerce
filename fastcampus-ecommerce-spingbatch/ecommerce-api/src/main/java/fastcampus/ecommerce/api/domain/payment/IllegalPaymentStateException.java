package fastcampus.ecommerce.api.domain.payment;

public class IllegalPaymentStateException extends RuntimeException {
    public IllegalPaymentStateException(String message) {
        super(message);
    }
}
