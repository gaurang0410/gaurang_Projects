<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
                <%-- Main content ends here --%>
            </div> <!-- /page-container -->
        </div> <!-- /content -->
    </div> <!-- /d-flex -->
    
    <div class="spinner-overlay" id="global-spinner">
        <div class="spinner"></div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <c:if test="${not empty requestScope.extraScripts}">
        ${requestScope.extraScripts}
    </c:if>
</body>
</html>
