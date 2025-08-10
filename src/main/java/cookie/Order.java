package cookie;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter @ToString
public class Order {
    private Long orderId;
    private Long userId;
    private String status;          // PENDING/PAID/...
    private BigDecimal totalAmount; // 최종 결제금액
    private String address;
    private String postcode;
    private LocalDateTime createdAt; // 필요 시 (컬럼 없으면 제거)
    private List<OrderItem> items;
}
