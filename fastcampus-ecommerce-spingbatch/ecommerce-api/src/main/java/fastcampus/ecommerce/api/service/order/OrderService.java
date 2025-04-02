package fastcampus.ecommerce.api.service.order;

import fastcampus.ecommerce.api.domain.order.Order;
import fastcampus.ecommerce.api.domain.order.OrderItem;
import fastcampus.ecommerce.api.domain.order.OrderRepository;
import fastcampus.ecommerce.api.domain.payment.PaymentMethod;
import fastcampus.ecommerce.api.service.product.ProductResult;
import fastcampus.ecommerce.api.service.product.ProductService;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;

    @Transactional
    public OrderResult order(Long customerId, List<OrderItemCommand> orderItems, PaymentMethod paymentMethod) {
        Order order = Order.createOrder(customerId);
        for(OrderItemCommand orderItem : orderItems) {
            ProductResult product = productService.findProduct(orderItem.getProductId());

            order.addOrderItem(product.getProductId(), orderItem.getQuantity(), product.getSalesPrice());
        }
        order.initPayment(paymentMethod);

        return save(order);
    }

    @Transactional
    public OrderResult completePayment(Long orderId, boolean success) {
        Order order = findById(orderId);
        order.completePayment(success);

        decreaseStock(success, order);
        return save(order);
    }

    @Transactional
    public OrderResult completeOrder(Long orderId) {
        Order order = findById(orderId);
        order.complete();
        return save(order);
    }

    @Transactional
    public OrderResult cancelOrder(Long orderId) {
        Order order = findById(orderId);
        order.cancel();
        increaseStock(order);
        return save(order);
    }

    private Order findById(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() ->new OrderNotFoundException(orderId));
        return order;
    }

    private void increaseStock(Order order) {
        for(OrderItem orderItem : order.getOrderItems()) {
            productService.increaseStock(orderItem.getProductId(), orderItem.getQuantity());
        }
    }

    private void decreaseStock(boolean success, Order order) {
        if(success) {
            for(OrderItem orderItem : order.getOrderItems()) {
                productService.decreaseStock(orderItem.getProductId(), orderItem.getQuantity());
            }
        }
    }

    private OrderResult save(Order order) {
        return OrderResult.from(orderRepository.save(order));
    }
}
