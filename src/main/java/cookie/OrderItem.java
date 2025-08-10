package cookie;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter @ToString
public class OrderItem {
    private Long orderItemId;
    private Long orderId;
    private Long bookId;
    private Integer quantity;
    private Integer unitPrice;
}
