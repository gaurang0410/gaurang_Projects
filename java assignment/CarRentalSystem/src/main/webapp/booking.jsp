<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<% utils.CSRFUtil.ensureToken(session); %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Book Vehicle - CarRental</title>
    <jsp:include page="WEB-INF/fragments/layout-head.jsp" />
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css">
</head>
<body>

    <%-- Navbar --%>
    <nav class="navbar navbar-expand-lg">
        <div class="container">
            <a class="navbar-brand fw-bold text-primary d-flex align-items-center gap-2"
               href="${pageContext.request.contextPath}/customer/dashboard">
                <span class="brand-icon d-inline-flex align-items-center justify-content-center rounded-2"
                      style="width:32px;height:32px;background:var(--accent-color)">
                    <i class="fas fa-car-side text-white" style="font-size:.85rem"></i>
                </span>
                <span>CarRental</span>
            </a>
            <div class="ms-auto d-flex align-items-center gap-3">
                <a class="nav-link" href="${pageContext.request.contextPath}/customer/dashboard">
                    <i class="fas fa-home me-1"></i> Dashboard
                </a>
                <a class="nav-link" href="${pageContext.request.contextPath}/customer/bookings">
                    <i class="fas fa-calendar-check me-1"></i> My Bookings
                </a>
                <button id="themeToggle" class="btn btn-sm rounded-circle p-0 d-flex align-items-center justify-content-center"
                        title="Toggle theme" style="width:36px;height:36px">
                    <i class="fas fa-moon"></i>
                </button>
            </div>
        </div>
    </nav>

    <div class="container py-5">

        <%-- Step Progress --%>
        <div class="row justify-content-center mb-5">
            <div class="col-md-8">
                <div class="d-flex align-items-center justify-content-between">
                    <div class="text-center" style="flex:1">
                        <div class="mx-auto mb-1 rounded-circle d-flex align-items-center justify-content-center fw-bold"
                             style="width:38px;height:38px;background:var(--accent-color);color:var(--text-inverse)">1</div>
                        <div class="small fw-bold text-primary">Select Dates</div>
                    </div>
                    <div class="flex-grow-1 border-top mx-2" style="border-color:var(--border-color)!important"></div>
                    <div class="text-center" style="flex:1">
                        <div class="mx-auto mb-1 rounded-circle d-flex align-items-center justify-content-center fw-bold"
                             style="width:38px;height:38px;background:var(--bg-secondary);color:var(--text-muted)">2</div>
                        <div class="small" style="color:var(--text-muted)">Confirm Details</div>
                    </div>
                    <div class="flex-grow-1 border-top mx-2" style="border-color:var(--border-color)!important"></div>
                    <div class="text-center" style="flex:1">
                        <div class="mx-auto mb-1 rounded-circle d-flex align-items-center justify-content-center fw-bold"
                             style="width:38px;height:38px;background:var(--bg-secondary);color:var(--text-muted)">3</div>
                        <div class="small" style="color:var(--text-muted)">Payment</div>
                    </div>
                </div>
            </div>
        </div>

        <div class="row g-4 justify-content-center">

            <%-- ═══════════════════════════════════ --%>
            <%-- LEFT: Booking Form                 --%>
            <%-- ═══════════════════════════════════ --%>
            <div class="col-lg-7">
                <div class="card border-0 shadow-sm rounded-4 p-4">
                    <h4 class="fw-bold mb-4">
                        <i class="fas fa-calendar-alt me-2 text-primary"></i>Reservation Details
                    </h4>

                    <%-- Error alert --%>
                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger border-0 rounded-3 mb-4" role="alert">
                            <i class="fas fa-exclamation-circle me-2"></i>
                            <strong>Booking Error:</strong> ${fn:escapeXml(errorMessage)}
                        </div>
                    </c:if>
                    <c:if test="${not empty param.error}">
                        <div class="alert alert-danger border-0 rounded-3 mb-4" role="alert">
                            <i class="fas fa-exclamation-circle me-2"></i>
                            ${fn:escapeXml(param.error)}
                        </div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/customer/bookCar"
                          method="post" id="bookingForm" novalidate>
                        <input type="hidden" name="csrf_token" value="${sessionScope.csrf_token}"/>
                        <input type="hidden" name="carId" value="${car.id}">

                        <%-- Dates Row --%>
                        <div class="row g-3 mb-4">
                            <div class="col-md-6">
                                <label class="form-label" for="pickupDate">
                                    <i class="far fa-calendar-alt me-1 text-primary"></i> Pickup Date
                                </label>
                                <input type="text" name="pickupDate" id="pickupDate"
                                       class="form-control datepicker"
                                       placeholder="YYYY-MM-DD" required autocomplete="off"
                                       value="${fn:escapeXml(pickupDate)}">
                                <div class="invalid-feedback">Please select a pickup date.</div>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label" for="returnDate">
                                    <i class="far fa-calendar-check me-1 text-primary"></i> Return Date
                                </label>
                                <input type="text" name="returnDate" id="returnDate"
                                       class="form-control datepicker"
                                       placeholder="YYYY-MM-DD" required autocomplete="off"
                                       value="${fn:escapeXml(returnDate)}">
                                <div class="invalid-feedback">Please select a return date.</div>
                            </div>
                        </div>

                        <%-- Date error --%>
                        <div id="dateError" class="alert alert-warning border-0 rounded-3 mb-4 d-none" role="alert">
                            <i class="fas fa-exclamation-triangle me-2"></i>
                            <span id="dateErrorMsg">Return date must be after pickup date.</span>
                        </div>

                        <%-- Locations Row --%>
                        <div class="row g-3 mb-4">
                            <div class="col-md-6">
                                <label class="form-label" for="pickupLocation">
                                    <i class="fas fa-map-marker-alt me-1 text-success"></i> Pickup Location
                                </label>
                                <select class="form-select" name="pickupLocation" id="pickupLocation" required>
                                    <option value="" disabled selected>Choose location</option>
                                    <option value="Mumbai"    ${pickupLocation == 'Mumbai'    ? 'selected' : ''}>Mumbai</option>
                                    <option value="Pune"      ${pickupLocation == 'Pune'      ? 'selected' : ''}>Pune</option>
                                    <option value="Goa"       ${pickupLocation == 'Goa'       ? 'selected' : ''}>Goa</option>
                                    <option value="Delhi"     ${pickupLocation == 'Delhi'     ? 'selected' : ''}>Delhi</option>
                                    <option value="Bangalore" ${pickupLocation == 'Bangalore' ? 'selected' : ''}>Bangalore</option>
                                </select>
                                <div class="invalid-feedback">Please select a pickup location.</div>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label" for="dropLocation">
                                    <i class="fas fa-map-pin me-1 text-danger"></i> Drop-off Location
                                </label>
                                <select class="form-select" name="dropLocation" id="dropLocation" required>
                                    <option value="" disabled selected>Choose location</option>
                                    <option value="Mumbai"    ${dropLocation == 'Mumbai'    ? 'selected' : ''}>Mumbai</option>
                                    <option value="Pune"      ${dropLocation == 'Pune'      ? 'selected' : ''}>Pune</option>
                                    <option value="Goa"       ${dropLocation == 'Goa'       ? 'selected' : ''}>Goa</option>
                                    <option value="Delhi"     ${dropLocation == 'Delhi'     ? 'selected' : ''}>Delhi</option>
                                    <option value="Bangalore" ${dropLocation == 'Bangalore' ? 'selected' : ''}>Bangalore</option>
                                </select>
                                <div class="invalid-feedback">Please select a drop-off location.</div>
                            </div>
                        </div>

                        <%-- Terms --%>
                        <div class="form-check mb-4">
                            <input class="form-check-input" type="checkbox" id="termsCheck" required>
                            <label class="form-check-label small" for="termsCheck">
                                I agree to the <a href="#" class="text-primary">Terms & Conditions</a> and
                                <a href="#" class="text-primary">Cancellation Policy</a>.
                            </label>
                        </div>

                        <button type="submit" class="btn btn-primary w-100 py-3 fw-bold rounded-3"
                                id="confirmBtn" disabled>
                            <span id="btnText">
                                <i class="fas fa-lock me-2"></i>Confirm Booking
                            </span>
                            <span id="btnSpinner" class="d-none">
                                <span class="spinner-border spinner-border-sm me-2"></span> Processing...
                            </span>
                        </button>
                    </form>
                </div>
            </div>

            <%-- ═══════════════════════════════════ --%>
            <%-- RIGHT: Price Summary Sidebar       --%>
            <%-- ═══════════════════════════════════ --%>
            <div class="col-lg-4">
                <div class="card border-0 shadow-sm rounded-4 overflow-hidden sticky-top" style="top:90px">

                    <%-- Car Image --%>
                    <div style="position:relative;height:200px;overflow:hidden;background:var(--bg-secondary)">
                        <c:choose>
                            <c:when test="${not empty car.imageUrl and (fn:startsWith(car.imageUrl,'http://') or fn:startsWith(car.imageUrl,'https://'))}">
                                <img src="${fn:escapeXml(car.imageUrl)}"
                                     alt="${fn:escapeXml(car.brand)} ${fn:escapeXml(car.model)}"
                                     id="carImg"
                                     style="width:100%;height:200px;object-fit:cover"
                                     onerror="this.style.display='none';document.getElementById('carImgFallback').style.display='flex'">
                            </c:when>
                            <c:when test="${not empty car.imageUrl}">
                                <img src="${pageContext.request.contextPath}/uploads/${fn:escapeXml(car.imageUrl)}"
                                     alt="${fn:escapeXml(car.brand)} ${fn:escapeXml(car.model)}"
                                     id="carImg"
                                     style="width:100%;height:200px;object-fit:cover"
                                     onerror="this.style.display='none';document.getElementById('carImgFallback').style.display='flex'">
                            </c:when>
                        </c:choose>
                        <%-- Fallback if image fails --%>
                        <div id="carImgFallback"
                             style="display:${empty car.imageUrl ? 'flex' : 'none'};height:200px;align-items:center;justify-content:center;flex-direction:column;background:var(--bg-secondary)">
                            <i class="fas fa-car-side fa-3x mb-2" style="color:var(--text-muted)"></i>
                            <span class="small" style="color:var(--text-muted)">No image available</span>
                        </div>
                    </div>

                    <%-- Car Info --%>
                    <div class="card-body p-4">
                        <div class="mb-3">
                            <span class="badge rounded-pill mb-2"
                                  style="background:var(--accent-glow);color:var(--accent-color)">
                                ${fn:escapeXml(car.category)}
                            </span>
                            <h5 class="fw-bold mb-1">
                                ${fn:escapeXml(car.brand)} ${fn:escapeXml(car.model)}
                            </h5>
                            <div class="small" style="color:var(--text-muted)">
                                <i class="fas fa-gas-pump me-1"></i>${fn:escapeXml(car.fuelType)}
                                &nbsp;•&nbsp;
                                <i class="fas fa-users me-1"></i>5 Seater
                                &nbsp;•&nbsp;
                                <i class="fas fa-map-marker-alt me-1"></i>${fn:escapeXml(car.location)}
                            </div>
                        </div>

                        <hr style="border-color:var(--border-color)">

                        <%-- Pricing breakdown --%>
                        <div class="d-flex justify-content-between align-items-center mb-2">
                            <span class="small" style="color:var(--text-muted)">Daily Rate</span>
                            <span class="fw-semibold">
                                ₹<span id="pricePerDayDisplay"><c:out value="${fn:substringBefore(car.pricePerDay, '.')}"/></span>
                            </span>
                        </div>
                        <div class="d-flex justify-content-between align-items-center mb-2">
                            <span class="small" style="color:var(--text-muted)">Duration</span>
                            <span class="fw-semibold" id="durationDisplay" style="color:var(--text-muted)">-- days</span>
                        </div>
                        <div class="d-flex justify-content-between align-items-center mb-2">
                            <span class="small" style="color:var(--text-muted)">Subtotal</span>
                            <span class="fw-semibold" id="subtotalDisplay" style="color:var(--text-muted)">₹0.00</span>
                        </div>
                        <div class="d-flex justify-content-between align-items-center mb-2">
                            <span class="small" style="color:var(--text-muted)">GST (18%)</span>
                            <span class="fw-semibold" id="gstAmountDisplay" style="color:var(--text-muted)">₹0.00</span>
                        </div>

                        <hr style="border-color:var(--border-color)">

                        <div class="d-flex justify-content-between align-items-center">
                            <span class="fw-bold">Total Payable</span>
                            <span class="fw-bold fs-4 text-primary" id="totalAmountDisplay">₹0.00</span>
                        </div>

                        <%-- Trust signals --%>
                        <div class="mt-3 p-3 rounded-3 text-center small"
                             style="background:var(--bg-secondary);color:var(--text-muted)">
                            <i class="fas fa-shield-alt text-success me-1"></i> Secure Booking
                            &nbsp;|&nbsp;
                            <i class="fas fa-undo me-1 text-primary"></i> Free Cancellation
                        </div>
                    </div>
                </div>

                <%-- Reviews --%>
                <div class="card border-0 shadow-sm rounded-4 p-4 mt-4">
                    <h5 class="fw-bold mb-3">
                        <i class="fas fa-star text-warning me-2"></i>Customer Reviews
                    </h5>
                    <c:choose>
                        <c:when test="${not empty reviews}">
                            <c:forEach var="r" items="${reviews}">
                                <div class="mb-3 pb-3 border-bottom">
                                    <div class="d-flex justify-content-between align-items-center mb-1">
                                        <span class="fw-semibold small">${fn:escapeXml(r.customerName)}</span>
                                        <span class="text-warning extra-small">
                                            <c:forEach begin="1" end="5" var="i">
                                                <i class="${i <= r.rating ? 'fas' : 'far'} fa-star"></i>
                                            </c:forEach>
                                        </span>
                                    </div>
                                    <p class="small mb-0 fst-italic" style="color:var(--text-muted)">
                                        "${fn:escapeXml(r.comment)}"
                                    </p>
                                </div>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <p class="small mb-0" style="color:var(--text-muted)">No reviews yet for this vehicle.</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>

    <%-- Spinner overlay --%>
    <div class="spinner-overlay" id="global-spinner">
        <div class="spinner"></div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
    <script>
    (function() {
        'use strict';

        // ── Price per day from server (no locale/formatting issues) ──
        const pricePerDay = parseFloat('<c:out value="${car.pricePerDay}" default="0"/>') || 0;

        const pickupInput    = document.getElementById('pickupDate');
        const returnInput    = document.getElementById('returnDate');
        const durationEl     = document.getElementById('durationDisplay');
        const subtotalEl     = document.getElementById('subtotalDisplay');
        const gstEl          = document.getElementById('gstAmountDisplay');
        const totalEl        = document.getElementById('totalAmountDisplay');
        const confirmBtn     = document.getElementById('confirmBtn');
        const dateErrorDiv   = document.getElementById('dateError');
        const dateErrorMsg   = document.getElementById('dateErrorMsg');

        function fmt(n) { return '₹' + n.toFixed(2); }

        function resetSummary() {
            durationEl.textContent = '-- days';
            durationEl.style.color = 'var(--text-muted)';
            subtotalEl.textContent = '₹0.00';
            subtotalEl.style.color = 'var(--text-muted)';
            gstEl.textContent      = '₹0.00';
            gstEl.style.color      = 'var(--text-muted)';
            totalEl.textContent    = '₹0.00';
        }

        function calculateTotal() {
            const pdStr = pickupInput.value.trim();
            const rdStr = returnInput.value.trim();

            dateErrorDiv.classList.add('d-none');

            if (!pdStr || !rdStr) {
                resetSummary();
                confirmBtn.disabled = true;
                return;
            }

            // Parse dates as YYYY-MM-DD — split manually to avoid timezone offset issues
            // new Date('2024-05-10') is parsed as UTC midnight which shifts by timezone.
            // Splitting and using Date(y,m,d) keeps it local.
            const [py, pm, pd] = pdStr.split('-').map(Number);
            const [ry, rm, rd] = rdStr.split('-').map(Number);

            if (!py || !pm || !pd || !ry || !rm || !rd) {
                resetSummary(); confirmBtn.disabled = true; return;
            }

            const pickup = new Date(py, pm - 1, pd);
            const ret    = new Date(ry, rm - 1, rd);
            const diffMs = ret - pickup;
            const days   = Math.round(diffMs / 86400000); // ms → days exact

            if (days <= 0) {
                resetSummary();
                dateErrorMsg.textContent = days === 0
                    ? 'Return date cannot be the same as pickup date — minimum 1 day.'
                    : 'Return date must be after the pickup date.';
                dateErrorDiv.classList.remove('d-none');
                confirmBtn.disabled = true;
                return;
            }

            const subtotal = days * pricePerDay;
            const gst      = subtotal * 0.18;
            const total    = subtotal + gst;

            durationEl.textContent = days + (days === 1 ? ' day' : ' days');
            durationEl.style.color = 'var(--text-primary)';
            subtotalEl.textContent = fmt(subtotal);
            subtotalEl.style.color = 'var(--text-primary)';
            gstEl.textContent      = fmt(gst);
            gstEl.style.color      = 'var(--text-primary)';
            totalEl.textContent    = fmt(total);

            confirmBtn.disabled = false;
        }

        // Flatpickr config — YYYY-MM-DD string format matches Java LocalDate.parse()
        const fpConfig = {
            minDate: 'today',
            dateFormat: 'Y-m-d',
            disableMobile: false,
            onChange: calculateTotal
        };

        const pickupFp = flatpickr(pickupInput, {
            ...fpConfig,
            onChange: function(selectedDates, dateStr) {
                // Ensure return date is always after pickup
                if (selectedDates[0]) {
                    const nextDay = new Date(selectedDates[0]);
                    nextDay.setDate(nextDay.getDate() + 1);
                    returnFp.set('minDate', nextDay);
                }
                calculateTotal();
            }
        });
        const returnFp = flatpickr(returnInput, fpConfig);

        // Run once in case values are pre-populated (form re-render on error)
        calculateTotal();

        // Spinner on submit + basic validation
        document.getElementById('bookingForm').addEventListener('submit', function(e) {
            if (!this.checkValidity()) {
                e.preventDefault(); e.stopPropagation();
                this.classList.add('was-validated');
                return;
            }
            document.getElementById('btnText').classList.add('d-none');
            document.getElementById('btnSpinner').classList.remove('d-none');
            confirmBtn.disabled = true;
            document.getElementById('global-spinner').style.display = 'flex';
        });

    })();
    </script>
</body>
</html>