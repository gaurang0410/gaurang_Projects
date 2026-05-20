<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<% utils.CSRFUtil.ensureToken(session); %>
<c:set var="pageTitle" value="Add Car - Admin Panel" scope="request" />
<jsp:include page="WEB-INF/fragments/layout-top.jsp" />

                <!-- Page Header -->
                <div class="mb-4">
                    <h2 class="fw-bold mb-1">Add Vehicle</h2>
                    <p class="text-muted small mb-0">Register a new vehicle into the fleet</p>
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

                    <form action="${pageContext.request.contextPath}/admin/addCar" method="post" enctype="multipart/form-data">
    <input type="hidden" name="csrf_token" value="${sessionScope.csrf_token}"/>
<div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Brand</label>
                                <input type="text" class="form-control" name="brand" required value="<%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(request.getAttribute("brand") != null ? request.getAttribute("brand") : "")) %>" placeholder="e.g. Toyota">
                            </div>
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Model</label>
                                <input type="text" class="form-control" name="model" required value="<%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(request.getAttribute("model") != null ? request.getAttribute("model") : "")) %>" placeholder="e.g. Corolla">
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-md-4 mb-3">
                                <label class="form-label">Category</label>
                                <select class="form-select" name="category" required>
                                    <option value="" disabled <%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(request.getAttribute("category") == null ? "selected" : "")) %>>Select Category</option>
                                    <option value="Sedan" <%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf("Sedan".equals(request.getAttribute("category")) ? "selected" : "")) %>>Sedan</option>
                                    <option value="SUV" <%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf("SUV".equals(request.getAttribute("category")) ? "selected" : "")) %>>SUV</option>
                                    <option value="Hatchback" <%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf("Hatchback".equals(request.getAttribute("category")) ? "selected" : "")) %>>Hatchback</option>
                                    <option value="Luxury" <%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf("Luxury".equals(request.getAttribute("category")) ? "selected" : "")) %>>Luxury</option>
                                </select>
                            </div>
                            <div class="col-md-4 mb-3">
                                <label class="form-label">Fuel Type</label>
                                <select class="form-select" name="fuelType" required>
                                    <option value="" disabled <%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(request.getAttribute("fuelType") == null ? "selected" : "")) %>>Select Fuel Type</option>
                                    <option value="Petrol" <%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf("Petrol".equals(request.getAttribute("fuelType")) ? "selected" : "")) %>>Petrol</option>
                                    <option value="Diesel" <%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf("Diesel".equals(request.getAttribute("fuelType")) ? "selected" : "")) %>>Diesel</option>
                                    <option value="Electric" <%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf("Electric".equals(request.getAttribute("fuelType")) ? "selected" : "")) %>>Electric</option>
                                    <option value="CNG" <%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf("CNG".equals(request.getAttribute("fuelType")) ? "selected" : "")) %>>CNG</option>
                                </select>
                            </div>
                            <div class="col-md-4 mb-3">
                                <label class="form-label">Price Per Day (₹)</label>
                                <input type="number" step="0.01" class="form-control" name="pricePerDay" required value="<%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(request.getAttribute("pricePerDay") != null ? request.getAttribute("pricePerDay") : "")) %>" placeholder="0.00">
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Location</label>
                                <select class="form-select" name="location" required>
                                    <option value="" disabled <%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(request.getAttribute("location") == null ? "selected" : "")) %>>Select Location</option>
                                    <option value="Mumbai" <%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf("Mumbai".equals(request.getAttribute("location")) ? "selected" : "")) %>>Mumbai</option>
                                    <option value="Pune" <%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf("Pune".equals(request.getAttribute("location")) ? "selected" : "")) %>>Pune</option>
                                    <option value="Goa" <%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf("Goa".equals(request.getAttribute("location")) ? "selected" : "")) %>>Goa</option>
                                    <option value="Delhi" <%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf("Delhi".equals(request.getAttribute("location")) ? "selected" : "")) %>>Delhi</option>
                                    <option value="Bangalore" <%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf("Bangalore".equals(request.getAttribute("location")) ? "selected" : "")) %>>Bangalore</option>
                                </select>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Status</label>
                                <select class="form-select" name="status" required>
                                    <option value="AVAILABLE" <%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf("AVAILABLE".equals(request.getAttribute("status")) || request.getAttribute("status") == null ? "selected" : "")) %>>Available</option>
                                    <option value="BOOKED" <%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf("BOOKED".equals(request.getAttribute("status")) ? "selected" : "")) %>>Booked</option>
                                </select>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Image URL</label>
                            <input type="url" class="form-control" name="imageUrl" value="<%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(request.getAttribute("imageUrl") != null ? request.getAttribute("imageUrl") : "")) %>" placeholder="https://example.com/car.jpg">
                        </div>
                        <div class="mb-4">
                            <label class="form-label">Or Upload Image</label>
                            <input type="file" class="form-control" name="imageFile" accept="image/*">
                        </div>

                        <button type="submit" class="btn btn-primary"><i class="fas fa-save"></i> Save Car</button>
                        <a href="${pageContext.request.contextPath}/admin/cars" class="btn btn-secondary">Cancel</a>
                    </form>
                </div>
            </div><c:set var="extraScripts" scope="request">
    <script src="${pageContext.request.contextPath}/js/admin.js"></script>
</c:set>
<jsp:include page="WEB-INF/fragments/layout-bottom.jsp" />