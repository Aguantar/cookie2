<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>장바구니</title>
    <style>
        body { font-family: sans-serif; }
        table { border-collapse: collapse; width: 100%; margin: 12px 0; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background: #f7f7f7; }
        .actions { display: inline-flex; gap: 6px; }
        .right { text-align: right; }
        .muted { color: #666; font-size: 12px; }
        .danger { color: #b00020; }
        button { cursor: pointer; }
    </style>
</head>
<body>

<h2>장바구니</h2>

<!-- 디버그: 원시 cart 쿠키 값 보기 (테스트 후 삭제 권장) -->
<div>
    <details>
        <summary>쿠키(raw) 보기 (테스트용)</summary>
        <div class="muted">
            <%
                javax.servlet.http.Cookie[] cs = request.getCookies();
                String raw = "";
                if (cs != null) {
                    for (javax.servlet.http.Cookie c : cs) {
                        if ("cart".equals(c.getName())) { raw = c.getValue(); break; }
                    }
                }
                out.print("cart=" + (raw == null ? "" : raw));
            %>
            <p>포맷: <code>bookId:URLEncodedName:price:qty</code> 를 <code>|</code>로 연결</p>
        </div>
    </details>
</div>

<!-- 에러 메시지 표시 -->
<c:if test="${not empty error}">
    <p class="danger">${error}</p>
</c:if>

<!-- 장바구니 아이템 테이블 -->
<c:choose>
    <c:when test="${empty cartItems}">
        <p>장바구니가 비어 있습니다.</p>
    </c:when>
    <c:otherwise>
        <table>
            <thead>
            <tr>
                <th>도서 ID</th>
                <th>제목</th>
                <th class="right">가격(원)</th>
                <th class="right">수량</th>
                <th class="right">합계(원)</th>
                <th>액션</th>
            </tr>
            </thead>
            <tbody>
            <c:set var="grand" value="0" />
            <c:forEach var="it" items="${cartItems}">
                <tr>
                    <td>${it.id}</td>
                    <td>${it.name}</td>
                    <td class="right"><c:out value="${it.price}" /></td>
                    <td class="right">${it.qty}</td>
                    <td class="right">
                        <c:set var="line" value="${it.price * it.qty}" />
                        <c:out value="${line}" />
                        <c:set var="grand" value="${grand + line}" />
                    </td>
                    <td>
                        <div class="actions">
                            <!-- +1 증가 -->
                            <form method="post" action="<c:url value='/cookie/cart/increase'/>" style="display:inline;">
                                <input type="hidden" name="bookId" value="${it.id}">
                                <button type="submit">+ 1개</button>
                            </form>
                            <!-- -1 감소 (1에서 누르면 항목 제거) -->
                            <form method="post" action="<c:url value='/cookie/cart/decrease'/>" style="display:inline;">
                                <input type="hidden" name="bookId" value="${it.id}">
                                <button type="submit">- 1개</button>
                            </form>
                            <!-- 항목 전체 삭제 -->
                            <form method="post" action="<c:url value='/cookie/cart/remove'/>" style="display:inline;">
                                <input type="hidden" name="bookId" value="${it.id}">
                                <button type="submit">항목 삭제</button>
                            </form>
                        </div>
                    </td>
                </tr>
            </c:forEach>
            <tr>
                <th colspan="4" class="right">총합</th>
                <th class="right"><c:out value="${grand}" /></th>
                <th></th>
            </tr>
            </tbody>
        </table>

        <div style="margin:8px 0;">
            <!-- 장바구니 비우기 -->
            <form method="post" action="<c:url value='/cookie/cart/clear'/>" style="display:inline;">
                <button type="submit">장바구니 비우기</button>
            </form>
        </div>

        <hr/>

        <!-- 결제 폼 -->
        <h3>결제 정보</h3>
        <form method="post" action="<c:url value='/cookie/checkout'/>">
            <div>
                <label>사용자 ID:
                    <input type="number" name="userId" value="1" required>
                </label>
            </div>
            <div>
                <label>주소:
                    <input type="text" name="address" value="서울시 테스트구 테스트로 1" required style="width: 420px;">
                </label>
            </div>
            <div>
                <label>우편번호:
                    <input type="text" name="postcode" value="01234" required>
                </label>
            </div>
            <div style="margin-top:8px;">
                <button type="submit">결제하기</button>
            </div>
        </form>
    </c:otherwise>
</c:choose>

<p><a href="<c:url value='/cookie/bookList'/>">← 도서 목록으로</a></p>

</body>
</html>
