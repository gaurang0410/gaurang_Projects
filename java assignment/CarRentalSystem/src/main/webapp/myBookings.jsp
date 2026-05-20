<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%
    if (session.getAttribute("loggedUser") == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
%>
<c:set var="pageTitle" value="My Bookings - CarRental" scope="request" />
<jsp:include page="WEB-INF/fragments/layout-top.jsp" />
                <!-- Page Header -->
                <div class="mb-4">
                    <h2 class="fw-bold mb-1">My Bookings</h2>
                    <p class="text-muted small mb-0">Manage your active reservations and rental history.</p>
                </div>

                <div class="row g-4">
                    <c:choose>
                        <c:when test="${not empty userBookings}">
                            <c:forEach var="b" items="${userBookings}">
                                <div class="col-md-6 col-xl-4">
                                    <div class="card border-0 shadow-sm rounded-4 overflow-hidden h-100">
                                        <div class="p-4 border-bottom">
                                            <div class="d-flex justify-content-between align-items-center mb-3">
                                                <span class="badge border fw-semibold px-3">#<c:out value="${b.id}" /></span>
                                                <c:choose>
                                                    <c:when test="${b.status == 'CONFIRMED'}"><span class="badge bg-success bg-opacity-10 text-success rounded-pill px-3">Confirmed</span></c:when>
                                                    <c:when test="${b.status == 'PENDING'}"><span class="badge bg-warning bg-opacity-10 text-warning rounded-pill px-3">Pending Payment</span></c:when>
                                                    <c:when test="${b.status == 'CANCELLED'}"><span class="badge bg-danger bg-opacity-10 text-danger rounded-pill px-3">Cancelled</span></c:when>
                                                    <c:when test="${b.status == 'COMPLETED'}"><span class="badge bg-primary bg-opacity-10 text-primary rounded-pill px-3">Completed</span></c:when>
                                                    <c:otherwise><span class="badge bg-secondary bg-opacity-10 text-secondary rounded-pill px-3"><c:out value="${b.status}" /></span></c:otherwise>
                                                </c:choose>
                                            </div>
                                            <h5 class="fw-bold mb-0"><c:out value="${b.carBrand}" /> <c:out value="${b.carModel}" /></h5>
                                        </div>
                                        
                                        <div class="card-body p-4">
                                            <div class="mb-4">
                                                <div class="extra-small text-muted text-uppercase fw-bold tracking-wider mb-2">Duration</div>
                                                <div class="d-flex align-items-center gap-3">
                                                    <div class="bg-light p-2 rounded-2 text-center" style="min-width: 80px;">
                                                        <div class="extra-small text-muted">Pickup</div>
                                                        <div class="fw-bold small"><c:out value="${b.pickupDate}" /></div>
                                                    </div>
                                                    <i class="fas fa-long-arrow-alt-right text-muted"></i>
                                                    <div class="bg-light p-2 rounded-2 text-center" style="min-width: 80px;">
                                                        <div class="extra-small text-muted">Return</div>
                                                        <div class="fw-bold small"><c:out value="${b.returnDate}" /></div>
                                                    </div>
                                                </div>
                                            </div>

                                            <div class="mb-4">
                                                <div class="extra-small text-muted text-uppercase fw-bold tracking-wider mb-2">Route</div>
                                                <div class="small text-secondary">
                                                    <i class="fas fa-map-marker-alt text-danger me-2"></i><c:out value="${b.pickupLocation}" />
                                                    <span class="mx-2 text-muted">•</span>
                                                    <i class="fas fa-flag-checkered text-primary me-2"></i><c:out value="${b.dropLocation}" />
                                                </div>
                                            </div>

                                            <div class="pt-3 border-top d-flex justify-content-between align-items-center">
                                                <div>
                                                    <div class="extra-small text-muted">Amount Paid</div>
                                                    <div class="fw-bold text-primary fs-5">₹<c:out value="${fn:substringBefore(b.totalAmount, '.')}" /></div>
                                                </div>
                                                <div class="d-flex gap-2">
                                                    <a href="${pageContext.request.contextPath}/customer/invoice?bookingId=${b.id}" class="btn btn-light btn-sm rounded-3 border" title="Download Invoice">
                                                        <i class="fas fa-file-pdf"></i>
                                                    </a>
                                                    <c:if test="${b.status == 'PENDING'}">
                                                        <a href="${pageContext.request.contextPath}/customer/payment?bookingId=${b.id}" class="btn btn-primary btn-sm rounded-3 px-3 shadow-sm">Pay Now</a>
                                                    </c:if>
                                                    <c:if test="${b.status == 'COMPLETED'}">
                                                        <button class="btn btn-outline-primary btn-sm rounded-3 px-3" data-bs-toggle="modal" data-bs-target="#reviewModal${b.id}">Rate Trip</button>
                                                    </c:if>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Review Modal -->
                                    <c:if test="${b.status == 'COMPLETED'}">
                                        <div class="modal fade" id="reviewModal${b.id}" tabindex="-1">
                                            <div class="modal-dialog modal-dialog-centered">
                                                <div class="modal-content border-0 shadow-lg rounded-4">
                                                    <div class="modal-header border-0 p-4 pb-0">
                                                        <h5 class="fw-bold">Rate your journey</h5>
                                                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                                    </div>
                                                    <form action="${pageContext.request.contextPath}/customer/review" method="POST">
                                                        <input type="hidden" name="csrf_token" value="${sessionScope.csrf_token}"/>
                                                        <input type="hidden" name="bookingId" value="${b.id}">
                                                        <div class="modal-body p-4">
                                                            <div class="mb-3 text-center">
                                                                <label class="form-label d-block">Overall Rating</label>
                                                                <div class="rating-stars h3 text-warning">
                                                                    <i class="far fa-star cursor-pointer" onclick="setRating(1, ${b.id})"></i>
                                                                    <i class="far fa-star cursor-pointer" onclick="setRating(2, ${b.id})"></i>
                                                                    <i class="far fa-star cursor-pointer" onclick="setRating(3, ${b.id})"></i>
                                                                    <i class="far fa-star cursor-pointer" onclick="setRating(4, ${b.id})"></i>
                                                                    <i class="far fa-star cursor-pointer" onclick="setRating(5, ${b.id})"></i>
                                                                </div>
                                                                <input type="hidden" name="rating" id="ratingInput${b.id}" required value="5">
                                                            </div>
                                                            <div class="mb-0">
                                                                <label class="form-label">Feedback (Optional)</label>
                                                                <textarea name="comment" class="form-control rounded-3" rows="3" placeholder="How was the vehicle and our service?"></textarea>
                                                            </div>
                                                        </div>
                                                        <div class="modal-footer border-0 p-4 pt-0">
                                                            <button type="submit" class="btn btn-primary w-100 py-2 rounded-3 shadow-sm">Submit Review</button>
                                                        </div>
                                                    </form>
                                                </div>
                                            </div>
                                        </div>
                                    </c:if>
                                </div>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <div class="col-12 text-center py-5">
                                <div class="opacity-25 mb-4"><i class="fas fa-calendar-alt fa-5x"></i></div>
                                <h4 class="fw-bold">No bookings yet</h4>
                                <p class="text-muted">You haven't rented any vehicles yet. Ready to hit the road?</p>
                                <a href="${pageContext.request.contextPath}/customer/dashboard" class="btn btn-primary px-5 rounded-pill shadow-sm mt-3">Explore Fleet</a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div><c:set var="extraScripts" scope="request">
    <script>
        function setRating(val, bid) {
            const stars = document.querySelectorAll('#reviewModal' + bid + ' .rating-stars i');
            document.getElementById('ratingInput' + bid).value = val;
            stars.forEach((s, idx) => {
                if (idx < val) {
                    s.classList.replace('far', 'fas');
                } else {
                    s.classList.replace('fas', 'far');
                }
            });
        }
    </script>
    <style>
        .cursor-pointer { cursor: pointer; }
        .rating-stars i { transition: transform 0.1s ease; }
        .rating-stars i:hover { transform: scale(1.2); }
    </style>
</c:set>
<jsp:include page="WEB-INF/fragments/layout-bottom.jsp" />