<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Error - Car Rental System</title>
    <jsp:include page="WEB-INF/fragments/layout-head.jsp" />
    <style>
        body {
            display: flex;
            align-items: center;
            justify-content: center;
            min-height: 100vh;
            background: linear-gradient(135deg, var(--bg-primary) 0%, var(--bg-secondary) 100%);
        }
        .error-container {
            background: var(--card-color);
            border: 1px solid var(--border-color);
            border-radius: 12px;
            padding: 3rem;
            box-shadow: var(--shadow-lg);
            text-align: center;
            max-width: 500px;
            animation: slideUp 0.5s ease-out;
        }
        @keyframes slideUp {
            from { opacity: 0; transform: translateY(20px); }
            to { opacity: 1; transform: translateY(0); }
        }
        .error-icon {
            font-size: 4rem;
            color: var(--danger);
            margin-bottom: 1rem;
        }
        .error-title {
            font-size: 1.8rem;
            font-weight: 700;
            color: var(--text-primary);
            margin-bottom: 1rem;
        }
        .error-message {
            color: var(--text-secondary);
            margin-bottom: 2rem;
            line-height: 1.6;
        }
        .error-details {
            background: var(--bg-secondary);
            border-left: 4px solid var(--danger);
            padding: 1rem;
            border-radius: 4px;
            text-align: left;
            margin-bottom: 2rem;
            font-size: 0.9rem;
            color: var(--text-muted);
            font-family: 'Courier New', monospace;
        }
        .btn-group-error {
            display: flex;
            gap: 1rem;
            justify-content: center;
        }
        .btn-group-error a { flex: 1; }
    </style>
</head>
<body>
    <div class="error-container">
        <div class="error-icon">
            <i class="fas fa-exclamation-triangle"></i>
        </div>
        <h1 class="error-title">Oops! An Error Occurred</h1>
        <p class="error-message">
            <% 
                String errorMessage = (String) request.getAttribute("errorMessage");
                if (errorMessage != null) {
                    out.print(errorMessage);
                } else {
                    out.print("We encountered an unexpected error. Please try again later.");
                }
            %>
        </p>
        
        <% 
            String errorCode = (String) request.getAttribute("errorCode");
            if (errorCode != null) {
        %>
        <div class="error-details">
            <strong>Error Code:</strong> <%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(errorCode)) %><br>
            <% 
                String errorDetails = (String) request.getAttribute("errorDetails");
                if (errorDetails != null) {
            %>
            <strong>Details:</strong> <%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(errorDetails)) %>
            <% } %>
        </div>
        <% } %>
        
        <div class="btn-group-error">
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-primary">
                <i class="fas fa-home me-2"></i>Dashboard
            </a>
            <a href="javascript:history.back()" class="btn btn-outline-secondary">
                <i class="fas fa-arrow-left me-2"></i>Go Back
            </a>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>