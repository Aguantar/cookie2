package cookie;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter @ToString
public class CartItem {
    private String id;     // bookId (문자열로 보관: 쿠키 파싱 편의)
    private String name;   // title
    private String price;  // 문자형 가격(쿠키에서 문자열로 옴)
    private int    qty;    // 장바구니 수량
}
