<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%
    if (session.getAttribute("loggedUser") == null) {
        response.sendRedirect("login");
        return;
    }
%>
<c:set var="pageTitle" value="Command Center - CarRental" scope="request" />
<jsp:include page="WEB-INF/fragments/layout-top.jsp" />
            <%-- Page Header --%>
            <div class="mb-4 animate-in">
                <h2 class="fw-bold mb-1" style="font-weight:800!important; letter-spacing:-.02em">Command Center</h2>
                <p class="text-muted small mb-0">Real-time fleet analytics and performance metrics</p>
            </div>

            <%-- KPI Cards --%>
            <div class="row g-3 mb-4">
                <div class="col-sm-6 col-xl-3 animate-in">
                    <div class="kpi-card" style="border-bottom:3px solid var(--primary)!important">
                        <div class="d-flex justify-content-between align-items-start mb-3">
                            <div class="kpi-icon" style="background:var(--primary-glow);">
                                <i class="fas fa-car" style="color:var(--primary)"></i>
                            </div>
                            <span class="badge rounded-pill" style="background:rgba(59,130,246,.1); color:var(--primary); font-size:.65rem">Fleet</span>
                        </div>
                        <div class="extra-small fw-bold text-uppercase mb-1" style="color:var(--text-muted); letter-spacing:.1em">Total Vehicles</div>
                        <h2 class="mb-0" style="font-weight:800"><c:out value="${totalCars}" default="0" /></h2>
                        <div class="kpi-meta">
                            <span class="trend-pill info"><i class="fas fa-bolt"></i><c:out value="${fleetUtilization}" default="0" />%</span>
                            <span class="extra-small text-muted">Fleet utilization</span>
                        </div>
                    </div>
                </div>
                <div class="col-sm-6 col-xl-3 animate-in">
                    <div class="kpi-card" style="border-bottom:3px solid var(--accent-color)!important">
                        <div class="d-flex justify-content-between align-items-start mb-3">
                            <div class="kpi-icon" style="background:var(--accent-glow);">
                                <i class="fas fa-key" style="color:var(--accent-color)"></i>
                            </div>
                            <span class="badge rounded-pill" style="background:rgba(16,185,129,.1); color:var(--accent-color); font-size:.65rem">Live</span>
                        </div>
                        <div class="extra-small fw-bold text-uppercase mb-1" style="color:var(--text-muted); letter-spacing:.1em">Active Rentals</div>
                        <h2 class="mb-0" style="font-weight:800"><c:out value="${activeRentals}" default="0" /></h2>
                        <div class="kpi-meta">
                            <span class="trend-pill ${bookingTrend}">
                                <i class="fas ${bookingTrendIcon}"></i><c:out value="${bookingGrowthAbs}" default="0" />%
                            </span>
                            <span class="extra-small text-muted">vs last month</span>
                        </div>
                    </div>
                </div>
                <div class="col-sm-6 col-xl-3 animate-in">
                    <div class="kpi-card" style="border-bottom:3px solid var(--success)!important">
                        <div class="d-flex justify-content-between align-items-start mb-3">
                            <div class="kpi-icon" style="background:rgba(34,197,94,.1);">
                                <i class="fas fa-wallet" style="color:var(--success)"></i>
                            </div>
                        </div>
                        <div class="extra-small fw-bold text-uppercase mb-1" style="color:var(--text-muted); letter-spacing:.1em">Total Revenue</div>
                        <h2 class="mb-0" style="font-weight:800">₹<c:out value="${fn:substringBefore(totalRevenue, '.')}" default="0" /></h2>
                        <div class="kpi-meta">
                            <span class="trend-pill ${revenueTrend}">
                                <i class="fas ${revenueTrendIcon}"></i><c:out value="${revenueGrowthAbs}" default="0" />%
                            </span>
                            <span class="extra-small text-muted">month over month</span>
                        </div>
                    </div>
                </div>
                <div class="col-sm-6 col-xl-3 animate-in">
                    <div class="kpi-card" style="border-bottom:3px solid var(--warning)!important">
                        <div class="d-flex justify-content-between align-items-start mb-3">
                            <div class="kpi-icon" style="background:rgba(245,158,11,.1);">
                                <i class="fas fa-users" style="color:var(--warning)"></i>
                            </div>
                        </div>
                        <div class="extra-small fw-bold text-uppercase mb-1" style="color:var(--text-muted); letter-spacing:.1em">Total Customers</div>
                        <h2 class="mb-0" style="font-weight:800"><c:out value="${totalCustomers}" default="0" /></h2>
                        <div class="kpi-meta">
                            <span class="trend-pill ${customerTrend}">
                                <i class="fas ${customerTrendIcon}"></i><c:out value="${customerGrowthAbs}" default="0" />%
                            </span>
                            <span class="extra-small text-muted">new this month</span>
                        </div>
                    </div>
                </div>
            </div>

            <%-- Insight Tiles --%>
            <div class="row g-3 mb-4">
                <div class="col-md-4 animate-in">
                    <div class="mini-kpi-card">
                        <div class="d-flex justify-content-between align-items-center">
                            <span class="mini-kpi-label">Pending Reviews</span>
                            <span class="trend-pill ${reviewTrend}">
                                <i class="fas ${reviewTrendIcon}"></i><c:out value="${reviewGrowthAbs}" default="0" />%
                            </span>
                        </div>
                        <div class="mini-kpi-value"><c:out value="${pendingReviews}" default="0" /></div>
                        <div class="mini-kpi-subtext">Review flow this month</div>
                    </div>
                </div>
                <div class="col-md-4 animate-in">
                    <div class="mini-kpi-card">
                        <div class="d-flex justify-content-between align-items-center">
                            <span class="mini-kpi-label">Available Cars</span>
                            <span class="trend-pill info"><c:out value="${availableShare}" default="0" />%</span>
                        </div>
                        <div class="mini-kpi-value"><c:out value="${availableCars}" default="0" /></div>
                        <div class="mini-kpi-subtext">Ready for booking</div>
                    </div>
                </div>
                <div class="col-md-4 animate-in">
                    <div class="mini-kpi-card">
                        <div class="d-flex justify-content-between align-items-center">
                            <span class="mini-kpi-label">Booked Cars</span>
                            <span class="trend-pill info"><c:out value="${bookedShare}" default="0" />%</span>
                        </div>
                        <div class="mini-kpi-value"><c:out value="${bookedCars}" default="0" /></div>
                        <div class="mini-kpi-subtext">Currently in use</div>
                    </div>
                </div>
            </div>

            <%-- Quick Actions --%>
            <div class="card mb-4 animate-in">
                <div class="card-body p-4">
                    <h6 class="fw-bold mb-3" style="font-size:.85rem"><i class="fas fa-bolt me-2" style="color:var(--warning)"></i>Quick Actions</h6>
                    <div class="d-flex flex-wrap gap-2">
                        <a href="${pageContext.request.contextPath}/admin/addCar" class="btn btn-primary btn-sm">
                            <i class="fas fa-plus me-1"></i>Add Vehicle
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/bookings" class="btn btn-light btn-sm">
                            <i class="fas fa-calendar-check me-1"></i>Manage Bookings
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/reviews" class="btn btn-light btn-sm">
                            <i class="fas fa-star me-1"></i>Moderate Reviews
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/customers" class="btn btn-light btn-sm">
                            <i class="fas fa-users me-1"></i>Customer Directory
                        </a>
                    </div>
                </div>
            </div>

            <%-- Analytics Overview --%>
            <div class="row g-3 mb-4">
                <div class="col-lg-8 animate-in">
                    <div class="card chart-card h-100">
                        <div class="card-header">
                            <div>
                                <h6 class="fw-bold mb-0" style="font-size:.9rem">Revenue Analytics</h6>
                                <span class="extra-small text-muted">Smooth revenue performance trend</span>
                            </div>
                        </div>
                        <div class="card-body">
                            <div class="chart-wrapper chart-lg">
                                <canvas id="revenueChart" class="chart-canvas"></canvas>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-lg-4 animate-in">
                    <div class="card chart-card h-100">
                        <div class="card-header">
                            <div>
                                <h6 class="fw-bold mb-0" style="font-size:.9rem">Booking Status</h6>
                                <span class="extra-small text-muted">Live booking distribution</span>
                            </div>
                        </div>
                        <div class="card-body">
                            <div class="chart-wrapper chart-lg chart-center">
                                <canvas id="bookingStatusChart" class="chart-canvas"></canvas>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row g-3 mb-4">
                <div class="col-lg-4 animate-in">
                    <div class="card chart-card h-100">
                        <div class="card-header">
                            <div>
                                <h6 class="fw-bold mb-0" style="font-size:.9rem">Fleet Categories</h6>
                                <span class="extra-small text-muted">Vehicle mix by segment</span>
                            </div>
                        </div>
                        <div class="card-body">
                            <div class="chart-wrapper chart-md">
                                <canvas id="fleetCategoryChart" class="chart-canvas"></canvas>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-lg-4 animate-in">
                    <div class="card chart-card h-100">
                        <div class="card-header">
                            <div>
                                <h6 class="fw-bold mb-0" style="font-size:.9rem">Monthly Growth</h6>
                                <span class="extra-small text-muted">Bookings momentum</span>
                            </div>
                        </div>
                        <div class="card-body">
                            <div class="chart-wrapper chart-md">
                                <canvas id="monthlyGrowthChart" class="chart-canvas"></canvas>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-lg-4 animate-in">
                    <div class="card chart-card h-100">
                        <div class="card-header">
                            <div>
                                <h6 class="fw-bold mb-0" style="font-size:.9rem">Payments</h6>
                                <span class="extra-small text-muted">Method distribution</span>
                            </div>
                        </div>
                        <div class="card-body">
                            <div class="chart-wrapper chart-md chart-center">
                                <canvas id="paymentsChart" class="chart-canvas"></canvas>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row g-3 mb-4">
                <div class="col-lg-6 animate-in">
                    <div class="card chart-card h-100">
                        <div class="card-header">
                            <div>
                                <h6 class="fw-bold mb-0" style="font-size:.9rem">Customer Growth</h6>
                                <span class="extra-small text-muted">New customers per month</span>
                            </div>
                        </div>
                        <div class="card-body">
                            <div class="chart-wrapper chart-sm">
                                <canvas id="customerGrowthChart" class="chart-canvas"></canvas>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-lg-6 animate-in">
                    <div class="card chart-card h-100">
                        <div class="card-header">
                            <div>
                                <h6 class="fw-bold mb-0" style="font-size:.9rem">Review Ratings</h6>
                                <span class="extra-small text-muted">Customer sentiment snapshot</span>
                            </div>
                        </div>
                        <div class="card-body">
                            <div class="chart-wrapper chart-sm">
                                <canvas id="reviewRatingsChart" class="chart-canvas"></canvas>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <%-- Recent Bookings Table --%>
            <div class="card animate-in">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <div>
                        <h6 class="fw-bold mb-0" style="font-size:.9rem">Recent Bookings</h6>
                        <span class="extra-small text-muted">Latest reservation activity</span>
                    </div>
                    <a href="${pageContext.request.contextPath}/admin/bookings" class="btn btn-sm btn-light">View All <i class="fas fa-arrow-right ms-1"></i></a>
                </div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0">
                            <thead>
                                <tr>
                                    <th class="ps-4">ID</th>
                                    <th>Customer</th>
                                    <th>Vehicle</th>
                                    <th>Amount</th>
                                    <th>Status</th>
                                    <th class="pe-4 text-end">Date</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${not empty recentBookings}">
                                        <c:forEach var="b" items="${recentBookings}">
                                            <tr>
                                                <td class="ps-4">
                                                    <span class="fw-bold" style="color:var(--primary)">#<c:out value="${b.id}" /></span>
                                                </td>
                                                <td>
                                                    <span class="fw-bold small"><c:out value="${b.customerName}" /></span>
                                                </td>
                                                <td>
                                                    <span class="small"><c:out value="${b.carDetails}" /></span>
                                                </td>
                                                <td>
                                                    <span class="fw-bold">₹<c:out value="${fn:substringBefore(b.totalAmount, '.')}" /></span>
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${b.status == 'CONFIRMED'}">
                                                            <span class="status-badge status-confirmed"><i class="fas fa-check-circle me-1"></i>Confirmed</span>
                                                        </c:when>
                                                        <c:when test="${b.status == 'PENDING'}">
                                                            <span class="status-badge status-pending"><i class="fas fa-clock me-1"></i>Pending</span>
                                                        </c:when>
                                                        <c:when test="${b.status == 'CANCELLED'}">
                                                            <span class="status-badge status-cancelled"><i class="fas fa-times-circle me-1"></i>Cancelled</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="status-badge status-completed"><c:out value="${b.status}" /></span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td class="pe-4 text-end">
                                                    <span class="extra-small text-muted"><c:out value="${fn:substring(b.bookingDate, 0, 10)}" /></span>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr><td colspan="6" class="text-center py-5 text-muted">
                                            <i class="fas fa-inbox d-block mb-2" style="font-size:1.5rem; opacity:.3"></i>
                                            No recent bookings
                                        </td></tr>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
<c:set var="extraScripts" scope="request">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>
document.addEventListener('DOMContentLoaded', function() {
    if (typeof Chart === 'undefined') return;

    const revenueLabels = [<c:forEach var="e" items="${monthlyRevenue}">'${e.key}',</c:forEach>];
    const revenueData = [<c:forEach var="e" items="${monthlyRevenue}">${e.value},</c:forEach>];
    const bookingStatusLabels = [<c:forEach var="e" items="${bookingStatusDist}">'${e.key}',</c:forEach>];
    const bookingStatusData = [<c:forEach var="e" items="${bookingStatusDist}">${e.value},</c:forEach>];
    const fleetLabels = [<c:forEach var="e" items="${categoryDist}">'${e.key}',</c:forEach>];
    const fleetData = [<c:forEach var="e" items="${categoryDist}">${e.value},</c:forEach>];
    const monthlyBookingLabels = [<c:forEach var="e" items="${monthlyBookings}">'${e.key}',</c:forEach>];
    const monthlyBookingData = [<c:forEach var="e" items="${monthlyBookings}">${e.value},</c:forEach>];
    const paymentLabels = [<c:forEach var="e" items="${paymentMethodDist}">'${e.key}',</c:forEach>];
    const paymentData = [<c:forEach var="e" items="${paymentMethodDist}">${e.value},</c:forEach>];
    const customerLabels = [<c:forEach var="e" items="${monthlyNewCustomers}">'${e.key}',</c:forEach>];
    const customerData = [<c:forEach var="e" items="${monthlyNewCustomers}">${e.value},</c:forEach>];
    const reviewLabels = [<c:forEach var="e" items="${reviewRatingDist}">'${e.key}',</c:forEach>];
    const reviewData = [<c:forEach var="e" items="${reviewRatingDist}">${e.value},</c:forEach>];

    const numberFormatter = new Intl.NumberFormat('en-IN');
    const compactFormatter = new Intl.NumberFormat('en-IN', { notation: 'compact', maximumFractionDigits: 1 });
    const currencyFormatter = new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 });

    const cssVar = (name) => getComputedStyle(document.documentElement).getPropertyValue(name).trim();

    function themeTokens() {
        return {
            primary: cssVar('--accent-color') || '#3B82F6',
            success: cssVar('--success') || '#10B981',
            warning: cssVar('--warning') || '#F59E0B',
            danger: cssVar('--danger') || '#EF4444',
            text: cssVar('--text-primary') || '#111827',
            muted: cssVar('--text-muted') || '#6b7280',
            border: cssVar('--border-color') || '#e2e8f0',
            surface: cssVar('--card-color') || '#ffffff',
            grid: cssVar('--chart-grid') || 'rgba(15,23,42,0.08)',
            tick: cssVar('--chart-tick') || cssVar('--text-muted') || '#64748B',
            tooltipBg: cssVar('--tooltip-bg') || '#ffffff',
            tooltipTitle: cssVar('--tooltip-title') || '#0f172a',
            tooltipBody: cssVar('--tooltip-body') || '#475569'
        };
    }

    function formatMonthLabel(label) {
        if (!label) return '';
        if (/^\d{4}-\d{2}$/.test(label)) {
            const parts = label.split('-');
            const date = new Date(Number(parts[0]), Number(parts[1]) - 1, 1);
            return date.toLocaleDateString('en-IN', { month: 'short', year: 'numeric' });
        }
        return label;
    }

    function toRgba(color, alpha) {
        if (color.startsWith('#')) {
            const bigint = parseInt(color.slice(1), 16);
            const r = (bigint >> 16) & 255;
            const g = (bigint >> 8) & 255;
            const b = bigint & 255;
            return 'rgba(' + r + ',' + g + ',' + b + ',' + alpha + ')';
        }
        if (color.startsWith('rgb(')) {
            return color.replace('rgb(', 'rgba(').replace(')', ',' + alpha + ')');
        }
        return color;
    }

    function buildLineGradient(chart, color, alpha) {
        const { ctx, chartArea } = chart;
        if (!chartArea) return toRgba(color, alpha);
        const gradient = ctx.createLinearGradient(0, chartArea.top, 0, chartArea.bottom);
        gradient.addColorStop(0, toRgba(color, alpha));
        gradient.addColorStop(1, toRgba(color, 0));
        return gradient;
    }

    function buildBarGradient(chart, color) {
        const { ctx, chartArea } = chart;
        if (!chartArea) return color;
        const gradient = ctx.createLinearGradient(0, chartArea.bottom, 0, chartArea.top);
        gradient.addColorStop(0, toRgba(color, 0.2));
        gradient.addColorStop(1, toRgba(color, 0.9));
        return gradient;
    }

    Chart.defaults.font.family = 'Inter, system-ui, -apple-system, sans-serif';
    Chart.defaults.animation.duration = 1200;
    Chart.defaults.animation.easing = 'easeOutQuart';

    const charts = {};

    function createOrUpdateChart(id, config) {
        const canvas = document.getElementById(id);
        if (!canvas) return;
        const existing = Chart.getChart(canvas);
        if (existing) {
            existing.data = config.data;
            existing.options = config.options;
            existing.update();
            charts[id] = existing;
            return;
        }
        charts[id] = new Chart(canvas, config);
    }

    function baseTooltip(formatter, formatTitle) {
        const colors = themeTokens();
        return {
            backgroundColor: colors.tooltipBg,
            titleColor: colors.tooltipTitle,
            bodyColor: colors.tooltipBody,
            borderColor: colors.border,
            borderWidth: 1,
            cornerRadius: 12,
            padding: 12,
            displayColors: false,
            titleFont: { family: 'Inter', size: 12, weight: '600' },
            bodyFont: { family: 'Inter', size: 11 },
            callbacks: {
                title: (items) => formatTitle ? formatTitle(items[0].label) : items[0].label,
                label: (context) => formatter(context)
            }
        };
    }

    function buildCharts() {
        const colors = themeTokens();
        const palette = [
            colors.primary,
            colors.success,
            colors.warning,
            colors.danger,
            '#8B5CF6',
            '#06B6D4',
            '#F97316'
        ];

        createOrUpdateChart('revenueChart', {
            type: 'line',
            data: {
                labels: revenueLabels,
                datasets: [{
                    label: 'Revenue',
                    data: revenueData,
                    borderColor: colors.primary,
                    backgroundColor: (context) => buildLineGradient(context.chart, colors.primary, 0.35),
                    fill: true,
                    tension: 0.45,
                    borderWidth: 2.5,
                    pointRadius: 0,
                    pointHoverRadius: 5,
                    pointHoverBackgroundColor: colors.primary,
                    pointHoverBorderColor: colors.surface,
                    pointHoverBorderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                resizeDelay: 150,
                interaction: { mode: 'index', intersect: false },
                layout: { padding: { top: 6, right: 12, left: 4, bottom: 0 } },
                plugins: {
                    legend: { display: false },
                    tooltip: baseTooltip(
                        (ctx) => currencyFormatter.format(ctx.raw),
                        formatMonthLabel
                    )
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        grid: { color: colors.grid, drawBorder: false },
                        ticks: {
                            color: colors.tick,
                            font: { size: 11 },
                            callback: (value) => compactFormatter.format(value)
                        }
                    },
                    x: {
                        grid: { display: false },
                        ticks: {
                            color: colors.tick,
                            font: { size: 11 },
                            callback: (value, index) => formatMonthLabel(revenueLabels[index])
                        }
                    }
                }
            }
        });

        createOrUpdateChart('bookingStatusChart', {
            type: 'doughnut',
            data: {
                labels: bookingStatusLabels,
                datasets: [{
                    data: bookingStatusData,
                    backgroundColor: palette,
                    borderWidth: 0,
                    hoverOffset: 8,
                    borderRadius: 10,
                    spacing: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                cutout: '72%',
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: {
                            color: colors.tick,
                            usePointStyle: true,
                            pointStyle: 'circle',
                            padding: 14,
                            font: { size: 11, weight: '600' }
                        }
                    },
                    tooltip: baseTooltip(
                        (ctx) => ctx.label + ': ' + numberFormatter.format(ctx.raw)
                    )
                }
            }
        });

        createOrUpdateChart('fleetCategoryChart', {
            type: 'bar',
            data: {
                labels: fleetLabels,
                datasets: [{
                    label: 'Cars',
                    data: fleetData,
                    backgroundColor: (context) => buildBarGradient(context.chart, colors.primary),
                    borderRadius: 10,
                    borderSkipped: false,
                    barThickness: 26
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                resizeDelay: 150,
                plugins: {
                    legend: { display: false },
                    tooltip: baseTooltip((ctx) => numberFormatter.format(ctx.raw))
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        grid: { color: colors.grid, drawBorder: false },
                        ticks: { color: colors.tick, font: { size: 11 } }
                    },
                    x: {
                        grid: { display: false },
                        ticks: { color: colors.tick, font: { size: 11 } }
                    }
                }
            }
        });

        createOrUpdateChart('monthlyGrowthChart', {
            type: 'line',
            data: {
                labels: monthlyBookingLabels,
                datasets: [{
                    label: 'Bookings',
                    data: monthlyBookingData,
                    borderColor: colors.success,
                    backgroundColor: (context) => buildLineGradient(context.chart, colors.success, 0.35),
                    fill: true,
                    tension: 0.45,
                    borderWidth: 2.5,
                    pointRadius: 0,
                    pointHoverRadius: 5,
                    pointHoverBackgroundColor: colors.success,
                    pointHoverBorderColor: colors.surface,
                    pointHoverBorderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                resizeDelay: 150,
                interaction: { mode: 'index', intersect: false },
                plugins: {
                    legend: { display: false },
                    tooltip: baseTooltip(
                        (ctx) => numberFormatter.format(ctx.raw),
                        formatMonthLabel
                    )
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        grid: { color: colors.grid, drawBorder: false },
                        ticks: { color: colors.tick, font: { size: 11 } }
                    },
                    x: {
                        grid: { display: false },
                        ticks: {
                            color: colors.tick,
                            font: { size: 11 },
                            callback: (value, index) => formatMonthLabel(monthlyBookingLabels[index])
                        }
                    }
                }
            }
        });

        createOrUpdateChart('paymentsChart', {
            type: 'doughnut',
            data: {
                labels: paymentLabels,
                datasets: [{
                    data: paymentData,
                    backgroundColor: palette,
                    borderWidth: 0,
                    hoverOffset: 8,
                    borderRadius: 10,
                    spacing: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                cutout: '70%',
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: {
                            color: colors.muted,
                            usePointStyle: true,
                            pointStyle: 'circle',
                            padding: 12,
                            font: { size: 11, weight: '600' }
                        }
                    },
                    tooltip: baseTooltip(
                        (ctx) => ctx.label + ': ' + numberFormatter.format(ctx.raw)
                    )
                }
            }
        });

        createOrUpdateChart('customerGrowthChart', {
            type: 'line',
            data: {
                labels: customerLabels,
                datasets: [{
                    label: 'Customers',
                    data: customerData,
                    borderColor: '#8B5CF6',
                    backgroundColor: (context) => buildLineGradient(context.chart, '#8B5CF6', 0.3),
                    fill: true,
                    tension: 0.45,
                    borderWidth: 2.5,
                    pointRadius: 0,
                    pointHoverRadius: 4,
                    pointHoverBackgroundColor: '#8B5CF6',
                    pointHoverBorderColor: colors.surface,
                    pointHoverBorderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                resizeDelay: 150,
                plugins: {
                    legend: { display: false },
                    tooltip: baseTooltip(
                        (ctx) => numberFormatter.format(ctx.raw),
                        formatMonthLabel
                    )
                },
                scales: {
                    y: { display: false },
                    x: { display: false }
                }
            }
        });

        createOrUpdateChart('reviewRatingsChart', {
            type: 'bar',
            data: {
                labels: reviewLabels.map(label => label + '★'),
                datasets: [{
                    label: 'Ratings',
                    data: reviewData,
                    backgroundColor: (context) => buildBarGradient(context.chart, colors.warning),
                    borderRadius: 10,
                    borderSkipped: false,
                    barThickness: 28
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                resizeDelay: 150,
                plugins: {
                    legend: { display: false },
                    tooltip: baseTooltip((ctx) => numberFormatter.format(ctx.raw))
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        grid: { color: colors.grid, drawBorder: false },
                        ticks: { color: colors.tick, font: { size: 11 } }
                    },
                    x: {
                        grid: { display: false },
                        ticks: { color: colors.tick, font: { size: 11 } }
                    }
                }
            }
        });
    }

    buildCharts();

    const themeObserver = new MutationObserver(() => {
        buildCharts();
    });
    themeObserver.observe(document.documentElement, { attributes: true, attributeFilter: ['data-theme'] });
});
</script>
</c:set>
<jsp:include page="WEB-INF/fragments/layout-bottom.jsp" />