<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<% utils.CSRFUtil.ensureToken(session); %>
<c:set var="pageTitle" value="Manage Bookings - Admin Panel" scope="request" />
<jsp:include page="WEB-INF/fragments/layout-top.jsp" />

<!-- Page Header -->
<div class="d-flex justify-content-between align-items-center mb-4">
    <div>
        <h2 class="fw-bold mb-1">Reservation Registry</h2>
        <p class="text-muted small mb-0">Manage customer booking requests and rentals.</p>
    </div>
</div>

<!-- Filters & Search -->
<div class="card border-0 shadow-sm rounded-4 mb-4">
    <div class="card-body p-3">
        <form action="${pageContext.request.contextPath}/admin/bookings" method="GET" class="row g-3">
            <div class="col-md-5">
                <div class="input-group">
                    <span class="input-group-text bg-light border-end-0"><i class="fas fa-search text-muted"></i></span>
                    <input type="text" name="search" class="form-control bg-light border-start-0 ps-0" 
                           placeholder="Search by ID, Customer or Car..." value="${fn:escapeXml(param.search)}">
                </div>
            </div>
            <div class="col-md-3">
                <select name="status" class="form-select bg-light border-0">
                    <option value="">All Statuses</option>
                    <option value="PENDING_APPROVAL" ${param.status == 'PENDING_APPROVAL' ? 'selected' : ''}>Pending Approval</option>
                    <option value="APPROVED" ${param.status == 'APPROVED' ? 'selected' : ''}>Approved</option>
                    <option value="REJECTED" ${param.status == 'REJECTED' ? 'selected' : ''}>Rejected</option>
                    <option value="PAYMENT_PENDING" ${param.status == 'PAYMENT_PENDING' ? 'selected' : ''}>Payment Pending</option>
                    <option value="CONFIRMED" ${param.status == 'CONFIRMED' ? 'selected' : ''}>Confirmed</option>
                    <option value="CANCELLED" ${param.status == 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
                    <option value="COMPLETED" ${param.status == 'COMPLETED' ? 'selected' : ''}>Completed</option>
                </select>
            </div>
            <div class="col-md-4 d-flex gap-2">
                <button type="submit" class="btn btn-primary w-100">Apply Filter</button>
                <a href="${pageContext.request.contextPath}/admin/bookings" class="btn btn-light border w-100">Reset</a>
            </div>
        </form>
    </div>
</div>

<!-- Error / Success Messages -->
<c:if test="${not empty param.error}">
    <div class="alert alert-danger border-0 rounded-3 shadow-sm mb-4">
        <i class="fas fa-exclamation-circle me-2"></i> ${fn:escapeXml(param.error)}
    </div>
</c:if>
<c:if test="${not empty param.success}">
    <div class="alert alert-success border-0 rounded-3 shadow-sm mb-4">
        <i class="fas fa-check-circle me-2"></i> ${fn:escapeXml(param.success)}
    </div>
</c:if>

<!-- Table Card -->
<div class="card border-0 shadow-sm rounded-4 overflow-hidden">
    <div class="table-responsive">
        <table class="table table-hover align-middle mb-0">
            <thead class="bg-light">
                <tr class="small text-uppercase">
                    <th class="ps-4 border-0 py-3">Reference</th>
                    <th class="border-0 py-3">Client</th>
                    <th class="border-0 py-3">Vehicle</th>
                    <th class="border-0 py-3">Schedule</th>
                    <th class="border-0 py-3">Total Amount</th>
                    <th class="border-0 py-3 text-center">Status</th>
                    <th class="pe-4 text-end border-0 py-3">Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${not empty allBookings}">
                        <c:forEach var="b" items="${allBookings}">
                            <tr>
                                <td class="ps-4">
                                     <span class="fw-bold text-primary">#<c:out value="${b.id}" /></span>
                                </td>
                                <td>
                                    <div class="fw-bold small"><c:out value="${b.customerName}" /></div>
                                    <div class="extra-small text-muted">ID: <c:out value="${b.customerId}" /></div>
                                </td>
                                <td>
                                    <div class="small fw-bold"><c:out value="${b.carBrand}" /> <c:out value="${b.carModel}" /></div>
                                    <div class="extra-small text-muted text-uppercase tracking-tighter"><c:out value="${b.pickupLocation}" /> &rarr; <c:out value="${b.dropLocation}" /></div>
                                </td>
                                <td>
                                    <div class="extra-small">
                                        <div class="text-success mb-1"><i class="far fa-calendar-check me-1"></i><c:out value="${b.pickupDate}" /></div>
                                        <div class="text-danger"><i class="far fa-calendar-times me-1"></i><c:out value="${b.returnDate}" /></div>
                                    </div>
                                </td>
                                <td>
                                    <div class="fw-bold text-primary">&#8377;<c:out value="${fn:substringBefore(b.totalAmount, '.')}" /></div>
                                </td>
                                <td class="text-center">
                                    <c:choose>
                                        <c:when test="${b.status == 'PENDING_APPROVAL' || b.status == 'PENDING'}">
                                            <span class="badge bg-warning bg-opacity-10 text-warning rounded-pill px-3">Pending</span>
                                        </c:when>
                                        <c:when test="${b.status == 'APPROVED'}">
                                            <span class="badge bg-info bg-opacity-10 text-info rounded-pill px-3">Approved</span>
                                        </c:when>
                                        <c:when test="${b.status == 'REJECTED'}">
                                            <span class="badge bg-danger bg-opacity-10 text-danger rounded-pill px-3">Rejected</span>
                                        </c:when>
                                        <c:when test="${b.status == 'PAYMENT_PENDING'}">
                                            <span class="badge bg-warning bg-opacity-10 text-warning rounded-pill px-3">Payment Pending</span>
                                        </c:when>
                                        <c:when test="${b.status == 'CONFIRMED'}">
                                            <span class="badge bg-success bg-opacity-10 text-success rounded-pill px-3">Confirmed</span>
                                        </c:when>
                                        <c:when test="${b.status == 'COMPLETED'}">
                                            <span class="badge bg-secondary bg-opacity-10 text-secondary rounded-pill px-3">Completed</span>
                                        </c:when>
                                        <c:when test="${b.status == 'CANCELLED'}">
                                            <span class="badge bg-danger bg-opacity-10 text-danger rounded-pill px-3">Cancelled</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-dark bg-opacity-10 text-dark rounded-pill px-3"><c:out value="${b.status}" /></span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="pe-4 text-end">
                                    <div class="dropdown">
                                        <button class="btn btn-light btn-sm rounded-circle border shadow-sm" type="button" data-bs-toggle="dropdown">
                                            <i class="fas fa-ellipsis-v extra-small"></i>
                                        </button>
                                        <ul class="dropdown-menu dropdown-menu-end shadow-lg border-0 p-2">
                                            
                                            <li><a class="dropdown-item rounded-2 py-2" href="${pageContext.request.contextPath}/customer/invoice?bookingId=${b.id}"><i class="fas fa-file-invoice me-3 text-primary"></i>View Invoice</a></li>
                                            
                                            <li><hr class="dropdown-divider"></li>
                                            
                                            <!-- Action: Approve / Reject -->
                                            <c:if test="${b.status == 'PENDING_APPROVAL' || b.status == 'PENDING'}">
                                                <li>
                                                    <form action="${pageContext.request.contextPath}/admin/updateBookingStatus" method="post">
                                                        <input type="hidden" name="csrf_token" value="${sessionScope.csrf_token}"/>
                                                        <input type="hidden" name="bookingId" value="${b.id}">
                                                        <input type="hidden" name="status" value="APPROVED">
                                                        <button type="submit" class="dropdown-item rounded-2 py-2"><i class="fas fa-check-circle me-3 text-success"></i>Approve Request</button>
                                                    </form>
                                                </li>
                                                <li>
                                                    <form action="${pageContext.request.contextPath}/admin/updateBookingStatus" method="post">
                                                        <input type="hidden" name="csrf_token" value="${sessionScope.csrf_token}"/>
                                                        <input type="hidden" name="bookingId" value="${b.id}">
                                                        <input type="hidden" name="status" value="REJECTED">
                                                        <button type="submit" class="dropdown-item rounded-2 py-2 text-danger" onclick="return confirm('Reject this booking request?')"><i class="fas fa-times-circle me-3 opacity-50"></i>Reject Request</button>
                                                    </form>
                                                </li>
                                            </c:if>

                                            <!-- Action: Confirm (legacy) or Cancel -->
                                            <c:if test="${b.status != 'CANCELLED' && b.status != 'COMPLETED' && b.status != 'REJECTED'}">
                                                <c:if test="${b.status == 'APPROVED' || b.status == 'PAYMENT_PENDING'}">
                                                    <li>
                                                        <form action="${pageContext.request.contextPath}/admin/updateBookingStatus" method="post">
                                                            <input type="hidden" name="csrf_token" value="${sessionScope.csrf_token}"/>
                                                            <input type="hidden" name="bookingId" value="${b.id}">
                                                            <input type="hidden" name="status" value="CONFIRMED">
                                                            <button type="submit" class="dropdown-item rounded-2 py-2"><i class="fas fa-check-double me-3 text-success"></i>Mark Confirmed</button>
                                                        </form>
                                                    </li>
                                                </c:if>
                                                <li>
                                                    <form action="${pageContext.request.contextPath}/admin/updateBookingStatus" method="post">
                                                        <input type="hidden" name="csrf_token" value="${sessionScope.csrf_token}"/>
                                                        <input type="hidden" name="bookingId" value="${b.id}">
                                                        <input type="hidden" name="status" value="CANCELLED">
                                                        <button type="submit" class="dropdown-item rounded-2 py-2 text-danger" onclick="return confirm('Cancel this reservation completely?')"><i class="fas fa-ban me-3 opacity-50"></i>Cancel Booking</button>
                                                    </form>
                                                </li>
                                            </c:if>

                                            <!-- Action: Mark Completed -->
                                            <c:if test="${b.status == 'CONFIRMED'}">
                                                <li>
                                                    <form action="${pageContext.request.contextPath}/admin/updateBookingStatus" method="post">
                                                        <input type="hidden" name="csrf_token" value="${sessionScope.csrf_token}"/>
                                                        <input type="hidden" name="bookingId" value="${b.id}">
                                                        <input type="hidden" name="status" value="COMPLETED">
                                                        <button type="submit" class="dropdown-item rounded-2 py-2"><i class="fas fa-flag-checkered me-3 text-secondary"></i>Mark Completed</button>
                                                    </form>
                                                </li>
                                            </c:if>
                                        </ul>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="7" class="text-center py-5">
                                <div class="opacity-25 mb-3"><i class="fas fa-calendar-times fa-4x"></i></div>
                                <h5 class="text-muted fw-bold">No reservations found</h5>
                                <p class="text-muted small">Try adjusting your filters or search terms.</p>
                            </td>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>
</div>
<jsp:include page="WEB-INF/fragments/layout-bottom.jsp" />