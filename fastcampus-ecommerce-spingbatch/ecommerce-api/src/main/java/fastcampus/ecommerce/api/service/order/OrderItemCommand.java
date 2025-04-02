package fastcampus.ecommerce.api.service.order;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderItemCommand {
    private String productId;
    private int quantity;

    public static OrderItemCommand of(String productId, int quantity) {
        return new OrderItemCommand(productId, quantity);
    }


}
