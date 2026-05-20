<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%
    if (session.getAttribute("loggedUser") == null) {
        response.sendRedirect("login");
        return;
    }
%>
<c:set var="pageTitle" value="Fleet Management - CarRental" scope="request" />
<jsp:include page="WEB-INF/fragments/layout-top.jsp" />
                <!-- Page Header -->
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <div>
                        <h2 class="fw-bold mb-1">Fleet Management</h2>
                        <p class="text-muted small mb-0">Monitor and manage all vehicles in your rental fleet.</p>
                    </div>
                    <a href="addCar" class="btn btn-primary px-4 py-2 shadow-sm">
                        <i class="fas fa-plus me-2"></i>Add New Car
                    </a>
                </div>

                <!-- Table Card -->
                <div class="card border-0 shadow-sm rounded-4 overflow-hidden">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0">
                            <thead class="bg-light">
                                <tr>
                                    <th class="ps-4 border-0 small text-uppercase py-3">Vehicle Details</th>
                                    <th class="border-0 small text-uppercase py-3">Category</th>
                                    <th class="border-0 small text-uppercase py-3">Specifications</th>
                                    <th class="border-0 small text-uppercase py-3">Location</th>
                                    <th class="border-0 small text-uppercase py-3">Daily Rate</th>
                                    <th class="border-0 small text-uppercase py-3">Status</th>
                                    <th class="pe-4 text-end border-0 small text-uppercase py-3">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${not empty cars}">
                                        <c:forEach var="car" items="${cars}">
                                            <tr>
                                                <td class="ps-4 py-3">
                                                    <div class="d-flex align-items-center">
                                                        <c:choose>
                                                            <c:when test="${fn:startsWith(car.imageUrl, 'http://') || fn:startsWith(car.imageUrl, 'https://')}">
                                                                <img src="<c:out value='${car.imageUrl}'/>"
                                                                     alt="<c:out value='${car.brand}'/> <c:out value='${car.model}'/>"
                                                                     class="rounded-3 shadow-sm me-3"
                                                                     width="70" height="45" style="object-fit:cover"
                                                                     onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/images/default-car.svg'">
                                                            </c:when>
                                                            <c:otherwise>
                                                                <img src="${pageContext.request.contextPath}/${car.imageUrl}"
                                                                     alt="<c:out value='${car.brand}'/> <c:out value='${car.model}'/>"
                                                                     class="rounded-3 shadow-sm me-3"
                                                                     width="70" height="45" style="object-fit:cover"
                                                                     onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/images/default-car.svg'">
                                                            </c:otherwise>
                                                        </c:choose>
                                                        <div>
                                                            <div class="fw-bold"><c:out value="${car.brand}" /></div>
                                                            <div class="extra-small text-muted text-uppercase tracking-wider"><c:out value="${car.model}" /></div>
                                                        </div>
                                                    </div>
                                                </td>
                                                <td>
                                                    <span class="badge border fw-medium small"><c:out value="${car.category}" /></span>
                                                </td>
                                                <td>
                                                    <div class="extra-small text-muted mb-1">
                                                        <i class="fas fa-gas-pump me-1"></i><c:out value="${car.fuelType}" />
                                                    </div>
                                                    <div class="extra-small text-muted">
                                                        <i class="fas fa-cog me-1"></i>Automatic
                                                    </div>
                                                </td>
                                                <td>
                                                    <div class="small text-muted">
                                                        <i class="fas fa-map-marker-alt me-1 text-danger"></i><c:out value="${car.location}" />
                                                    </div>
                                                </td>
                                                <td>
                                                    <div class="fw-bold">₹<c:out value="${fn:substringBefore(car.pricePerDay, '.')}" /></div>
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${car.status == 'AVAILABLE'}">
                                                            <span class="badge bg-success bg-opacity-10 text-success rounded-pill px-3">Available</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge bg-danger bg-opacity-10 text-danger rounded-pill px-3"><c:out value="${car.status}" /></span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td class="pe-4 text-end">
                                                    <div class="dropdown">
                                                        <button class="btn btn-light btn-sm rounded-circle shadow-sm" type="button" data-bs-toggle="dropdown" style="width: 32px; height: 32px;">
                                                            <i class="fas fa-ellipsis-v extra-small"></i>
                                                        </button>
                                                        <ul class="dropdown-menu dropdown-menu-end shadow-lg border-0 p-2">
                                                            <li><a class="dropdown-item rounded-2 py-2" href="${pageContext.request.contextPath}/admin/editCar?id=${car.id}"><i class="fas fa-edit me-3 text-primary"></i>Edit Car</a></li>
                                                            <li><hr class="dropdown-divider"></li>
                                                            <li><a class="dropdown-item rounded-2 py-2 text-danger" href="deleteCar?id=${car.id}" onclick="return confirm('Are you sure you want to delete this car?')"><i class="fas fa-trash me-3 opacity-50"></i>Delete Car</a></li>
                                                        </ul>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr>
                                            <td colspan="7" class="text-center py-5">
                                                <div class="opacity-25 mb-3"><i class="fas fa-car fa-4x"></i></div>
                                                <h5 class="text-muted fw-bold">No cars found in fleet</h5>
                                                <p class="text-muted small">Start by adding a new vehicle to your collection.</p>
                                                <a href="addCar" class="btn btn-primary btn-sm mt-2 px-4">Add First Car</a>
                                            </td>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div><jsp:include page="WEB-INF/fragments/layout-bottom.jsp" />