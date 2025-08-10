package cookie;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

@Mapper
public interface OrderMapper {
    int insertOrder(Order order); // useGeneratedKeys
    int insertOrderItem(OrderItem item);
    int updateOrderTotal(@Param("orderId") Long orderId,
                         @Param("totalAmount") BigDecimal totalAmount); // ★ 변경
}
