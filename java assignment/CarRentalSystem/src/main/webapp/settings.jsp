<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<% utils.CSRFUtil.ensureToken(session); %>
<%@ page import="model.User" %>
<%
    User currentUser = (User) request.getAttribute("currentUser");
    if (currentUser == null) {
        currentUser = (User) session.getAttribute("loggedUser");
    }
%>
<c:set var="pageTitle" value="Settings - CarRental" scope="request" />
<jsp:include page="WEB-INF/fragments/layout-top.jsp" />
                <!-- Page Header -->
                <div class="page-header mb-4">
                    <h2 class="fw-bold">Settings</h2>
                    <p class="text-muted">Manage your profile, security, and preferences.</p>
                </div>

                <!-- Alerts -->
                <c:if test="${not empty param.success}">
                    <div class="alert alert-success alert-dismissible fade show border-0 shadow-sm mb-4" role="alert">
                        <i class="fas fa-check-circle me-2"></i> ${fn:escapeXml(param.success)}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                <c:if test="${not empty param.error}">
                    <div class="alert alert-danger alert-dismissible fade show border-0 shadow-sm mb-4" role="alert">
                        <i class="fas fa-exclamation-circle me-2"></i> ${fn:escapeXml(param.error)}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>

                <div class="row g-4">
                    <!-- Nav Tabs (Vertical on Large) -->
                    <div class="col-lg-3">
                        <div class="card border-0 shadow-sm rounded-4 overflow-hidden">
                            <div class="list-group list-group-flush" id="settingsTabs" role="tablist">
                                <a class="list-group-item list-group-item-action active p-3 border-0" id="profile-tab" data-bs-toggle="tab" href="#profile" role="tab">
                                    <i class="fas fa-user-circle me-2"></i> Profile Information
                                </a>
                                <a class="list-group-item list-group-item-action p-3 border-0" id="password-tab" data-bs-toggle="tab" href="#password" role="tab">
                                    <i class="fas fa-shield-alt me-2"></i> Password & Security
                                </a>
                                <a class="list-group-item list-group-item-action p-3 border-0" id="preferences-tab" data-bs-toggle="tab" href="#preferences" role="tab">
                                    <i class="fas fa-paint-brush me-2"></i> Appearance
                                </a>
                            </div>
                        </div>
                    </div>

                    <!-- Tab Content -->
                    <div class="col-lg-9">
                        <div class="tab-content border-0">
                            <!-- Profile Tab -->
                            <div class="tab-pane fade show active" id="profile" role="tabpanel">
                                <div class="card border-0 shadow-sm rounded-4">
                                    <div class="card-body p-4">
                                        <h5 class="fw-bold mb-4">Profile Information</h5>
                                        <form action="settings" method="POST">
    <input type="hidden" name="csrf_token" value="${sessionScope.csrf_token}"/>
<input type="hidden" name="action" value="updateProfile">
                                            <div class="row g-3">
                                                <div class="col-md-6">
                                                    <label class="form-label text-secondary small fw-bold">Full Name</label>
                                                    <input type="text" class="form-control" name="fullName" value="<%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(currentUser.getFullName())) %>" required>
                                                </div>
                                                <div class="col-md-6">
                                                    <label class="form-label text-secondary small fw-bold">Email Address</label>
                                                    <input type="email" class="form-control" name="email" value="<%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(currentUser.getEmail())) %>" required>
                                                </div>
                                                <div class="col-md-6">
                                                    <label class="form-label text-secondary small fw-bold">Phone Number</label>
                                                    <input type="text" class="form-control" name="phoneNumber" value="<%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(currentUser.getPhoneNumber() != null ? currentUser.getPhoneNumber() : "")) %>">
                                                </div>
                                                <div class="col-md-6">
                                                    <label class="form-label text-secondary small fw-bold">Role</label>
                                                    <input type="text" class="form-control" value="<%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(currentUser.getRole())) %>" disabled>
                                                </div>
                                                <div class="col-12 mt-4">
                                                    <button type="submit" class="btn btn-primary">Save Changes</button>
                                                </div>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </div>

                            <!-- Password Tab -->
                            <div class="tab-pane fade" id="password" role="tabpanel">
                                <div class="card border-0 shadow-sm rounded-4">
                                    <div class="card-body p-4">
                                        <h5 class="fw-bold mb-4">Update Password</h5>
                                        <form action="settings" method="POST">
    <input type="hidden" name="csrf_token" value="${sessionScope.csrf_token}"/>
<input type="hidden" name="action" value="changePassword">
                                            <div class="row g-3">
                                                <div class="col-12">
                                                    <label class="form-label text-secondary small fw-bold">Current Password</label>
                                                    <input type="password" class="form-control" name="currentPassword" required>
                                                </div>
                                                <div class="col-md-6">
                                                    <label class="form-label text-secondary small fw-bold">New Password</label>
                                                    <input type="password" class="form-control" name="newPassword" minlength="6" required>
                                                </div>
                                                <div class="col-md-6">
                                                    <label class="form-label text-secondary small fw-bold">Confirm New Password</label>
                                                    <input type="password" class="form-control" name="confirmPassword" required>
                                                </div>
                                                <div class="col-12 mt-4">
                                                    <button type="submit" class="btn btn-primary">Update Password</button>
                                                </div>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </div>

                            <!-- Appearance Tab -->
                            <div class="tab-pane fade" id="preferences" role="tabpanel">
                                <div class="card border-0 shadow-sm rounded-4">
                                    <div class="card-body p-4">
                                        <h5 class="fw-bold mb-4">Appearance Settings</h5>
                                        <div class="mb-4">
                                            <label class="form-label text-secondary small fw-bold">Interface Theme</label>
                                            <div class="row g-3">
                                                <div class="col-md-4">
                                                    <div class="theme-option p-3 border rounded-3 text-center cursor-pointer" onclick="themeManager.applyTheme('light')">
                                                        <i class="fas fa-sun fa-2x mb-2 text-warning"></i>
                                                        <div class="fw-bold">Light</div>
                                                    </div>
                                                </div>
                                                <div class="col-md-4">
                                                    <div class="theme-option p-3 border rounded-3 text-center cursor-pointer" onclick="themeManager.applyTheme('dark')">
                                                        <i class="fas fa-moon fa-2x mb-2 text-primary"></i>
                                                        <div class="fw-bold">Dark</div>
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
        </div>
    </div>

    <c:set var="extraScripts" scope="request">
<script>
        // Handle direct linking to tabs
        document.addEventListener('DOMContentLoaded', function() {
            var hash = window.location.hash;
            if (hash) {
                var triggerEl = document.querySelector('#settingsTabs a[href="' + hash + '"]');
                if (triggerEl) {
                    bootstrap.Tab.getInstance(triggerEl)?.show() || new bootstrap.Tab(triggerEl).show();
                }
            }
        });
    </script>
    <style>
        .cursor-pointer { cursor: pointer; }
        .theme-option { transition: var(--transition-base); }
        .theme-option:hover { background-color: var(--bg-body); border-color: var(--primary) !important; }
        [data-theme="light"] .theme-option[onclick*="'light'"],
        [data-theme="dark"] .theme-option[onclick*="'dark'"] {
            border-color: var(--primary) !important;
            background-color: var(--primary-light);
        }
    </style>
</c:set>
<jsp:include page="WEB-INF/fragments/layout-bottom.jsp" />