<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Car" %>
<%@ page import="model.User" %>
<%
    User loggedUser = (User) session.getAttribute("loggedUser");
    if (loggedUser == null) {
        response.sendRedirect("login");
        return;
    }
%>
<c:set var="pageTitle" value="Browse Fleet — CarRental" scope="request" />
<jsp:include page="WEB-INF/fragments/layout-top.jsp" />

            <%-- ── Hero Section ── --%>
            <div class="hero-section mb-4 animate-in">
                <div class="hero-content">
                    <div class="row align-items-center">
                        <div class="col-lg-7">
                            <p class="text-uppercase small fw-bold mb-2" style="color:rgba(139,92,246,.9); letter-spacing:.1em; font-size:.7rem;">
                                <i class="fas fa-gem me-1"></i> Premium Fleet
                            </p>
                            <h1 class="mb-2" style="font-size:1.75rem;">
                                Welcome back, <%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(loggedUser.getFullName())) %>
                            </h1>
                            <p class="mb-4" style="font-size:.9rem;">Find your perfect ride from our curated collection of premium vehicles.</p>
                            <div class="d-flex flex-wrap gap-3">
                                <div class="stat-pill">
                                    <i class="fas fa-key"></i>
                                    <span class="value"><%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(request.getAttribute("activeRentals") != null ? request.getAttribute("activeRentals") : "0")) %></span>
                                    <span style="opacity:.6">Active Rentals</span>
                                </div>
                                <div class="stat-pill">
                                    <i class="fas fa-medal"></i>
                                    <span class="value"><%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(request.getAttribute("loyaltyPoints") != null ? request.getAttribute("loyaltyPoints") : "0")) %></span>
                                    <span style="opacity:.6">Loyalty Points</span>
                                </div>
                                <div class="stat-pill">
                                    <i class="fas fa-receipt"></i>
                                    <span class="value"><%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(request.getAttribute("totalBookings") != null ? request.getAttribute("totalBookings") : "0")) %></span>
                                    <span style="opacity:.6">Total Bookings</span>
                                </div>
                            </div>
                        </div>
                        <div class="col-lg-5 text-end d-none d-lg-block">
                            <div style="font-size:6rem; opacity:.08; line-height:1;">
                                <i class="fas fa-car-side"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <%-- ── Main Content ── --%>
            <div class="row g-4">
                <%-- Filters Sidebar --%>
                <div class="col-lg-3 animate-in">
                    <div class="filter-card">
                        <div class="d-flex justify-content-between align-items-center mb-4">
                            <h6 class="fw-bold mb-0"><i class="fas fa-sliders-h me-2 opacity-50"></i>Filters</h6>
                            <a href="${pageContext.request.contextPath}/customer/dashboard" class="text-primary small text-decoration-none fw-600">Reset</a>
                        </div>
                        <form action="${pageContext.request.contextPath}/customer/dashboard" method="GET">
                            <div class="mb-3">
                                <label class="form-label">Brand</label>
                                <select name="brand" class="form-select form-select-sm">
                                    <option value="">All Brands</option>
                                    <%
                                        List<String> brands = (List<String>) request.getAttribute("brands");
                                        String brandParam = request.getParameter("brand");
                                        if (brands != null) {
                                            for (String b : brands) {
                                    %>
                                    <option value="<%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(b)) %>" <%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf((b.equalsIgnoreCase(brandParam)) ? "selected" : "")) %>><%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(b)) %></option>
                                    <%      }
                                        } %>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Category</label>
                                <select name="category" class="form-select form-select-sm">
                                    <option value="">All Categories</option>
                                    <%
                                        List<String> categories = (List<String>) request.getAttribute("categories");
                                        String catParam = request.getParameter("category");
                                        if (categories != null) {
                                            for (String c : categories) {
                                    %>
                                    <option value="<%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(c)) %>" <%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf((c.equalsIgnoreCase(catParam)) ? "selected" : "")) %>><%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(c)) %></option>
                                    <%      }
                                        } %>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Fuel Type</label>
                                <select name="fuelType" class="form-select form-select-sm">
                                    <option value="">All Types</option>
                                    <%
                                        List<String> fuelTypes = (List<String>) request.getAttribute("fuelTypes");
                                        String fuelParam = request.getParameter("fuelType");
                                        if (fuelTypes != null) {
                                            for (String f : fuelTypes) {
                                    %>
                                    <option value="<%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(f)) %>" <%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf((f.equalsIgnoreCase(fuelParam)) ? "selected" : "")) %>><%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(f)) %></option>
                                    <%      }
                                        } %>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Location</label>
                                <select name="location" class="form-select form-select-sm">
                                    <option value="">All Locations</option>
                                    <%
                                        List<String> locations = (List<String>) request.getAttribute("locations");
                                        String locParam = request.getParameter("location");
                                        if (locations != null) {
                                            for (String l : locations) {
                                    %>
                                    <option value="<%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(l)) %>" <%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf((l.equalsIgnoreCase(locParam)) ? "selected" : "")) %>><%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(l)) %></option>
                                    <%      }
                                        } %>
                                </select>
                            </div>
                            <div class="mb-4">
                                <label class="form-label">Max Price / Day</label>
                                <div class="d-flex justify-content-between mb-1">
                                    <span class="extra-small text-muted">₹0</span>
                                    <span class="extra-small fw-bold" id="priceLabel" style="color:var(--primary)">₹<%= request.getParameter("maxPrice") != null ? org.apache.taglibs.standard.functions.Functions.escapeXml(request.getParameter("maxPrice")) : "10000" %></span>
                                </div>
                                <input type="range" class="form-range" min="0" max="10000" step="500"
                                       name="maxPrice" id="priceRange"
                                       value="<%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(request.getParameter("maxPrice") != null ? request.getParameter("maxPrice") : "10000")) %>"
                                       oninput="document.getElementById('priceLabel').innerText='₹'+this.value">
                            </div>
                            <button type="submit" class="btn btn-primary w-100">
                                <i class="fas fa-search me-2"></i>Apply Filters
                            </button>
                        </form>
                    </div>
                </div>

                <%-- Car Grid --%>
                <div class="col-lg-9">
                    <%-- Results Header --%>
                    <%
                        List<Car> availableCars = (List<Car>) request.getAttribute("availableCars");
                        int carCount = (availableCars != null) ? availableCars.size() : 0;
                    %>
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <p class="text-muted mb-0 small">
                            <span class="fw-bold" style="color:var(--text-heading)"><%= carCount %></span> vehicles available
                        </p>
                    </div>

                    <div class="row g-3">
                        <%
                            if (availableCars != null && !availableCars.isEmpty()) {
                                for (Car car : availableCars) {
                                    // Fix image URL — don't prepend context path for external URLs
                                    String imgUrl = car.getImageUrl();
                                    String imgSrc;
                                    if (imgUrl != null && (imgUrl.startsWith("http://") || imgUrl.startsWith("https://"))) {
                                        imgSrc = imgUrl; // external URL — use as-is
                                    } else if (imgUrl != null && !imgUrl.isEmpty()) {
                                        imgSrc = request.getContextPath() + "/" + imgUrl; // local path
                                    } else {
                                        imgSrc = request.getContextPath() + "/images/default-car.svg"; // fallback
                                    }
                        %>
                        <div class="col-md-6 col-xl-4 animate-in">
                            <div class="premium-car-card">
                                <div class="car-image-wrap">
                                    <img src="<%= org.apache.taglibs.standard.functions.Functions.escapeXml(imgSrc) %>"
                                         alt="<%= org.apache.taglibs.standard.functions.Functions.escapeXml(car.getBrand() + " " + car.getModel()) %>"
                                         loading="lazy"
                                         onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/images/default-car.svg'">
                                    <span class="category-badge"><%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(car.getCategory())) %></span>
                                    <span class="fuel-badge"><i class="fas fa-gas-pump me-1"></i><%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(car.getFuelType())) %></span>
                                </div>
                                <div class="car-details">
                                    <div class="car-name"><%= org.apache.taglibs.standard.functions.Functions.escapeXml(car.getBrand()) %> <%= org.apache.taglibs.standard.functions.Functions.escapeXml(car.getModel()) %></div>
                                    <div class="car-meta">
                                        <i class="fas fa-map-marker-alt" style="color:var(--danger);"></i>
                                        <%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(car.getLocation())) %>
                                    </div>
                                    <div class="car-footer">
                                        <div class="car-price">
                                            ₹<%= String.format("%.0f", car.getPricePerDay()) %>
                                            <small>/ day</small>
                                        </div>
                                        <a href="${pageContext.request.contextPath}/customer/bookCar?carId=<%= car.getId() %>" class="book-btn">
                                            <i class="fas fa-arrow-right me-1"></i>Book
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <%
                                }
                            } else {
                        %>
                        <div class="col-12">
                            <div class="empty-state">
                                <i class="fas fa-car-side d-block"></i>
                                <h5 class="fw-bold mb-2">No Vehicles Found</h5>
                                <p class="text-muted small mb-3">Try adjusting your filters or check back later.</p>
                                <a href="${pageContext.request.contextPath}/customer/dashboard" class="btn btn-primary btn-sm">
                                    <i class="fas fa-redo me-2"></i>Clear Filters
                                </a>
                            </div>
                        </div>
                        <% } %>
                    </div>
                </div>
            </div><jsp:include page="WEB-INF/fragments/layout-bottom.jsp" />