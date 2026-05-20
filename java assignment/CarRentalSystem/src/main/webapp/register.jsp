<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<% utils.CSRFUtil.ensureToken(session); %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Create Account - CarRental</title>
    <jsp:include page="WEB-INF/fragments/layout-head.jsp" />
</head>
<body>
    <div class="container d-flex justify-content-center align-items-center py-5">
        <div class="card shadow-lg border-0 rounded-4 overflow-hidden" style="max-width: 600px; width: 100%;">
            <div class="p-5">
                <div class="text-center mb-4">
                    <div class="bg-primary d-inline-flex align-items-center justify-content-center rounded-circle mb-3" style="width: 60px; height: 60px;">
                        <i class="fas fa-user-plus text-white fs-3"></i>
                    </div>
                    <h3 class="fw-bold">Create Account</h3>
                    <p class="text-muted small">Join our community of premium car rentals</p>
                </div>

                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger border-0 small py-2 mb-4" role="alert">
                        <i class="fas fa-exclamation-circle me-2"></i> ${fn:escapeXml(errorMessage)}
                    </div>
                </c:if>

                <form action="${pageContext.request.contextPath}/register" method="post" onsubmit="showSpinner()">
                    <input type="hidden" name="csrf_token" value="${sessionScope.csrf_token}"/>
                    
                    <div class="row g-3 mb-3">
                        <div class="col-md-6">
                            <label class="form-label">Full Name</label>
                            <input type="text" name="fullName" class="form-control" 
                                   required value="${fn:escapeXml(fullName)}" placeholder="John Doe">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Phone Number</label>
                            <input type="text" name="phoneNumber" class="form-control" 
                                   required value="${fn:escapeXml(phoneNumber)}" placeholder="10-digit number">
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Username</label>
                        <input type="text" name="username" class="form-control" 
                               required value="${fn:escapeXml(username)}" placeholder="Choose a username"
                               minlength="3" maxlength="30" pattern="[a-zA-Z0-9_]+"
                               title="Letters, numbers, and underscores only">
                        <div class="form-text extra-small text-muted">3–30 characters. Letters, numbers, underscores only.</div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Email Address</label>
                        <input type="email" name="email" class="form-control" 
                               required value="${fn:escapeXml(email)}" placeholder="name@example.com">
                    </div>

                    <div class="row g-3 mb-4">
                        <div class="col-md-6">
                            <label class="form-label">Password</label>
                            <input type="password" name="password" id="password" class="form-control" 
                                   required placeholder="••••••••" onkeyup="checkStrength(this.value)">
                            <div class="progress mt-2" style="height: 4px;">
                                <div id="strengthBar" class="progress-bar" role="progressbar" style="width: 0%"></div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Confirm Password</label>
                            <input type="password" name="confirmPassword" id="confirmPassword" class="form-control" 
                                   required placeholder="••••••••" onkeyup="validateMatch()">
                            <div id="matchText" class="extra-small mt-1"></div>
                        </div>
                    </div>

                    <div class="mb-4 form-check">
                        <input type="checkbox" class="form-check-input" id="terms" required>
                        <label class="form-check-label small text-muted" for="terms">I agree to the <a href="#" class="text-primary text-decoration-none">Terms & Conditions</a></label>
                    </div>

                    <button type="submit" class="btn btn-primary w-100 py-2 fw-bold shadow-sm">Create Account</button>
                </form>

                <div class="mt-4 text-center">
                    <p class="text-muted small">Already have an account? <a href="${pageContext.request.contextPath}/login" class="text-primary fw-bold text-decoration-none">Sign In</a></p>
                </div>
            </div>
        </div>
    </div>

    <div class="spinner-overlay" id="global-spinner">
        <div class="spinner"></div>
    </div>

    <script>
        function checkStrength(password) {
            const bar = document.getElementById('strengthBar');
            let strength = 0;
            if (password.length > 5) strength += 30;
            if (password.match(/[A-Z]/)) strength += 20;
            if (password.match(/[0-9]/)) strength += 25;
            if (password.match(/[^a-zA-Z0-9]/)) strength += 25;
            
            bar.style.width = strength + '%';
            bar.className = 'progress-bar';
            if (strength < 40) bar.classList.add('bg-danger');
            else if (strength < 80) bar.classList.add('bg-warning');
            else bar.classList.add('bg-success');
        }

        function validateMatch() {
            const pass = document.getElementById('password').value;
            const confirm = document.getElementById('confirmPassword').value;
            const text = document.getElementById('matchText');
            if (confirm === '') { text.innerText = ''; return; }
            if (pass === confirm) {
                text.innerText = 'Passwords match';
                text.className = 'extra-small mt-1 text-success';
            } else {
                text.innerText = 'Passwords do not match';
                text.className = 'extra-small mt-1 text-danger';
            }
        }

        function showSpinner() {
            document.getElementById('global-spinner').style.display = 'flex';
        }
    </script>
</body>
</html>