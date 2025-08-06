<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <meta charset="UTF-8">
    <title>장바구니</title>
</head>
<body>

<h2>🛒 장바구니</h2>

<c:choose>
    <c:when test="${not empty cartItems}">
        <table border="1" cellpadding="10">
            <thead>
                <tr>
                    <th>책 번호</th>
                    <th>책 제목</th>
                    <th>가격</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="item" items="${cartItems}">
                    <tr>
                        <td>${item.id}</td>
                        <td>${item.name}</td>
                        <td>${item.price}</td>
                        
                        <td><form action="cart/remove" method="post" style="display:inline;">
            			<input type="hidden" name="bookId" value="${item.id}" />
            			<button type="submit">❌ 삭제</button>
        				</form></td>
                    </tr>
                    
                </c:forEach>
            </tbody>
        </table>
    </c:when>
    <c:otherwise>
        <p>장바구니에 담긴 상품이 없습니다.</p>
    </c:otherwise>
</c:choose>

<br/>

<a href="bookList">← 책 목록으로 돌아가기</a>

<form action="cart/clear" method="post">
    <button type="submit">🗑 장바구니 비우기</button>
</form>
</body>
</html>