<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Booking" %>
<c:set var="pageTitle" value="Payment Management - Car Rental System" scope="request" />
<jsp:include page="WEB-INF/fragments/layout-top.jsp" />
                <!-- Page Header -->
                <div class="mb-4">
                    <h2 class="fw-bold mb-1">Payment Management</h2>
                    <p class="text-muted">Track transactions and manage payment invoices</p>
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

                <!-- Payments Card -->
                <div class="card border-0 shadow-sm rounded-4">
                    <div class="card-header bg-transparent border-0 pt-4 px-4 d-flex justify-content-between align-items-center">
                        <h5 class="fw-bold mb-0">Transaction History</h5>
                        <button class="btn btn-sm btn-outline-success" id="exportBtn">
                            <i class="fas fa-file-export me-1"></i> Export CSV
                        </button>
                    </div>
                    <div class="card-body p-0">
                        <div class="table-responsive">
                            <table class="table table-hover align-middle mb-0" id="paymentsTable">
                                <thead class="">
                                    <tr class="small text-uppercase text-muted">
                                        <th class="ps-4">Transaction ID</th>
                                        <th>Customer</th>
                                        <th>Booking ID</th>
                                        <th>Date</th>
                                        <th>Payment Method</th>
                                        <th>Amount</th>
                                        <th>Status</th>
                                        <th class="pe-4 text-center">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <% 
                                        List<Booking> payments = (List<Booking>) request.getAttribute("allPayments");
                                        if (payments != null && !payments.isEmpty()) {
                                            for (Booking p : payments) {
                                                if(p.getStatus().equals("CANCELLED")) continue;
                                    %>
                                    <tr>
                                        <td class="ps-4">
                                            <span class="text-primary fw-bold"><%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(p.getInvoiceId() != null ? p.getInvoiceId().replace("INV", "TXN") : "TXN-"+p.getId())) %></span>
                                        </td>
                                        <td>
                                            <div class="fw-bold"><%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(p.getCustomerName())) %></div>
                                            <div class="text-muted small">ID: #<%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(p.getCustomerId())) %></div>
                                        </td>
                                        <td>
                                            <span class="fw-medium">#<%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(p.getId())) %></span>
                                        </td>
                                        <td><%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(p.getBookingDate())) %></td>
                                        <td>
                                            <span class="badge border">
                                                <i class="fas fa-credit-card me-1"></i>Online
                                            </span>
                                        </td>
                                        <td>
                                            <span class="fw-bold text-success">₹<%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(String.format("%.2f", p.getTotalAmount()))) %></span>
                                        </td>
                                        <td>
                                            <span class="badge bg-success bg-opacity-15 text-success rounded-pill px-3 py-2">
                                                <i class="fas fa-check-circle me-1"></i>SUCCESS
                                            </span>
                                        </td>
                                        <td class="pe-4 text-center">
                                            <div class="btn-group" role="group">
                                                <a href="${pageContext.request.contextPath}/customer/generateInvoice?bookingId=<%= org.apache.taglibs.standard.functions.Functions.escapeXml(String.valueOf(p.getId())) %>" class="btn btn-sm btn-outline-primary" title="Download Invoice">
                                                    <i class="fas fa-download"></i>
                                                </a>
                                                <a href="#" class="btn btn-sm btn-outline-secondary" title="View Details">
                                                    <i class="fas fa-eye"></i>
                                                </a>
                                            </div>
                                        </td>
                                    </tr>
                                    <% 
                                            }
                                        } else {
                                    %>
                                    <tr><td colspan="8" class="text-center py-5 text-muted">
                                        <i class="fas fa-inbox fa-2x mb-3"></i><br>No transaction records found.
                                    </td></tr>
                                    <% } %>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div><c:set var="extraScripts" scope="request">
    <script>
        document.getElementById('exportBtn').addEventListener('click', function() {
            const table = document.getElementById('paymentsTable');
            let csv = [];
            
            // Get headers
            const headers = [];
            table.querySelectorAll('thead th').forEach(th => {
                headers.push(th.innerText);
            });
            csv.push(headers.join(','));
            
            // Get rows
            table.querySelectorAll('tbody tr').forEach(tr => {
                const row = [];
                tr.querySelectorAll('td').forEach((td, index) => {
                    if (index < 7) { // Exclude actions column
                        row.push('"' + td.innerText.replace(/"/g, '""') + '"');
                    }
                });
                if (row.length > 0) csv.push(row.join(','));
            });
            
            // Download
            const csvContent = csv.join('\n');
            const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
            const link = document.createElement('a');
            link.href = URL.createObjectURL(blob);
            link.download = 'payments_' + new Date().toISOString().split('T')[0] + '.csv';
            link.click();
        });
    </script>
    <script src="${pageContext.request.contextPath}/js/admin.js"></script>
</c:set>
<jsp:include page="WEB-INF/fragments/layout-bottom.jsp" />