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
        List<Book> books = bookService.getAllBooks(); // 책 목록 DB 조회
        model.addAttribute("books", books);  // model에 books 저장
        return "bookList";  // bookList.jsp로 이동
    }
    
    @PostMapping("addToCart")
    public String addToCart(
            @RequestParam("bookId") String bookId,
            @RequestParam("bookName") String bookName,
            @RequestParam("price") String price,
            HttpServletRequest request,
            HttpServletResponse response) { //폼 데이터(bookId, name, price)를 받아옴

        // 쿠키 이름: cart
        String newItem = bookId + ":" + bookName + ":" + price;

        // 기존 cart 쿠키가 있는지 확인
        Cookie[] cookies = request.getCookies(); //쿠키 전부 가져옴
        String cartValue = "";

        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("cart".equals(c.getName())) {//쿠키이름이 cart일떄
                    cartValue = c.getValue(); //기존 쿠키값 저장
                }
            }
        }

        // 새 항목 추가
        if (!cartValue.isEmpty()) {//기존 쿠키값이 있는 경우 새로운 값 추가
            cartValue += "|" + newItem;  // 구분자로 | 사용
        } else {//기존 쿠키값 없으면 새로운 값 저장
            cartValue = newItem;
        }

        // 쿠키 생성 및 설정
        Cookie cartCookie = new Cookie("cart", cartValue);
        cartCookie.setMaxAge(60 * 60 * 24); // 하루
        cartCookie.setPath("/"); // 전체 경로에서 접근 가능하도록 설정
        response.addCookie(cartCookie);

        // 장바구니 페이지로 리다이렉션
        return "redirect:/cookie/bookList"; // bookList.jsp로 이동
    }
    
    @GetMapping("cart")
    public String viewCart(HttpServletRequest request, Model model) {
        Cookie[] cookies = request.getCookies();
        List<Book> cartItems = new ArrayList<>();

        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("cart".equals(c.getName())) {
                    String[] items = c.getValue().split("\\|"); // | 구분자로 해서 저장 
                    //"101:자바책:10000|102:스프링책:15000" → ["101:자바책:10000", "102:스프링책:15000"]
                    for (String item : items) {
                        String[] parts = item.split(":"); // : 기준으로 나눔
                        //"101:자바책:10000" → ["101", "자바책", "10000"]
                        if (parts.length == 3) { //정확히 3개의 요소가 있는지 확인
                            Book book = new Book(parts[0], parts[1], parts[2]);
                            cartItems.add(book); //쿠키에서 cart 읽고 Book 객체로 변환해서 리스트에 저장
                        }
                    }
                }
            }
        }

        model.addAttribute("cartItems", cartItems);
        //JSP에서 사용할 수 있도록 cartItems라는 이름으로 model에 등록
        //→ cartView.jsp에서 ${cartItems}로 접근 가능
        return "cartView"; // cartView.jsp 이동
    }
    
    @PostMapping("cart/clear")
    public String clearCart(HttpServletResponse response) {
        Cookie cookie = new Cookie("cart", ""); //이름이 "cart"인 쿠키를 새로 생성
        //값은 "" (빈 문자열)로 설정 → 기존 장바구니 내용을 제거
        cookie.setPath("/");//해당 쿠키의 유효 경로를 /로 설정
        cookie.setMaxAge(0); // 쿠키의 유효 시간을 0초로 설정 → 브라우저가 즉시 삭제
        response.addCookie(cookie);
        return "redirect:/cookie/cart"; // 장바구니 페이지로 리다이렉트
    }
    
    @PostMapping("cart/remove")
    public String removeItem(@RequestParam("bookId") String bookId, //사용자가 삭제 요청한 책의 ID를 파라미터로 받아옴
                             @CookieValue(value = "cart", defaultValue = "") String cart, //기존 쿠키 값을 가져옴
                             HttpServletResponse response) {  //쿠키를 새로 설정하여 브라우저에 전달하기 위해 사용

        System.out.println("요청된 bookId = [" + bookId + "]");
        System.out.println("기존 쿠키 cart = [" + cart + "]");

        if (!cart.isEmpty()) { //쿠키가 비어있지 않을 때만 처리
        	String[] items = cart.split("\\|"); // | 구분자로 나눠서 저장
            StringBuilder updatedCart = new StringBuilder();
            //삭제하고 남은 항목들을 새로 담을 문자열 생성기

            for (String item : items) {
                String[] parts = item.split(":");  // : 구분자로 나눠서 저장

                // 공백제거를 위해 trim()
                String currentId = parts[0].trim(); 
                // 현재 비교 하려는 id를 공백제거해서 저장

                System.out.println("현재 비교 대상 id = [" + currentId + "], 제목 = [" + parts[1] + "]");

                if (!currentId.equals(bookId.trim())) {//삭제하려는 id와 비교해서 삭제대상이 아니라면
                    if (updatedCart.length() > 0) //(맨 앞에는 | 안 붙임)
                    	updatedCart.append("|");
                    updatedCart.append(item);//새로운 쿠키 문자열에 추가 
                } else {
                    System.out.println("삭제됨: " + item);
                }
            }

            Cookie newCart = new Cookie("cart", updatedCart.toString());//새로운 쿠키 생성
            newCart.setPath("/");
            newCart.setMaxAge(60 * 60 * 24); // 1일
            response.addCookie(newCart);
        }

        return "redirect:/cookie/cart"; //장바구니페이지 이동
    }

}