<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="model.User" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>${pageTitle} - Car Rental System</title>
    <jsp:include page="layout-head.jsp" />
</head>
<body>
    <div class="d-flex">
        <!-- Sidebar Navigation -->
        <jsp:include page="sidebar.jsp" />
        
        <!-- Main Content Area -->
        <div id="content" class="flex-grow-1">
            <!-- Header -->
            <jsp:include page="header.jsp" />
            
            <!-- Content Container -->
            <div class="container-fluid p-4">
                <!-- Page Title and Actions -->
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <div>
                        <h3 class="fw-bold mb-1">${pageTitle}</h3>
                        <p class="text-muted mb-0">${pageDescription}</p>
                    </div>
                    <div id="pageActions">
                        <!-- Child JSP can inject actions here -->
                    </div>
                </div>

                <!-- Content Body -->
                <jsp:doBody />
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script src="${pageContext.request.contextPath}/js/admin.js"></script>
</body>
</html>