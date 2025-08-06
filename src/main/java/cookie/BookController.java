package cookie;



import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("bookList")
    public String bookList(Model model) {
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", books);
        return "bookList"; 
    }
    
    @PostMapping("addToCart")
    public String addToCart(
            @RequestParam("bookId") String bookId,
            @RequestParam("bookName") String bookName,
            @RequestParam("price") String price,
            HttpServletRequest request,
            HttpServletResponse response) {

        // 쿠키 이름: cart
        String newItem = bookId + ":" + bookName + ":" + price;

        // 기존 cart 쿠키가 있는지 확인
        Cookie[] cookies = request.getCookies();
        String cartValue = "";

        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("cart".equals(c.getName())) {
                    cartValue = c.getValue();
                }
            }
        }

        // 새 항목 추가
        if (!cartValue.isEmpty()) {
            cartValue += "|" + newItem;  // 구분자로 | 사용
        } else {
            cartValue = newItem;
        }

        // 쿠키 생성 및 설정
        Cookie cartCookie = new Cookie("cart", cartValue);
        cartCookie.setMaxAge(60 * 60 * 24); // 하루
        cartCookie.setPath("/"); // 전체 경로에서 접근 가능하도록 설정
        response.addCookie(cartCookie);

        // 장바구니 페이지로 리디렉션하거나 메시지 전달
        return "redirect:/cookie/bookList"; // 예: 책 목록 페이지로
    }
    
    @GetMapping("cart")
    public String viewCart(HttpServletRequest request, Model model) {
        Cookie[] cookies = request.getCookies();
        List<BookDTO> cartItems = new ArrayList<>();

        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("cart".equals(c.getName())) {
                    String[] items = c.getValue().split("\\|"); // 각 책
                    for (String item : items) {
                        String[] parts = item.split(":"); // bookId:bookName:price
                        if (parts.length == 3) {
                            BookDTO book = new BookDTO(parts[0], parts[1], parts[2]);
                            cartItems.add(book);
                        }
                    }
                }
            }
        }

        model.addAttribute("cartItems", cartItems);
        return "cartView"; // JSP 페이지 이름
    }
    
    @PostMapping("cart/clear")
    public String clearCart(HttpServletResponse response) {
        Cookie cookie = new Cookie("cart", "");
        cookie.setPath("/");
        cookie.setMaxAge(0); // 즉시 삭제
        response.addCookie(cookie);
        return "redirect:/cookie/cart"; // 장바구니 페이지로 리다이렉트
    }
    
    @PostMapping("cart/remove")
    public String removeItem(@RequestParam("bookId") String bookId,
                             @CookieValue(value = "cart", defaultValue = "") String cart,
                             HttpServletResponse response) {

        System.out.println("요청된 bookId = [" + bookId + "]");
        System.out.println("기존 쿠키 cart = [" + cart + "]");

        if (!cart.isEmpty()) {
        	String[] items = cart.split("\\|");
            StringBuilder updatedCart = new StringBuilder();

            for (String item : items) {
                String[] parts = item.split(":");

                // 안전하게 비교 (trim() 꼭 사용!)
                String currentId = parts[0].trim();

                System.out.println("현재 비교 대상 id = [" + currentId + "], 제목 = [" + parts[1] + "]");

                if (!currentId.equals(bookId.trim())) {
                    if (updatedCart.length() > 0) updatedCart.append("|");
                    updatedCart.append(item);
                } else {
                    System.out.println("삭제됨: " + item);
                }
            }

            Cookie newCart = new Cookie("cart", updatedCart.toString());
            newCart.setPath("/");
            newCart.setMaxAge(60 * 60 * 24); // 1일
            response.addCookie(newCart);
        }

        return "redirect:/cookie/cart";
    }

}