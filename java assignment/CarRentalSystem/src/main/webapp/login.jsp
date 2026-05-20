<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<% utils.CSRFUtil.ensureToken(session); %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Login - CarRental</title>
    <jsp:include page="WEB-INF/fragments/layout-head.jsp" />
</head>
<body>
    <div class="container d-flex justify-content-center align-items-center vh-100">
        <div class="card shadow-lg border-0 rounded-4 overflow-hidden" style="max-width: 450px; width: 100%;">
            <div class="p-5">
                <div class="text-center mb-4">
                    <div class="bg-primary d-inline-flex align-items-center justify-content-center rounded-circle mb-3" style="width: 60px; height: 60px;">
                        <i class="fas fa-car-side text-white fs-3"></i>
                    </div>
                    <h3 class="fw-bold">Welcome Back</h3>
                    <p class="text-muted small">Please enter your details to sign in</p>
                </div>

                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger border-0 small py-2 mb-4" role="alert">
                        <i class="fas fa-exclamation-circle me-2"></i> ${fn:escapeXml(errorMessage)}
                    </div>
                </c:if>
                <c:if test="${not empty param.success}">
                    <div class="alert alert-success border-0 small py-2 mb-4">
                        <i class="fas fa-check-circle me-2"></i> ${fn:escapeXml(param.success)}
                    </div>
                </c:if>

                <form action="${pageContext.request.contextPath}/login" method="post" onsubmit="showSpinner()">
                    <input type="hidden" name="csrf_token" value="${sessionScope.csrf_token}"/>
                    
                    <div class="mb-3">
                        <label class="form-label" for="loginId">Username or Email</label>
                        <div class="input-group">
                            <span class="input-group-text border-end-0"><i class="far fa-user text-muted"></i></span>
                            <input type="text" name="loginId" id="loginId" class="form-control border-start-0 ps-0" 
                                   required value="${fn:escapeXml(loginId)}" placeholder="Enter username or email">
                        </div>
                    </div>

                    <div class="mb-4">
                        <div class="d-flex justify-content-between">
                            <label class="form-label" for="password">Password</label>
                            <a href="${pageContext.request.contextPath}/forgotPassword" class="text-primary small text-decoration-none fw-medium">Forgot?</a>
                        </div>
                        <div class="input-group">
                            <span class="input-group-text border-end-0"><i class="fas fa-lock text-muted"></i></span>
                            <input type="password" name="password" id="password" class="form-control border-start-0 ps-0" 
                                   required placeholder="••••••••" onblur="validateRequired(this)">
                            <button class="btn btn-outline-light border-start-0" type="button" onclick="togglePassword()">
                                <i class="far fa-eye text-muted" id="toggleIcon"></i>
                            </button>
                        </div>
                        <div class="invalid-feedback d-none" id="passwordError">Password is required.</div>
                    </div>

                    <button type="submit" class="btn btn-primary w-100 py-2 fw-bold shadow-sm">Sign In</button>
                </form>

                <div class="mt-4 text-center">
                    <p class="text-muted small">New here? <a href="${pageContext.request.contextPath}/register" class="text-primary fw-bold text-decoration-none">Create Account</a></p>
                </div>
            </div>
        </div>
    </div>

    <div class="spinner-overlay" id="global-spinner">
        <div class="spinner"></div>
    </div>

    <script>
        function togglePassword() {
            const passInput = document.getElementById('password');
            const icon = document.getElementById('toggleIcon');
            if (passInput.type === 'password') {
                passInput.type = 'text';
                icon.classList.replace('fa-eye', 'fa-eye-slash');
            } else {
                passInput.type = 'password';
                icon.classList.replace('fa-eye-slash', 'fa-eye');
            }
        }
        function validateEmail(input) {
            const error = document.getElementById('emailError');
            const isValid = input.value.match(/^[^\s@]+@[^\s@]+\.[^\s@]+$/);
            if (!isValid && input.value !== '') {
                input.classList.add('is-invalid');
                error.classList.remove('d-none');
            } else {
                input.classList.remove('is-invalid');
                error.classList.add('d-none');
            }
        }
        function validateRequired(input) {
            const error = document.getElementById(input.id + 'Error');
            if (input.value.trim() === '') {
                input.classList.add('is-invalid');
                error.classList.remove('d-none');
            } else {
                input.classList.remove('is-invalid');
                error.classList.add('d-none');
            }
        }
        function showSpinner() {
            document.getElementById('global-spinner').style.display = 'flex';
        }
    </script>
</body>
</html>