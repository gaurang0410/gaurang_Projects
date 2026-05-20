<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<% utils.CSRFUtil.ensureToken(session); %>
<%@ page import="model.Car" %>
<%
    Car car = (Car) request.getAttribute("car");
    if(car == null){
        response.sendRedirect(request.getContextPath() + "/admin/cars");
        return;
    }
%>
<c:set var="pageTitle" value="Edit Car - Admin Panel" scope="request" />
<jsp:include page="WEB-INF/fragments/layout-top.jsp" />

                <!-- Page Header -->
                <div class="mb-4">
                    <h2 class="fw-bold mb-1">Edit Vehicle</h2>
                    <p class="text-muted small mb-0">Update vehicle details</p>
                </div>

            <div class="card shadow-sm border-0 rounded-4">
                <div class="card-body p-4">
                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="fas fa-exclamation-circle me-2"></i>
                            ${fn:escapeXml(errorMessage)}
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/admin/editCar" method="post" enctype="multipart/form-data">
                        <input type="hidden" name="csrf_token" value="${sessionScope.csrf_token}"/>
                        <input type="hidden" name="id" value="${fn:escapeXml(car.id)}">
                        
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Brand</label>
                                <input type="text" class="form-control" name="brand" value="${fn:escapeXml(car.brand)}" required>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Model</label>
                                <input type="text" class="form-control" name="model" value="${fn:escapeXml(car.model)}" required>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-md-3 mb-3">
                                <label class="form-label">Category</label>
                                <select class="form-select" name="category" required>
                                    <option value="Sedan" ${car.category == 'Sedan' ? 'selected' : ''}>Sedan</option>
                                    <option value="SUV" ${car.category == 'SUV' ? 'selected' : ''}>SUV</option>
                                    <option value="Hatchback" ${car.category == 'Hatchback' ? 'selected' : ''}>Hatchback</option>
                                    <option value="Luxury" ${car.category == 'Luxury' ? 'selected' : ''}>Luxury</option>
                                </select>
                            </div>
                            <div class="col-md-3 mb-3">
                                <label class="form-label">Fuel Type</label>
                                <select class="form-select" name="fuelType" required>
                                    <option value="Petrol" ${car.fuelType == 'Petrol' ? 'selected' : ''}>Petrol</option>
                                    <option value="Diesel" ${car.fuelType == 'Diesel' ? 'selected' : ''}>Diesel</option>
                                    <option value="Electric" ${car.fuelType == 'Electric' ? 'selected' : ''}>Electric</option>
                                    <option value="CNG" ${car.fuelType == 'CNG' ? 'selected' : ''}>CNG</option>
                                </select>
                            </div>
                            <div class="col-md-3 mb-3">
                                <label class="form-label">Price Per Day (₹)</label>
                                <input type="number" step="0.01" class="form-control" name="pricePerDay" value="${fn:escapeXml(car.pricePerDay)}" required>
                            </div>
                            <div class="col-md-3 mb-3">
                                <label class="form-label">Status</label>
                                <select class="form-select" name="status" required>
                                    <option value="AVAILABLE" ${car.status == 'AVAILABLE' ? 'selected' : ''}>Available</option>
                                    <option value="BOOKED" ${car.status == 'BOOKED' ? 'selected' : ''}>Booked</option>
                                </select>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Location</label>
                                <select class="form-select" name="location" required>
                                    <option value="Mumbai" ${car.location == 'Mumbai' ? 'selected' : ''}>Mumbai</option>
                                    <option value="Pune" ${car.location == 'Pune' ? 'selected' : ''}>Pune</option>
                                    <option value="Goa" ${car.location == 'Goa' ? 'selected' : ''}>Goa</option>
                                    <option value="Delhi" ${car.location == 'Delhi' ? 'selected' : ''}>Delhi</option>
                                    <option value="Bangalore" ${car.location == 'Bangalore' ? 'selected' : ''}>Bangalore</option>
                                </select>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Image URL</label>
                                <input type="url" class="form-control" name="imageUrl" value="${fn:escapeXml(car.imageUrl)}">
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Or Upload New Image</label>
                            <input type="file" class="form-control" name="imageFile" accept="image/*">
                        </div>
                        <div class="mb-4">
                            <c:choose>
                                <c:when test="${not empty car.imageUrl and (fn:startsWith(car.imageUrl,'http://') or fn:startsWith(car.imageUrl,'https://'))}">
                                    <img src="${fn:escapeXml(car.imageUrl)}" alt="Car image" width="120" class="rounded border" onerror="this.src='${pageContext.request.contextPath}/images/default-car.png'">
                                </c:when>
                                <c:otherwise>
                                    <img src="${pageContext.request.contextPath}/${fn:escapeXml(car.imageUrl)}" alt="Car image" width="120" class="rounded border" onerror="this.src='${pageContext.request.contextPath}/images/default-car.png'">
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <button type="submit" class="btn btn-primary"><i class="fas fa-save"></i> Update Car</button>
                        <a href="${pageContext.request.contextPath}/admin/cars" class="btn btn-secondary">Cancel</a>
                    </form>
                </div>
            </div><c:set var="extraScripts" scope="request">
    <script src="${pageContext.request.contextPath}/js/admin.js"></script>
</c:set>
<jsp:include page="WEB-INF/fragments/layout-bottom.jsp" />