<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<meta charset="UTF-8">

<c:forEach var="b" items="${books}">
    <form method="post" action="<c:url value='/cookie/addToCart'/>">
    <input type="hidden" name="bookId" value="${b.bookId}"/>
        <input type="hidden" name="bookName" value="${b.title}"/>
        <input type="hidden" name="price" value="${b.price}"/>
            ${b.title} - ${b.price}
        <button type="submit">담기</button>
    </form>
</c:forEach>

<a href="<c:url value='/cookie/cart'/>">장바구니 보기</a>
