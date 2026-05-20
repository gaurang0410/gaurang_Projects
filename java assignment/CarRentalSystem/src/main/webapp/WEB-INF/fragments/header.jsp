<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="model.User" %>
<%
    User navbarUser = (User) session.getAttribute("loggedUser");
%>

<%-- ── Global Assets (always loaded via header) ── --%>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css">
<script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>

<%-- ── Top Navbar ── --%>
<nav class="navbar navbar-expand-lg sticky-top border-bottom">
    <div class="container-fluid px-4">
        <%-- Mobile sidebar toggle --%>
        <button type="button" id="sidebarToggle" class="btn btn-light d-lg-none me-3"
                aria-label="Toggle Sidebar" onclick="document.getElementById('sidebar').classList.toggle('open'); document.getElementById('sidebarOverlay').classList.toggle('active');">
            <i class="fas fa-bars"></i>
        </button>

        <%-- Page title --%>
        <h4 class="mb-0 fw-bold d-none d-sm-inline-block">
            <c:out value="${pageTitle != null ? pageTitle : 'Dashboard'}" />
        </h4>

        <div class="ms-auto d-flex align-items-center gap-3">
            <%-- Theme toggle --%>
            <button id="themeToggle"
                    class="btn btn-light rounded-circle d-flex align-items-center justify-content-center"
                    style="width:40px;height:40px;"
                    title="Toggle Theme"
                    aria-label="Toggle dark/light mode">
                <i class="fas fa-moon"></i>
            </button>

            <%-- User info --%>
            <div class="text-end d-none d-md-block">
                <div class="fw-bold small" style="color:var(--text-primary);">
                    <c:out value="${loggedUser.fullName}" />
                </div>
                <div class="extra-small tracking-wider" style="color:var(--text-muted);">
                    <span style="color:var(--primary);font-weight:600;">@<c:out value="${loggedUser.username}" /></span>
                    &nbsp;·&nbsp;
                    <c:out value="${loggedUser.role}" />
                </div>
            </div>

            <%-- Avatar dropdown --%>
            <div class="dropdown">
                <button class="btn btn-light rounded-circle d-flex align-items-center justify-content-center"
                        type="button"
                        data-bs-toggle="dropdown"
                        aria-expanded="false"
                        style="width:40px;height:40px;"
                        aria-label="User menu">
                    <i class="fas fa-user" style="color:var(--primary);"></i>
                </button>
                <ul class="dropdown-menu dropdown-menu-end shadow-lg border-0 mt-2 p-2">
                    <li>
                        <a class="dropdown-item rounded-2 py-2"
                           href="${pageContext.request.contextPath}/settings">
                            <i class="fas fa-user-circle me-3 opacity-50"></i>My Profile
                        </a>
                    </li>
                    <li><hr class="dropdown-divider my-1"></li>
                    <li>
                        <a class="dropdown-item rounded-2 py-2 text-danger"
                           href="${pageContext.request.contextPath}/logout">
                            <i class="fas fa-sign-out-alt me-3 opacity-50"></i>Logout
                        </a>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</nav>

<%-- ── Toast Notification Area ── --%>
<div id="toast-container">
    <c:if test="${not empty param.success}">
        <div class="toast success" role="alert">
            <i class="fas fa-check-circle me-3" style="color:var(--success);"></i>
            <span class="small"><c:out value="${param.success}" /></span>
        </div>
    </c:if>
    <c:if test="${not empty param.error}">
        <div class="toast error" role="alert">
            <i class="fas fa-exclamation-circle me-3" style="color:var(--danger);"></i>
            <span class="small"><c:out value="${param.error}" /></span>
        </div>
    </c:if>
    <c:if test="${not empty param.warning}">
        <div class="toast warning" role="alert">
            <i class="fas fa-exclamation-triangle me-3" style="color:var(--warning);"></i>
            <span class="small"><c:out value="${param.warning}" /></span>
        </div>
    </c:if>
    <c:if test="${not empty param.toast}">
        <div class="toast info" id="special-toast" role="alert">
            <i class="fas fa-info-circle me-3" style="color:var(--primary);"></i>
            <span class="small" id="toast-msg"></span>
        </div>
    </c:if>
</div>

<%-- Global loading spinner --%>
<div class="spinner-overlay" id="global-spinner">
    <div class="spinner"></div>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function () {
        // Handle toast messages from URL params
        const urlParams = new URLSearchParams(window.location.search);
        const toastType = urlParams.get('toast');
        if (toastType) {
            const msgEl = document.getElementById('toast-msg');
            if (msgEl) {
                const msgs = {
                    booking_success: 'Booking successful! Confirmation sent.',
                    profile_updated: 'Profile updated successfully.',
                    password_reset_sent: 'Password reset link sent to your email.'
                };
                msgEl.innerText = msgs[toastType] || 'Action completed successfully.';
            }
        }

        // Auto-dismiss toasts after 5s
        document.querySelectorAll('#toast-container .toast').forEach(function (t) {
            setTimeout(function () {
                t.style.transition = 'all 0.4s ease';
                t.style.opacity = '0';
                t.style.transform = 'translateX(110%)';
                setTimeout(function () { t.remove(); }, 400);
            }, 5000);
        });
    });

    // Global spinner helper
    function showSpinner() {
        document.getElementById('global-spinner').style.display = 'flex';
    }
    function hideSpinner() {
        document.getElementById('global-spinner').style.display = 'none';
    }

    // Confirm action helper
    document.addEventListener('click', function (e) {
        const t = e.target.closest('[data-confirm]');
        if (t && !confirm(t.getAttribute('data-confirm'))) {
            e.preventDefault();
            e.stopImmediatePropagation();
        }
    });
</script>