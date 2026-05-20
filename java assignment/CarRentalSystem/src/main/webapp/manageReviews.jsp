<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<% utils.CSRFUtil.ensureToken(session); %>
<c:set var="pageTitle" value="Review Moderation - CarRental" scope="request" />
<jsp:include page="WEB-INF/fragments/layout-top.jsp" />
                <!-- Page Header -->
                <div class="mb-4">
                    <h2 class="fw-bold mb-1">Feedback &amp; Moderation</h2>
                    <p class="text-muted small mb-0">Manage customer ratings and vehicle reviews.</p>
                </div>

                <!-- Filter Tabs -->
                <div class="mb-4">
                    <div class="btn-group p-1 rounded-3 shadow-sm" style="background:var(--bg-card);">
                        <button class="btn btn-sm btn-primary rounded-2 px-4" onclick="filterReviews('ALL', this)">All Reviews</button>
                        <button class="btn btn-sm btn-light rounded-2 px-4 border-0" onclick="filterReviews('PENDING', this)">Pending</button>
                        <button class="btn btn-sm btn-light rounded-2 px-4 border-0" onclick="filterReviews('APPROVED', this)">Approved</button>
                    </div>
                </div>

                <div class="row g-4" id="reviewsContainer">
                    <c:choose>
                        <c:when test="${not empty allReviews}">
                            <c:forEach var="r" items="${allReviews}">
                                <div class="col-md-6 col-xl-4 review-card" data-status="${r.status}">
                                    <div class="card border-0 shadow-sm rounded-4 h-100 overflow-hidden">
                                        <div class="card-body p-4">
                                            <div class="d-flex justify-content-between align-items-start mb-3">
                                                <div class="d-flex align-items-center">
                                                    <div class="rounded-circle d-flex align-items-center justify-content-center me-3" style="width:42px;height:42px;background:var(--bg-elevated);">
                                                        <i class="fas fa-user text-muted small"></i>
                                                    </div>
                                                    <div>
                                                        <div class="fw-bold small"><c:out value="${r.customerName}" /></div>
                                                        <div class="extra-small text-muted"><c:out value="${r.carDetails}" /></div>
                                                    </div>
                                                </div>
                                                <div class="text-warning extra-small">
                                                    <c:forEach begin="1" end="5" var="i">
                                                        <i class="${i <= r.rating ? 'fas' : 'far'} fa-star"></i>
                                                    </c:forEach>
                                                </div>
                                            </div>
                                            
                                            <div class="p-3 rounded-3 mb-4" style="background:var(--bg-elevated);">
                                                <p class="text-secondary small mb-0 italic" style="min-height: 50px;">
                                                    <i class="fas fa-quote-left me-2 opacity-25"></i>
                                                    <c:out value="${r.comment}" />
                                                </p>
                                            </div>

                                            <div class="d-flex justify-content-between align-items-center pt-3 border-top">
                                                <c:choose>
                                                    <c:when test="${r.status == 'APPROVED'}">
                                                        <span class="badge bg-success bg-opacity-10 text-success rounded-pill px-3">Approved</span>
                                                    </c:when>
                                                    <c:when test="${r.status == 'PENDING'}">
                                                        <span class="badge bg-warning bg-opacity-10 text-warning rounded-pill px-3">Pending</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-danger bg-opacity-10 text-danger rounded-pill px-3"><c:out value="${r.status}" /></span>
                                                    </c:otherwise>
                                                </c:choose>
                                                
                                                <div class="dropdown">
                                                    <button class="btn btn-light btn-sm rounded-circle border shadow-sm" type="button" data-bs-toggle="dropdown">
                                                        <i class="fas fa-ellipsis-h extra-small"></i>
                                                    </button>
                                                    <ul class="dropdown-menu dropdown-menu-end shadow-lg border-0 p-2">
                                                        <c:if test="${r.status == 'PENDING'}">
                                                            <li>
                                                                <form action="${pageContext.request.contextPath}/admin/moderateReview" method="post">
                                                                    <input type="hidden" name="csrf_token" value="${sessionScope.csrf_token}"/>
                                                                    <input type="hidden" name="reviewId" value="${r.id}">
                                                                    <input type="hidden" name="action" value="approve">
                                                                    <button type="submit" class="dropdown-item rounded-2 py-2 text-success"><i class="fas fa-check-circle me-3 opacity-50"></i>Approve</button>
                                                                </form>
                                                            </li>
                                                            <li>
                                                                <form action="${pageContext.request.contextPath}/admin/moderateReview" method="post">
                                                                    <input type="hidden" name="csrf_token" value="${sessionScope.csrf_token}"/>
                                                                    <input type="hidden" name="reviewId" value="${r.id}">
                                                                    <input type="hidden" name="action" value="reject">
                                                                    <button type="submit" class="dropdown-item rounded-2 py-2 text-warning"><i class="fas fa-times-circle me-3 opacity-50"></i>Reject</button>
                                                                </form>
                                                            </li>
                                                            <li><hr class="dropdown-divider"></li>
                                                        </c:if>
                                                        <li>
                                                            <form action="${pageContext.request.contextPath}/admin/moderateReview" method="post" onsubmit="return confirm('Permanently delete this review?')">
                                                                <input type="hidden" name="csrf_token" value="${sessionScope.csrf_token}"/>
                                                                <input type="hidden" name="reviewId" value="${r.id}">
                                                                <input type="hidden" name="action" value="delete">
                                                                <button type="submit" class="dropdown-item rounded-2 py-2 text-danger"><i class="fas fa-trash-alt me-3 opacity-50"></i>Delete</button>
                                                            </form>
                                                        </li>
                                                    </ul>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <div class="col-12 text-center py-5">
                                <div class="opacity-25 mb-4"><i class="fas fa-comments fa-5x"></i></div>
                                <h4 class="fw-bold">No reviews yet</h4>
                                <p class="text-muted">Customer feedback will appear here once submitted.</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div><c:set var="extraScripts" scope="request">
    <script>
        function filterReviews(status, btn) {
            // Update button styles
            const buttons = document.querySelectorAll('.btn-group button');
            buttons.forEach(b => {
                b.classList.remove('btn-primary');
                b.classList.add('btn-light', 'border-0');
            });
            btn.classList.add('btn-primary');
            btn.classList.remove('btn-light', 'border-0');

            // Filter cards
            const cards = document.querySelectorAll('.review-card');
            cards.forEach(card => {
                if (status === 'ALL' || card.getAttribute('data-status') === status) {
                    card.style.display = 'block';
                } else {
                    card.style.display = 'none';
                }
            });
        }
    </script>
</c:set>
<jsp:include page="WEB-INF/fragments/layout-bottom.jsp" />