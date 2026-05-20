<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<% utils.CSRFUtil.ensureToken(session); %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Secure Payment - CarRental</title>
    <jsp:include page="WEB-INF/fragments/layout-head.jsp" />
</head>
<body>
    <!-- Navbar -->
    <nav class="navbar navbar-expand-lg border-bottom py-3">
        <div class="container">
            <a class="navbar-brand fw-bold text-primary" href="${pageContext.request.contextPath}/customer/dashboard">
                <i class="fas fa-car-side me-2"></i>CarRental
            </a>
        </div>
    </nav>

    <div class="container py-5">
        <!-- Progress Wizard -->
        <div class="row justify-content-center mb-5">
            <div class="col-md-8 text-center">
                <div class="d-flex justify-content-between position-relative">
                    <div class="position-absolute top-50 start-0 end-0 translate-middle-y border-top" style="z-index: 0;"></div>
                    <div class="bg-success text-white rounded-circle d-flex align-items-center justify-content-center shadow-sm" style="width: 40px; height: 40px; z-index: 1;"><i class="fas fa-check"></i></div>
                    <div class="bg-success text-white rounded-circle d-flex align-items-center justify-content-center shadow-sm" style="width: 40px; height: 40px; z-index: 1;"><i class="fas fa-check"></i></div>
                    <div class="bg-primary text-white rounded-circle d-flex align-items-center justify-content-center shadow-sm" style="width: 40px; height: 40px; z-index: 1;">3</div>
                </div>
                <div class="d-flex justify-content-between mt-2 px-1">
                    <span class="small fw-bold text-success">Select Dates</span>
                    <span class="small fw-bold text-success">Confirm Details</span>
                    <span class="small fw-bold text-primary">Secure Payment</span>
                </div>
            </div>
        </div>

        <div class="row justify-content-center">
            <div class="col-lg-6">
                <div class="card border-0 shadow-lg rounded-4 overflow-hidden">
                    <div class="bg-primary p-4 text-white">
                        <div class="d-flex justify-content-between align-items-center">
                            <div>
                                <h4 class="fw-bold mb-0">Secure Checkout</h4>
                                <p class="small opacity-75 mb-0">Complete your payment to confirm booking</p>
                            </div>
                            <i class="fas fa-shield-check fs-1 opacity-25"></i>
                        </div>
                    </div>
                    
                    <div class="card-body p-4 p-md-5">
                        <c:choose>
                            <c:when test="${not empty booking}">
                                <div class="bg-light p-4 rounded-4 mb-5">
                                    <div class="d-flex justify-content-between align-items-center mb-3">
                                        <span class="text-muted small">Reservation Reference</span>
                                        <span class="badge bg-white text-dark border fw-bold px-3">#<c:out value="${booking.id}" /></span>
                                    </div>
                                    <div class="d-flex justify-content-between align-items-center mb-3">
                                        <span class="text-muted small">Vehicle</span>
                                        <span class="fw-bold"><c:out value="${booking.carBrand}" /> <c:out value="${booking.carModel}" /></span>
                                    </div>
                                    <hr class="opacity-10 my-3">
                                    <div class="d-flex justify-content-between align-items-center">
                                        <span class="h5 fw-bold mb-0">Grand Total</span>
                                        <span class="h3 fw-bold text-primary mb-0">₹<c:out value="${fn:substringBefore(booking.totalAmount, '.')}" />.00</span>
                                    </div>
                                </div>

<body>
    <!-- Navbar -->
    <nav class="navbar navbar-expand-lg border-bottom py-3">
        <div class="container">
            <a class="navbar-brand fw-bold text-primary" href="${pageContext.request.contextPath}/customer/dashboard">
                <i class="fas fa-car-side me-2"></i>CarRental
            </a>
        </div>
    </nav>

    <div class="container py-5">
        <!-- Progress Wizard -->
        <div class="row justify-content-center mb-5">
            <div class="col-md-8 text-center">
                <div class="d-flex justify-content-between position-relative">
                    <div class="position-absolute top-50 start-0 end-0 translate-middle-y border-top" style="z-index: 0;"></div>
                    <div class="bg-success text-white rounded-circle d-flex align-items-center justify-content-center shadow-sm" style="width: 40px; height: 40px; z-index: 1;"><i class="fas fa-check"></i></div>
                    <div class="bg-success text-white rounded-circle d-flex align-items-center justify-content-center shadow-sm" style="width: 40px; height: 40px; z-index: 1;"><i class="fas fa-check"></i></div>
                    <div class="bg-primary text-white rounded-circle d-flex align-items-center justify-content-center shadow-sm" style="width: 40px; height: 40px; z-index: 1;">3</div>
                </div>
                <div class="d-flex justify-content-between mt-2 px-1">
                    <span class="small fw-bold text-success">Select Dates</span>
                    <span class="small fw-bold text-success">Confirm Details</span>
                    <span class="small fw-bold text-primary">Secure Payment</span>
                </div>
            </div>
        </div>

        <div class="row justify-content-center">
            <div class="col-lg-6">
                <div class="card border-0 shadow-lg rounded-4 overflow-hidden">
                    <div class="bg-primary p-4 text-white">
                        <div class="d-flex justify-content-between align-items-center">
                            <div>
                                <h4 class="fw-bold mb-0">Secure Checkout</h4>
                                <p class="small opacity-75 mb-0">Complete your payment to confirm booking</p>
                            </div>
                            <i class="fas fa-shield-check fs-1 opacity-25"></i>
                        </div>
                    </div>
                    
                    <div class="card-body p-4 p-md-5">
                        <c:choose>
                            <c:when test="${not empty booking}">
                                <div class="bg-light p-4 rounded-4 mb-5">
                                    <div class="d-flex justify-content-between align-items-center mb-3">
                                        <span class="text-muted small">Reservation Reference</span>
                                        <span class="badge bg-white text-dark border fw-bold px-3">#<c:out value="${booking.id}" /></span>
                                    </div>
                                    <div class="d-flex justify-content-between align-items-center mb-3">
                                        <span class="text-muted small">Vehicle</span>
                                        <span class="fw-bold"><c:out value="${booking.carBrand}" /> <c:out value="${booking.carModel}" /></span>
                                    </div>
                                    <hr class="opacity-10 my-3">
                                    <div class="d-flex justify-content-between align-items-center">
                                        <span class="h5 fw-bold mb-0">Grand Total</span>
                                        <span class="h3 fw-bold text-primary mb-0">₹<c:out value="${fn:substringBefore(booking.totalAmount, '.')}" />.00</span>
                                    </div>
                                </div>

                                <form action="${pageContext.request.contextPath}/customer/processPayment" method="POST" onsubmit="showSpinner()">
                                    <input type="hidden" name="csrf_token" value="${sessionScope.csrf_token}"/>
                                    <input type="hidden" name="bookingId" value="${booking.id}">
                                    
                                    <h6 class="fw-bold mb-4 text-uppercase small tracking-wider">Select Payment Method</h6>
                                    
                                    <div class="row g-3 mb-4">
                                        <div class="col-md-4">
                                            <input type="radio" class="btn-check" name="paymentMethod" id="card" value="Credit/Debit Card" checked onclick="togglePaymentFields('card')">
                                            <label class="btn btn-outline-light text-dark border text-center p-3 w-100 rounded-4 d-flex flex-column h-100" for="card">
                                                <i class="fas fa-credit-card fs-4 mb-2 text-primary"></i>
                                                <span class="fw-bold small">Card</span>
                                            </label>
                                        </div>
                                        <div class="col-md-4">
                                            <input type="radio" class="btn-check" name="paymentMethod" id="upi" value="UPI" onclick="togglePaymentFields('upi')">
                                            <label class="btn btn-outline-light text-dark border text-center p-3 w-100 rounded-4 d-flex flex-column h-100" for="upi">
                                                <i class="fas fa-mobile-alt fs-4 mb-2 text-success"></i>
                                                <span class="fw-bold small">UPI</span>
                                            </label>
                                        </div>
                                        <div class="col-md-4">
                                            <input type="radio" class="btn-check" name="paymentMethod" id="netbanking" value="Net Banking" onclick="togglePaymentFields('netbanking')">
                                            <label class="btn btn-outline-light text-dark border text-center p-3 w-100 rounded-4 d-flex flex-column h-100" for="netbanking">
                                                <i class="fas fa-university fs-4 mb-2 text-info"></i>
                                                <span class="fw-bold small">Net Banking</span>
                                            </label>
                                        </div>
                                        <div class="col-md-6">
                                            <input type="radio" class="btn-check" name="paymentMethod" id="wallet" value="Wallet" onclick="togglePaymentFields('wallet')">
                                            <label class="btn btn-outline-light text-dark border text-center p-3 w-100 rounded-4 d-flex flex-column h-100" for="wallet">
                                                <i class="fas fa-wallet fs-4 mb-2 text-warning"></i>
                                                <span class="fw-bold small">Wallet</span>
                                            </label>
                                        </div>
                                        <div class="col-md-6">
                                            <input type="radio" class="btn-check" name="paymentMethod" id="cash" value="Cash at Pickup" onclick="togglePaymentFields('cash')">
                                            <label class="btn btn-outline-light text-dark border text-center p-3 w-100 rounded-4 d-flex flex-column h-100" for="cash">
                                                <i class="fas fa-money-bill-wave fs-4 mb-2 text-secondary"></i>
                                                <span class="fw-bold small">Cash at Pickup</span>
                                            </label>
                                        </div>
                                    </div>
                                    
                                    <div id="cardDetailsBlock">
                                        <div class="mb-4">
                                            <label class="form-label text-muted small">Card Number</label>
                                            <div class="input-group">
                                                <span class="input-group-text border-end-0 bg-white"><i class="far fa-credit-card text-muted"></i></span>
                                                <input type="text" class="form-control border-start-0 ps-0" placeholder="0000 0000 0000 0000">
                                            </div>
                                        </div>
                                        <div class="row g-4 mb-4">
                                            <div class="col-6">
                                                <label class="form-label text-muted small">Expiry Date</label>
                                                <input type="text" class="form-control" placeholder="MM / YY">
                                            </div>
                                            <div class="col-6">
                                                <label class="form-label text-muted small">CVV</label>
                                                <input type="password" class="form-control" placeholder="•••">
                                            </div>
                                        </div>
                                        <div class="mb-5">
                                            <label class="form-label text-muted small">Name on Card</label>
                                            <input type="text" class="form-control" placeholder="John Doe">
                                        </div>
                                    </div>

                                    <div id="upiBlock" style="display: none;">
                                        <div class="mb-5">
                                            <label class="form-label text-muted small">UPI ID / VPA</label>
                                            <div class="input-group">
                                                <span class="input-group-text border-end-0 bg-white"><i class="fas fa-at text-muted"></i></span>
                                                <input type="text" class="form-control border-start-0 ps-0" placeholder="yourname@bank">
                                            </div>
                                            <p class="extra-small text-muted mt-2"><i class="fas fa-info-circle me-1"></i>A payment request will be sent to your UPI app.</p>
                                        </div>
                                    </div>

                                    <div id="netbankingBlock" style="display: none;">
                                        <div class="mb-5">
                                            <label class="form-label text-muted small">Select Bank</label>
                                            <select class="form-select">
                                                <option selected disabled>Choose your bank</option>
                                                <option>HDFC Bank</option>
                                                <option>SBI - State Bank of India</option>
                                                <option>ICICI Bank</option>
                                                <option>Axis Bank</option>
                                                <option>Kotak Mahindra Bank</option>
                                            </select>
                                        </div>
                                    </div>

                                    <div id="walletBlock" style="display: none;">
                                        <div class="mb-5">
                                            <label class="form-label text-muted small">Select Wallet</label>
                                            <select class="form-select">
                                                <option selected disabled>Choose your wallet</option>
                                                <option>Paytm</option>
                                                <option>PhonePe Wallet</option>
                                                <option>Amazon Pay</option>
                                                <option>MobiKwik</option>
                                            </select>
                                        </div>
                                    </div>

                                    <div id="cashBlock" style="display: none;">
                                        <div class="mb-5 bg-light p-4 rounded-3 text-center border">
                                            <i class="fas fa-money-bill-wave fs-2 text-secondary mb-2"></i>
                                            <h6 class="fw-bold text-dark">Pay at Counter</h6>
                                            <p class="small text-muted mb-0">Your reservation will be confirmed now. You can pay the total amount at the time of pickup.</p>
                                        </div>
                                    </div>

                                    <button type="submit" class="btn btn-primary w-100 py-3 fw-bold rounded-4 shadow-sm">
                                        Confirm & Pay ₹<c:out value="${fn:substringBefore(booking.totalAmount, '.')}" />
                                    </button>
                                    
                                    <div class="text-center mt-4">
                                        <div class="d-flex justify-content-center gap-3 opacity-50">
                                            <i class="fab fa-cc-visa fs-3"></i>
                                            <i class="fab fa-cc-mastercard fs-3"></i>
                                            <i class="fab fa-cc-amex fs-3"></i>
                                        </div>
                                    </div>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <div class="text-center py-5">
                                    <div class="opacity-25 mb-4"><i class="fas fa-shopping-cart fa-5x"></i></div>
                                    <h4 class="fw-bold">No Booking Data</h4>
                                    <p class="text-muted">We couldn't find the reservation you're trying to pay for.</p>
                                    <a href="${pageContext.request.contextPath}/customer/dashboard" class="btn btn-primary px-5 rounded-pill mt-3">Back to Fleet</a>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    
                    <div class="bg-light p-4 text-center border-top">
                        <p class="extra-small text-muted mb-0">
                            <i class="fas fa-lock me-2 text-success"></i> Your transaction is secured with 256-bit SSL encryption.
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="spinner-overlay" id="global-spinner">
        <div class="spinner"></div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function togglePaymentFields(type) {
            document.getElementById('cardDetailsBlock').style.display = 'none';
            document.getElementById('upiBlock').style.display = 'none';
            document.getElementById('netbankingBlock').style.display = 'none';
            document.getElementById('walletBlock').style.display = 'none';
            document.getElementById('cashBlock').style.display = 'none';

            if (type === 'card') document.getElementById('cardDetailsBlock').style.display = 'block';
            if (type === 'upi') document.getElementById('upiBlock').style.display = 'block';
            if (type === 'netbanking') document.getElementById('netbankingBlock').style.display = 'block';
            if (type === 'wallet') document.getElementById('walletBlock').style.display = 'block';
            if (type === 'cash') document.getElementById('cashBlock').style.display = 'block';
        }
        function showSpinner() {
            document.getElementById('global-spinner').style.display = 'flex';
        }
    </script>
</body>
</html>