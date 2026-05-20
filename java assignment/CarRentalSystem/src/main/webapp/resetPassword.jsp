<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Reset Password - CarRental</title>
    <jsp:include page="WEB-INF/fragments/layout-head.jsp" />
</head>
<body>
    <div class="container d-flex justify-content-center align-items-center vh-100">
        <div class="card shadow border-0 rounded-4" style="width: 400px;">
            <div class="card-body p-4 text-center">
                <h3 class="fw-bold mb-3">Set New Password</h3>
                <p class="text-muted small mb-4">Choose a strong password for your account.</p>
                
                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger border-0 small" role="alert">${fn:escapeXml(errorMessage)}</div>
                </c:if>

                <form action="${pageContext.request.contextPath}/resetPassword" method="post">
                    <input type="hidden" name="token" value="${fn:escapeXml(token)}">
                    <div class="mb-3 text-start">
                        <label class="form-label small fw-bold">New Password</label>
                        <input type="password" name="password" class="form-control" required placeholder="••••••••">
                    </div>
                    <div class="mb-3 text-start">
                        <label class="form-label small fw-bold">Confirm Password</label>
                        <input type="password" name="confirmPassword" class="form-control" required placeholder="••••••••">
                    </div>
                    <button type="submit" class="btn btn-primary w-100 py-2">Update Password</button>
                </form>
            </div>
        </div>
    </div>
</body>
</html>