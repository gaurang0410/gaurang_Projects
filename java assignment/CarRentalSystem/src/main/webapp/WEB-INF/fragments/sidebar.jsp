<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<nav id="sidebar">
    <%-- Brand --%>
    <div class="sidebar-brand">
        <div class="brand-icon">
            <i class="fas fa-car-side"></i>
        </div>
        <div>
            <h5>CarRental</h5>
            <span>Enterprise Suite</span>
        </div>
    </div>

    <div class="px-1 flex-grow-1" style="overflow-y:auto;">
        <ul class="nav flex-column list-unstyled">

            <%-- Overview --%>
            <li>
                <a href="${pageContext.request.contextPath}/${loggedUser.role == 'ADMIN' ? 'admin/dashboard' : 'customer/dashboard'}"
                   class="nav-link ${fn:contains(pageContext.request.requestURI, '/dashboard') ? 'active' : ''}">
                    <i class="fas fa-th-large"></i>
                    <span>Overview</span>
                </a>
            </li>

            <c:choose>
                <c:when test="${loggedUser.role == 'ADMIN'}">
                    <li class="nav-section-label">Fleet Management</li>
                    <li>
                        <a href="${pageContext.request.contextPath}/admin/cars"
                           class="nav-link ${fn:contains(pageContext.request.requestURI, '/viewCars') || fn:contains(pageContext.request.requestURI, '/admin/cars') ? 'active' : ''}">
                            <i class="fas fa-car"></i>
                            <span>Fleet Registry</span>
                        </a>
                    </li>

                    <li class="nav-section-label">Operations</li>
                    <li>
                        <a href="${pageContext.request.contextPath}/admin/bookings"
                           class="nav-link ${fn:contains(pageContext.request.requestURI, '/manageBookings') || fn:contains(pageContext.request.requestURI, '/admin/bookings') ? 'active' : ''}">
                            <i class="fas fa-calendar-check"></i>
                            <span>Reservations</span>
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/admin/customers"
                           class="nav-link ${fn:contains(pageContext.request.requestURI, '/manageCustomers') || fn:contains(pageContext.request.requestURI, '/admin/customers') ? 'active' : ''}">
                            <i class="fas fa-users"></i>
                            <span>Customers</span>
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/admin/payments"
                           class="nav-link ${fn:contains(pageContext.request.requestURI, '/managePayments') || fn:contains(pageContext.request.requestURI, '/admin/payments') ? 'active' : ''}">
                            <i class="fas fa-credit-card"></i>
                            <span>Transactions</span>
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/admin/reviews"
                           class="nav-link ${fn:contains(pageContext.request.requestURI, '/manageReviews') || fn:contains(pageContext.request.requestURI, '/admin/reviews') ? 'active' : ''}">
                            <i class="fas fa-star"></i>
                            <span>Reviews</span>
                        </a>
                    </li>
                </c:when>
                <c:otherwise>
                    <li class="nav-section-label">Services</li>
                    <li>
                        <a href="${pageContext.request.contextPath}/customer/viewCars"
                           class="nav-link ${fn:contains(pageContext.request.requestURI, '/customer/viewCars') ? 'active' : ''}">
                            <i class="fas fa-search"></i>
                            <span>Browse Fleet</span>
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/customer/bookings"
                           class="nav-link ${fn:contains(pageContext.request.requestURI, '/myBookings') || fn:contains(pageContext.request.requestURI, '/customer/bookings') ? 'active' : ''}">
                            <i class="fas fa-calendar-alt"></i>
                            <span>My Rentals</span>
                        </a>
                    </li>
                </c:otherwise>
            </c:choose>

        </ul>
    </div>

    <%-- Footer links --%>
    <div class="sidebar-footer px-1">
        <ul class="nav flex-column list-unstyled">
            <li>
                <a href="${pageContext.request.contextPath}/settings"
                   class="nav-link ${fn:contains(pageContext.request.requestURI, '/settings') ? 'active' : ''}">
                    <i class="fas fa-cog"></i>
                    <span>Settings</span>
                </a>
            </li>
            <li>
                <a href="#" class="nav-link text-danger" data-bs-toggle="modal" data-bs-target="#logoutModal">
                    <i class="fas fa-sign-out-alt"></i>
                    <span>Sign Out</span>
                </a>
            </li>
        </ul>
    </div>
</nav>

<%-- Logout Confirmation Modal --%>
<div class="modal fade" id="logoutModal" tabindex="-1" aria-labelledby="logoutModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow-lg rounded-4 overflow-hidden">
            <div class="modal-body p-5 text-center">
                <div class="mx-auto mb-4 bg-danger bg-opacity-10 rounded-circle d-flex align-items-center justify-content-center" style="width:80px;height:80px;">
                    <i class="fas fa-sign-out-alt fa-3x text-danger"></i>
                </div>
                <h4 class="fw-bold mb-3" id="logoutModalLabel">Ready to leave?</h4>
                <p class="text-muted mb-4">Are you sure you want to log out of your session? You will need to sign in again to access your account.</p>
                <div class="d-flex justify-content-center gap-3">
                    <button type="button" class="btn btn-light px-4 py-2 border rounded-pill fw-semibold" data-bs-dismiss="modal">Stay Logged In</button>
                    <a href="${pageContext.request.contextPath}/logout" class="btn btn-danger px-4 py-2 rounded-pill fw-bold shadow-sm">Sign Out</a>
                </div>
            </div>
        </div>
    </div>
</div>
<div class="sidebar-overlay" id="sidebarOverlay"
     onclick="document.getElementById('sidebar').classList.remove('open'); this.classList.remove('active');"></div>
