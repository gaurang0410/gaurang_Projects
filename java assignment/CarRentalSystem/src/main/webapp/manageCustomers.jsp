<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<% utils.CSRFUtil.ensureToken(session); %>
<c:set var="pageTitle" value="Customer Management - CarRental" scope="request" />
<jsp:include page="WEB-INF/fragments/layout-top.jsp" />

                <!-- Page Header -->
                <div class="mb-4">
                    <h2 class="fw-bold mb-1">Customer Management</h2>
                    <p class="text-muted small mb-0">Manage and view all registered customers in the system</p>
                </div>

                <div class="card border-0 shadow-sm rounded-4">
                    <div class="card-header border-0 pt-4 px-4 d-flex justify-content-between align-items-center flex-wrap gap-3">
                        <div class="d-flex align-items-center gap-3">
                            <h5 class="fw-bold mb-0">Customer Directory</h5>
                            <button class="btn btn-sm btn-light border" onclick="exportToCSV()"><i class="fas fa-file-export me-2 text-primary"></i>Export CSV</button>
                        </div>
                        <div class="d-flex gap-2">
                            <select id="bulkAction" class="form-select form-select-sm" style="width: 150px;" disabled>
                                <option value="">Bulk Actions</option>
                                <option value="activate">Activate</option>
                                <option value="deactivate">Deactivate</option>
                                <option value="delete">Delete Selected</option>
                            </select>
                            <div class="input-group input-group-sm" style="width: 250px;">
                                <span class="input-group-text bg-light border-end-0"><i class="fas fa-search text-muted"></i></span>
                                <input type="text" id="customerSearch" class="form-control bg-light border-start-0 ps-0" placeholder="Search customers...">
                            </div>
                        </div>
                    </div>
                    <div class="card-body p-0">
                        <div class="table-responsive">
                            <table class="table table-hover align-middle mb-0" id="customersTable">
                                <thead class="bg-light">
                                    <tr class="small text-uppercase">
                                        <th class="ps-4 border-0 py-3" style="width: 40px;">
                                            <input type="checkbox" class="form-check-input" id="selectAll">
                                        </th>
                                        <th class="border-0 py-3">Reference</th>
                                        <th class="border-0 py-3">Full Name</th>
                                        <th class="border-0 py-3">Contact Details</th>
                                        <th class="border-0 py-3">Phone Number</th>
                                        <th class="border-0 py-3 text-center">Status</th>
                                        <th class="pe-4 text-end border-0 py-3">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${not empty allCustomers}">
                                            <c:forEach var="c" items="${allCustomers}">
                                                <tr class="customer-row">
                                                    <td class="ps-4">
                                                        <input type="checkbox" class="form-check-input customer-check" value="${c.id}">
                                                    </td>
                                                    <td class="fw-semibold small" style="color:var(--text-muted)">#<c:out value="${c.id}" /></td>
                                                    <td>
                                                        <div class="d-flex align-items-center">
                                                            <div class="bg-primary bg-opacity-10 text-primary rounded-circle d-flex align-items-center justify-content-center me-3" style="width: 38px; height: 38px;">
                                                                <i class="fas fa-user small"></i>
                                                            </div>
                                                            <span class="fw-bold"><c:out value="${c.fullName}" /></span>
                                                        </div>
                                                    </td>
                                                    <td class="small text-muted"><c:out value="${c.email}" /></td>
                                                    <td class="small text-dark"><c:out value="${c.phoneNumber}" /></td>
                                                    <td class="text-center">
                                                        <span class="badge bg-success bg-opacity-10 text-success rounded-pill px-3">Active</span>
                                                    </td>
                                                    <td class="pe-4 text-end">
                                                        <div class="dropdown">
                                                            <button class="btn btn-light btn-sm rounded-circle border shadow-sm" type="button" data-bs-toggle="dropdown" aria-label="Customer actions">
                                                                <i class="fas fa-ellipsis-v extra-small"></i>
                                                            </button>
                                                            <ul class="dropdown-menu dropdown-menu-end shadow-lg border-0 p-2">
                                                                <li><button class="dropdown-item rounded-2 py-2" onclick="setEditCustomer(${c.id}, '${fn:escapeXml(c.fullName)}', '${fn:escapeXml(c.email)}', '${fn:escapeXml(c.phoneNumber)}')" data-bs-toggle="modal" data-bs-target="#editModal"><i class="fas fa-edit me-3 text-primary"></i>Edit Profile</button></li>
                                                                <li><a class="dropdown-item rounded-2 py-2" href="${pageContext.request.contextPath}/admin/customers?action=bookings&customerId=${c.id}"><i class="fas fa-calendar-check me-3 text-info"></i>View History</a></li>
                                                                <li><hr class="dropdown-divider"></li>
                                                                <li><button class="dropdown-item rounded-2 py-2 text-danger" onclick="confirmDelete(${c.id}, '${fn:escapeXml(c.fullName)}')"><i class="fas fa-trash me-3 opacity-50"></i>Remove User</button></li>
                                                            </ul>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <tr><td colspan="7" class="text-center py-5 text-muted">No customers found in the system.</td></tr>
                                        </c:otherwise>
                                    </c:choose>
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <div class="card-footer border-0 p-4 d-flex justify-content-between align-items-center">
                        <span class="small text-muted">Showing ${fn:length(allCustomers)} customers</span>
                        <nav>
                            <ul class="pagination pagination-sm mb-0">
                                <li class="page-item disabled"><a class="page-link rounded-start-3" href="#">Previous</a></li>
                                <li class="page-item active"><a class="page-link" href="#">1</a></li>
                                <li class="page-item"><a class="page-link" href="#">2</a></li>
                                <li class="page-item"><a class="page-link rounded-end-3" href="#">Next</a></li>
                            </ul>
                        </nav>
                    </div>
                </div>

    <!-- Edit Customer Modal -->
    <div class="modal fade" id="editModal" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content border-0 shadow-lg rounded-4">
                <div class="modal-header border-0 p-4 pb-0">
                    <h5 class="fw-bold">Edit Customer Profile</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <form id="editForm" method="POST" action="${pageContext.request.contextPath}/admin/customers">
                    <input type="hidden" name="csrf_token" value="${sessionScope.csrf_token}"/>
                    <input type="hidden" name="action" value="edit">
                    <input type="hidden" id="customerId" name="customerId">
                    <div class="modal-body p-4">
                        <div class="mb-3">
                            <label class="form-label">Full Name</label>
                            <input type="text" class="form-control rounded-3" id="fullName" name="fullName" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Email Address</label>
                            <input type="email" class="form-control rounded-3" id="email" name="email" required>
                        </div>
                        <div class="mb-0">
                            <label class="form-label">Phone Number</label>
                            <input type="text" class="form-control rounded-3" id="phoneNumber" name="phoneNumber">
                        </div>
                    </div>
                    <div class="modal-footer border-0 p-4 pt-0">
                        <button type="button" class="btn btn-light rounded-3 px-4" data-bs-toggle="modal">Discard</button>
                        <button type="submit" class="btn btn-primary rounded-3 px-4 shadow-sm">Save Changes</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div class="modal fade" id="deleteModal" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content border-0 shadow-lg rounded-4">
                <div class="modal-body p-5 text-center">
                    <div class="text-danger mb-4"><i class="fas fa-user-times fa-4x opacity-25"></i></div>
                    <h4 class="fw-bold mb-2">Delete Customer?</h4>
                    <p class="text-muted">Are you sure you want to remove <strong id="deleteCustomerName"></strong> from the system? This action cannot be undone.</p>
                    <div class="d-flex gap-3 mt-5">
                        <button type="button" class="btn btn-light w-100 py-2 rounded-3 border" data-bs-dismiss="modal">Cancel</button>
                        <form id="deleteForm" method="POST" action="${pageContext.request.contextPath}/admin/customers" class="w-100">
                            <input type="hidden" name="csrf_token" value="${sessionScope.csrf_token}"/>
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" id="deleteCustomerId" name="customerId">
                            <button type="submit" class="btn btn-danger w-100 py-2 rounded-3 shadow-sm">Delete</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

<c:set var="extraScripts" scope="request">
    <script>
        function setEditCustomer(id, fullName, email, phoneNumber) {
            document.getElementById('customerId').value = id;
            document.getElementById('fullName').value = fullName;
            document.getElementById('email').value = email;
            document.getElementById('phoneNumber').value = phoneNumber;
        }

        function confirmDelete(id, name) {
            document.getElementById('deleteCustomerId').value = id;
            document.getElementById('deleteCustomerName').textContent = name;
            new bootstrap.Modal(document.getElementById('deleteModal')).show();
        }

        document.getElementById('customerSearch').addEventListener('keyup', function() {
            let value = this.value.toLowerCase();
            let rows = document.querySelectorAll('#customersTable tbody tr.customer-row');
            rows.forEach(row => {
                row.style.display = row.innerText.toLowerCase().includes(value) ? '' : 'none';
            });
        });

        // Select All Checkboxes
        const selectAll = document.getElementById('selectAll');
        const customerChecks = document.querySelectorAll('.customer-check');
        const bulkAction = document.getElementById('bulkAction');

        selectAll.addEventListener('change', function() {
            customerChecks.forEach(check => check.checked = this.checked);
            updateBulkActionState();
        });

        customerChecks.forEach(check => {
            check.addEventListener('change', updateBulkActionState);
        });

        function updateBulkActionState() {
            const checkedCount = document.querySelectorAll('.customer-check:checked').length;
            bulkAction.disabled = checkedCount === 0;
        }

        function exportToCSV() {
            let csv = 'ID,Name,Email,Phone\n';
            document.querySelectorAll('#customersTable tbody tr.customer-row').forEach(row => {
                const id = row.cells[1].innerText.replace('#', '');
                const name = row.cells[2].innerText;
                const email = row.cells[3].innerText;
                const phone = row.cells[4].innerText;
                csv += `${id},"${name}","${email}","${phone}"\n`;
            });
            const blob = new Blob([csv], { type: 'text/csv' });
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.setAttribute('hidden', '');
            a.setAttribute('href', url);
            a.setAttribute('download', 'customers.csv');
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
        }
    </script>
</c:set>

<jsp:include page="WEB-INF/fragments/layout-bottom.jsp" />