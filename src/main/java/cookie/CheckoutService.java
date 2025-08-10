package cookie;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final BookMapper bookMapper;
    private final OrderMapper orderMapper;

    @Transactional
    public Long checkout(Long userId, String address, String postcode, List<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("장바구니가 비어 있습니다.");
        }

        // 주문 헤더 생성 (총액 0으로 시작)
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus("PENDING");
        order.setTotalAmount(BigDecimal.ZERO); // ★ BigDecimal 0
        order.setAddress(address);
        order.setPostcode(postcode);

        int r = orderMapper.insertOrder(order);
        if (r != 1 || order.getOrderId() == null) {
            throw new IllegalStateException("주문 생성 실패");
        }
        long orderId = order.getOrderId();

        BigDecimal total = BigDecimal.ZERO; // ★ 누적 합계

        for (CartItem ci : cartItems) {
            Long bookId = Long.parseLong(ci.getId());
            int qty = ci.getQty();

            Book dbBook = bookMapper.findBookForCheckout(bookId);
            if (dbBook == null) {
                throw new IllegalStateException("존재하지 않는 도서: " + bookId);
            }
            int unitPrice = dbBook.getPrice(); // books.price = int

            // 재고 차감 (재고 부족 시 0행 → 예외)
            int dec = bookMapper.decrementStock(bookId, qty);
            if (dec != 1) {
                throw new IllegalStateException("재고 부족: bookId=" + bookId);
            }

            OrderItem item = new OrderItem();
            item.setOrderId(orderId);
            item.setBookId(bookId);
            item.setQuantity(qty);
            item.setUnitPrice(unitPrice); // order_items.unit_price = INT
            orderMapper.insertOrderItem(item);

            // ★ BigDecimal로 합계 누적
            total = total.add(
                    BigDecimal.valueOf(unitPrice)
                            .multiply(BigDecimal.valueOf(qty))
            );
        }

        // ★ 합계 BigDecimal로 업데이트
        orderMapper.updateOrderTotal(orderId, total);
        return orderId;
    }
}
