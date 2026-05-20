<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Forgot Password - CarRental</title>
    <jsp:include page="WEB-INF/fragments/layout-head.jsp" />
</head>
<body>
    <div class="container d-flex justify-content-center align-items-center vh-100">
        <div class="card shadow border-0 rounded-4" style="width: 400px;">
            <div class="card-body p-4 text-center">
                <h3 class="fw-bold mb-3">Forgot Password</h3>
                <p class="text-muted small mb-4">Enter your email and we'll send you a reset link.</p>
                
                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger border-0 small" role="alert">${fn:escapeXml(errorMessage)}</div>
                </c:if>
                <c:if test="${not empty successMessage}">
                    <div class="alert alert-success border-0 small">${fn:escapeXml(successMessage)}</div>
                </c:if>

                <form action="${pageContext.request.contextPath}/forgotPassword" method="post">
                    <div class="mb-3 text-start">
                        <label class="form-label small fw-bold">Email Address</label>
                        <input type="email" name="email" class="form-control" required placeholder="name@example.com">
                    </div>
                    <button type="submit" class="btn btn-primary w-100 py-2 mb-3">Send Reset Link</button>
                    <a href="${pageContext.request.contextPath}/login" class="text-decoration-none small text-primary">Back to Login</a>
                </form>
            </div>
        </div>
    </div>
</body>
</html>