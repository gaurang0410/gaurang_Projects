<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<% utils.CSRFUtil.ensureToken(session); %>
<%@ page import="model.User" %>
<%
    User currentUser = (User) request.getAttribute("currentUser");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Settings - Car Rental System</title>
    <jsp:include page="WEB-INF/fragments/layout-head.jsp" />
</head>
<body>
    <div class="d-flex">
        <jsp:include page="WEB-INF/fragments/sidebar.jsp" />
        <div id="content" class="flex-grow-1">
            <jsp:include page="WEB-INF/fragments/header.jsp" />
            <div class="container-fluid p-4">
                <!-- Page Header -->
                <div class="mb-4">
                    <h2 class="fw-bold mb-1">System Settings</h2>
                    <p class="text-muted">Manage your profile, preferences, and system configuration</p>
                </div>

                <!-- Success/Error Messages -->
                <% if (request.getParameter("success") != null) { %>
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="fas fa-check-circle me-2"></i>
                    <%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(request.getParameter("success"))) %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } %>
                <% if (request.getParameter("error") != null) { %>
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="fas fa-exclamation-circle me-2"></i>
                    <%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(request.getParameter("error"))) %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } %>

                <div class="row g-4">
                    <!-- Settings Sidebar -->
                    <div class="col-lg-3">
                        <div class="card border-0 shadow-sm rounded-4">
                            <div class="card-body">
                                <nav class="nav flex-column gap-2">
                                    <a class="nav-link ps-0 pe-0 pb-2 border-bottom border-light active" data-bs-toggle="pill" href="#profile">
                                        <i class="fas fa-user me-2"></i>Profile
                                    </a>
                                    <a class="nav-link ps-0 pe-0 py-2 border-bottom border-light" data-bs-toggle="pill" href="#password">
                                        <i class="fas fa-lock me-2"></i>Change Password
                                    </a>
                                    <a class="nav-link ps-0 pe-0 py-2 border-bottom border-light" data-bs-toggle="pill" href="#preferences">
                                        <i class="fas fa-sliders-h me-2"></i>Preferences
                                    </a>
                                    <a class="nav-link ps-0 pe-0 py-2" data-bs-toggle="pill" href="#about">
                                        <i class="fas fa-info-circle me-2"></i>About
                                    </a>
                                </nav>
                            </div>
                        </div>
                    </div>

                    <!-- Settings Content -->
                    <div class="col-lg-9">
                        <div class="tab-content">
                            <!-- Profile Settings -->
                            <div class="tab-pane fade show active" id="profile">
                                <div class="card border-0 shadow-sm rounded-4">
                                    <div class="card-header bg-transparent border-0 pt-4 px-4">
                                        <h5 class="fw-bold mb-0">Profile Settings</h5>
                                        <p class="text-muted small mt-1">Update your personal information</p>
                                    </div>
                                    <div class="card-body p-4">
                                        <form method="POST" action="settings">
    <input type="hidden" name="csrf_token" value="${sessionScope.csrf_token}"/>
<input type="hidden" name="action" value="updateProfile">

                                            <div class="mb-4">
                                                <label class="form-label fw-bold">Full Name</label>
                                                <input type="text" class="form-control form-control-lg rounded-3" name="fullName" 
                                                       value="<%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(currentUser != null ? currentUser.getFullName() : "")) %>" required>
                                            </div>

                                            <div class="mb-4">
                                                <label class="form-label fw-bold">Email Address</label>
                                                <input type="email" class="form-control form-control-lg rounded-3" name="email" 
                                                       value="<%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(currentUser != null ? currentUser.getEmail() : "")) %>" required>
                                            </div>

                                            <div class="mb-4">
                                                <label class="form-label fw-bold">Username</label>
                                                <input type="text" class="form-control form-control-lg rounded-3" 
                                                       value="<%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(currentUser != null ? currentUser.getUsername() : "")) %>" disabled>
                                                <small class="text-muted">Username cannot be changed</small>
                                            </div>

                                            <div class="mb-4">
                                                <label class="form-label fw-bold">Role</label>
                                                <input type="text" class="form-control form-control-lg rounded-3" 
                                                       value="<%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(currentUser != null ? currentUser.getRole() : "")) %>" disabled>
                                                <small class="text-muted">Role is assigned by administrator</small>
                                            </div>

                                            <div class="d-grid gap-2 d-sm-flex">
                                                <button type="submit" class="btn btn-primary btn-lg rounded-3">
                                                    <i class="fas fa-save me-2"></i>Save Changes
                                                </button>
                                                <button type="reset" class="btn btn-outline-secondary btn-lg rounded-3">
                                                    <i class="fas fa-redo me-2"></i>Reset
                                                </button>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </div>

                            <!-- Change Password -->
                            <div class="tab-pane fade" id="password">
                                <div class="card border-0 shadow-sm rounded-4">
                                    <div class="card-header bg-transparent border-0 pt-4 px-4">
                                        <h5 class="fw-bold mb-0">Change Password</h5>
                                        <p class="text-muted small mt-1">Update your password to keep your account secure</p>
                                    </div>
                                    <div class="card-body p-4">
                                        <form method="POST" action="settings">
    <input type="hidden" name="csrf_token" value="${sessionScope.csrf_token}"/>
<input type="hidden" name="action" value="changePassword">

                                            <div class="mb-4">
                                                <label class="form-label fw-bold">Current Password</label>
                                                <input type="password" class="form-control form-control-lg rounded-3" name="currentPassword" required>
                                                <small class="text-muted">Enter your current password</small>
                                            </div>

                                            <hr>

                                            <div class="mb-4">
                                                <label class="form-label fw-bold">New Password</label>
                                                <input type="password" class="form-control form-control-lg rounded-3" name="newPassword" required>
                                                <small class="text-muted">Minimum 6 characters required</small>
                                            </div>

                                            <div class="mb-4">
                                                <label class="form-label fw-bold">Confirm New Password</label>
                                                <input type="password" class="form-control form-control-lg rounded-3" name="confirmPassword" required>
                                                <small class="text-muted">Re-enter your new password</small>
                                            </div>

                                            <div class="alert alert-info rounded-3">
                                                <i class="fas fa-info-circle me-2"></i>
                                                <strong>Password Requirements:</strong>
                                                <ul class="mb-0 mt-2">
                                                    <li>Minimum 6 characters</li>
                                                    <li>Use a mix of letters, numbers, and symbols</li>
                                                    <li>Avoid using personal information</li>
                                                </ul>
                                            </div>

                                            <div class="d-grid gap-2 d-sm-flex">
                                                <button type="submit" class="btn btn-primary btn-lg rounded-3">
                                                    <i class="fas fa-lock me-2"></i>Change Password
                                                </button>
                                                <button type="reset" class="btn btn-outline-secondary btn-lg rounded-3">
                                                    <i class="fas fa-redo me-2"></i>Reset
                                                </button>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </div>

                            <!-- Preferences -->
                            <div class="tab-pane fade" id="preferences">
                                <div class="card border-0 shadow-sm rounded-4">
                                    <div class="card-header bg-transparent border-0 pt-4 px-4">
                                        <h5 class="fw-bold mb-0">System Preferences</h5>
                                        <p class="text-muted small mt-1">Customize your system experience</p>
                                    </div>
                                    <div class="card-body p-4">
                                        <form method="POST" action="settings">
    <input type="hidden" name="csrf_token" value="${sessionScope.csrf_token}"/>
<input type="hidden" name="action" value="updatePreferences">

                                            <div class="mb-4">
                                                <label class="form-label fw-bold">Theme</label>
                                                <select class="form-select form-select-lg rounded-3" name="theme">
                                                    <option value="light" selected>Light Theme</option>
                                                    <option value="dark">Dark Theme</option>
                                                    <option value="auto">Auto (System Default)</option>
                                                </select>
                                            </div>

                                            <div class="mb-4">
                                                <label class="form-label fw-bold">Currency</label>
                                                <select class="form-select form-select-lg rounded-3" name="currency">
                                                    <option value="INR" selected>Indian Rupee (₹)</option>
                                                    <option value="USD">US Dollar ($)</option>
                                                    <option value="EUR">Euro (€)</option>
                                                    <option value="GBP">British Pound (£)</option>
                                                </select>
                                            </div>

                                            <div class="mb-4">
                                                <label class="form-label fw-bold">Notifications</label>
                                                <div class="form-check">
                                                    <input class="form-check-input" type="checkbox" id="emailNotif" name="notifications" value="email" checked>
                                                    <label class="form-check-label" for="emailNotif">
                                                        Email Notifications
                                                    </label>
                                                </div>
                                                <div class="form-check mt-2">
                                                    <input class="form-check-input" type="checkbox" id="smsNotif" name="notifications" value="sms">
                                                    <label class="form-check-label" for="smsNotif">
                                                        SMS Notifications
                                                    </label>
                                                </div>
                                            </div>

                                            <div class="mb-4">
                                                <label class="form-label fw-bold">Items Per Page</label>
                                                <select class="form-select form-select-lg rounded-3" name="itemsPerPage">
                                                    <option value="10">10 items</option>
                                                    <option value="25" selected>25 items</option>
                                                    <option value="50">50 items</option>
                                                    <option value="100">100 items</option>
                                                </select>
                                            </div>

                                            <div class="d-grid gap-2 d-sm-flex">
                                                <button type="submit" class="btn btn-primary btn-lg rounded-3">
                                                    <i class="fas fa-save me-2"></i>Save Preferences
                                                </button>
                                                <button type="reset" class="btn btn-outline-secondary btn-lg rounded-3">
                                                    <i class="fas fa-redo me-2"></i>Reset
                                                </button>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </div>

                            <!-- About -->
                            <div class="tab-pane fade" id="about">
                                <div class="card border-0 shadow-sm rounded-4">
                                    <div class="card-header bg-transparent border-0 pt-4 px-4">
                                        <h5 class="fw-bold mb-0">About</h5>
                                        <p class="text-muted small mt-1">System information and version details</p>
                                    </div>
                                    <div class="card-body p-4">
                                        <div class="row g-4">
                                            <div class="col-md-6">
                                                <h6 class="fw-bold text-muted text-uppercase small">Application</h6>
                                                <p class="mb-0"><strong>Car Rental System</strong></p>
                                                <small class="text-muted">Version 2.0.0</small>
                                            </div>
                                            <div class="col-md-6">
                                                <h6 class="fw-bold text-muted text-uppercase small">Build</h6>
                                                <p class="mb-0"><strong>Production</strong></p>
                                                <small class="text-muted">Built with Java & Jakarta EE</small>
                                            </div>
                                            <div class="col-md-6">
                                                <h6 class="fw-bold text-muted text-uppercase small">Technologies</h6>
                                                <small>
                                                    JSP • Servlets • JDBC • Bootstrap 5 • Chart.js
                                                </small>
                                            </div>
                                            <div class="col-md-6">
                                                <h6 class="fw-bold text-muted text-uppercase small">Support</h6>
                                                <small>
                                                    <a href="mailto:support@carrental.com" class="text-decoration-none">
                                                        support@carrental.com
                                                    </a>
                                                </small>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>