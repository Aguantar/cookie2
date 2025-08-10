package cookie;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final CheckoutService checkoutService;

    @GetMapping("bookList")
    public String bookList(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        return "bookList"; // -> /WEB-INF/views/cookie/bookList.jsp
    }

    @PostMapping("addToCart")
    public String addToCart(@RequestParam("bookId") String bookId,
                            @RequestParam("bookName") String bookName,
                            @RequestParam("price") String price, // 폼에서 받은 문자열
                            HttpServletRequest request,
                            HttpServletResponse response) {

        String encName  = enc(bookName);
        String priceStr = String.valueOf(Integer.parseInt(price)); // 숫자 검증

        String cart = readCookie(request, "cart");

        // 포맷: id:name:price:qty (|로 아이템 구분)
        Map<String, String> map = new LinkedHashMap<>();
        if (!cart.isEmpty()) {
            for (String token : cart.split("\\|")) {
                String[] p = token.split(":");
                if (p.length == 4) {
                    map.put(p[0].trim(), token);
                }
            }
        }

        if (map.containsKey(bookId)) {
            String[] p = map.get(bookId).split(":");
            int qty = Integer.parseInt(p[3]) + 1;
            map.put(bookId, p[0] + ":" + p[1] + ":" + p[2] + ":" + qty);
        } else {
            map.put(bookId, bookId + ":" + encName + ":" + priceStr + ":1");
        }

        String updated = String.join("|", map.values());
        writeCookie(response, "cart", updated, 60 * 60 * 24);

        return "redirect:/cookie/bookList";
    }

    @GetMapping("cart")
    public String viewCart(HttpServletRequest request, Model model) {
        String cart = readCookie(request, "cart");
        List<CartItem> list = new ArrayList<>();

        if (!cart.isEmpty()) {
            for (String token : cart.split("\\|")) {
                String[] p = token.split(":");
                if (p.length == 4) {
                    String id   = p[0].trim();
                    String name = dec(p[1]);
                    String priceStr = p[2];           // 표시용 문자열
                    int qty     = Integer.parseInt(p[3]);
                    list.add(new CartItem(id, name, priceStr, qty));
                }
            }
        }

        model.addAttribute("cartItems", list);
        return "cartView"; // -> /WEB-INF/views/cookie/cartView.jsp
    }

    @PostMapping("cart/clear")
    public String clearCart(HttpServletResponse response) {
        writeCookie(response, "cart", "", 0);
        return "redirect:/cookie/cart";
    }

    @PostMapping("cart/remove")
    public String removeItem(@RequestParam("bookId") String bookId,
                             @CookieValue(value = "cart", defaultValue = "") String cart,
                             HttpServletResponse response) {

        if (!cart.isEmpty()) {
            List<String> kept = new ArrayList<>();
            for (String token : cart.split("\\|")) {
                String[] p = token.split(":");
                if (p.length == 4 && !p[0].trim().equals(bookId.trim())) {
                    kept.add(token);
                }
            }
            String updated = String.join("|", kept);
            writeCookie(response, "cart", updated, 60 * 60 * 24);
        }
        return "redirect:/cookie/cart";
    }

    // ▼▼ 추가: 수량 -1
    @PostMapping("cart/decrease")
    public String decreaseItem(@RequestParam("bookId") String bookId,
                               @CookieValue(value = "cart", defaultValue = "") String cart,
                               HttpServletResponse response) {
        if (!cart.isEmpty()) {
            String updated = updateQty(cart, bookId, -1); // qty - 1 (0 이하가 되면 항목 제거)
            writeCookie(response, "cart", updated, 60 * 60 * 24);
        }
        return "redirect:/cookie/cart";
    }

    // ▼▼ 추가: 수량 +1
    @PostMapping("cart/increase")
    public String increaseItem(@RequestParam("bookId") String bookId,
                               @CookieValue(value = "cart", defaultValue = "") String cart,
                               HttpServletResponse response) {
        if (!cart.isEmpty()) {
            String updated = updateQty(cart, bookId, +1); // qty + 1
            writeCookie(response, "cart", updated, 60 * 60 * 24);
        }
        return "redirect:/cookie/cart";
    }

    @PostMapping("checkout")
    public String checkout(@RequestParam("userId") Long userId,
                           @RequestParam("address") String address,
                           @RequestParam("postcode") String postcode,
                           HttpServletRequest request,
                           HttpServletResponse response,
                           Model model) {

        String cart = readCookie(request, "cart");
        if (cart.isEmpty()) {
            model.addAttribute("error", "장바구니가 비어 있습니다.");
            return "cartView";
        }

        List<CartItem> list = new ArrayList<>();
        for (String token : cart.split("\\|")) {
            String[] p = token.split(":");
            if (p.length == 4) {
                String id   = p[0].trim();
                String name = dec(p[1]);
                String priceStr = p[2];          // 표시용
                int qty     = Integer.parseInt(p[3]);
                list.add(new CartItem(id, name, priceStr, qty));
            }
        }

        try {
            Long orderId = checkoutService.checkout(userId, address, postcode, list);
            writeCookie(response, "cart", "", 0); // 성공 시 장바구니 비우기
            model.addAttribute("orderId", orderId);
            return "checkoutSuccess";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "cartView";
        }
    }

    // ===== Helpers =====
    private String readCookie(HttpServletRequest req, String name) {
        Cookie[] cs = req.getCookies();
        if (cs == null) return "";
        for (Cookie c : cs) if (name.equals(c.getName())) return c.getValue();
        return "";
    }

    private void writeCookie(HttpServletResponse resp, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        resp.addCookie(cookie);
    }

    private String enc(String v) {
        try { return URLEncoder.encode(v, "UTF-8"); }
        catch (UnsupportedEncodingException e) { throw new RuntimeException(e); }
    }
    private String dec(String v) {
        try { return URLDecoder.decode(v, "UTF-8"); }
        catch (UnsupportedEncodingException e) { throw new RuntimeException(e); }
    }

    // qty 증감 공통 처리 (delta: +1 / -1)
    private String updateQty(String cart, String targetId, int delta) {
        List<String> out = new ArrayList<>();
        for (String token : cart.split("\\|")) {
            String[] p = token.split(":"); // id:name:price:qty
            if (p.length != 4) continue;
            String id = p[0].trim();
            if (id.equals(targetId.trim())) {
                int qty = Integer.parseInt(p[3]) + delta;
                if (qty > 0) {
                    out.add(p[0] + ":" + p[1] + ":" + p[2] + ":" + qty);
                }
                // qty <= 0 이면 항목 제거(추가 안 함)
            } else {
                out.add(token);
            }
        }
        return String.join("|", out);
    }
}
