package fastcampus.ecommerce.api.domain.payment;

import static fastcampus.ecommerce.api.domain.payment.Payment.createPayment;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import fastcampus.ecommerce.api.domain.order.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PaymentTest {

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = Payment.createPayment(PaymentMethod.CREDIT_CARD, 1000, null);
    }

    @Test
    @DisplayName("결제 생성 시 초기 상태가 올바르게 설정되어야 함")
    void testPaymentPending() {

        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
    }

    @Test
    void testPaymentComplete() {
        payment.compelete();

        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }

    @Test
    void testPaymentCompleteException() {
        payment.compelete();

        assertThatThrownBy(() -> payment.compelete())
                .isInstanceOf(IllegalPaymentStateException.class);
    }

    @Test
    void testPaymentFail() {
        payment.fail();

        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.FAILED);

    }

    @Test
    void testPaymentFailException() {
        payment.compelete();

        assertThatThrownBy(payment::fail)
                .isInstanceOf(IllegalPaymentStateException.class);
    }

    @Test
    void testPaymentCancelAfterComplete() {
        payment.compelete();
        payment.cancel();

        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.REFUNDED);
    }

    @Test
    void testPaymentCancelAfterPending() {
        payment.cancel();

        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.CANCELLED);
    }

    @Test
    void testPaymentCancelAfterFail() {
        payment.fail();
        payment.cancel();

        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.CANCELLED);
    }

    @Test
    void testPaymentCancelAfterRefund() {
        payment.compelete();
        payment.cancel();

        assertThatThrownBy(payment::cancel)
                .isInstanceOf(IllegalPaymentStateException.class);
    }

    @Test
    void testPaymentCancelAfterCancel() {
        payment.fail();
        payment.cancel();

        assertThatThrownBy(payment::cancel)
                .isInstanceOf(IllegalPaymentStateException.class);
    }

}