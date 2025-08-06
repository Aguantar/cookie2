<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <meta charset="UTF-8">
    <title>책 목록</title>
</head>
<body>

<h2>📚 책 목록</h2>

<c:forEach var="book" items="${books}">
    <div style="margin-bottom: 10px;">
        <p>책 이름: ${book.name}</p>
        <p>가격: ${book.price}</p>

        <form action="addToCart" method="post">
            <input type="hidden" name="bookId" value="${book.id}" />
            <input type="hidden" name="bookName" value="${book.name}" />
            <input type="hidden" name="price" value="${book.price}" />
            <button type="submit">장바구니 담기</button>
        </form>
        
    </div>
</c:forEach>

	<form action="cart" method="get">
        <button type="submit">장바구니 이동</button>
    </form>

</body>
</html>
