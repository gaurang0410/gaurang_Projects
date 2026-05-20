<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>${not empty pageTitle ? pageTitle : 'CarRental Enterprise'}</title>
    <jsp:include page="layout-head.jsp" />
</head>
<body>
    <div class="d-flex">
        <c:if test="${not empty sessionScope.loggedUser}">
            <jsp:include page="sidebar.jsp" />
        </c:if>
        
        <div id="content" class="flex-grow-1">
            <c:if test="${not empty sessionScope.loggedUser}">
                <jsp:include page="header.jsp" />
            </c:if>
            
            <div class="page-container">
                <%-- Main content begins here --%>
