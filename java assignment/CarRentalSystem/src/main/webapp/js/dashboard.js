document.addEventListener("DOMContentLoaded", function() {
    if (typeof Chart === 'undefined') return;

    const cssVar = (name) => getComputedStyle(document.documentElement).getPropertyValue(name).trim();

    function themeTokens() {
        return {
            primary: cssVar('--accent-color') || '#3B82F6',
            success: cssVar('--success') || '#10B981',
            warning: cssVar('--warning') || '#F59E0B',
            danger: cssVar('--danger') || '#EF4444',
            muted: cssVar('--text-muted') || '#6b7280',
            border: cssVar('--border-color') || '#e2e8f0',
            grid: cssVar('--chart-grid') || 'rgba(15,23,42,0.08)',
            tick: cssVar('--chart-tick') || cssVar('--text-muted') || '#64748B',
            tooltipBg: cssVar('--tooltip-bg') || '#ffffff',
            tooltipTitle: cssVar('--tooltip-title') || '#0f172a',
            tooltipBody: cssVar('--tooltip-body') || '#475569'
        };
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

    function baseTooltip(formatter) {
        const colors = themeTokens();
        return {
            backgroundColor: colors.tooltipBg,
            titleColor: colors.tooltipTitle,
            bodyColor: colors.tooltipBody,
            borderColor: colors.border,
            borderWidth: 1,
            cornerRadius: 10,
            padding: 10,
            displayColors: false,
            callbacks: {
                label: formatter
            }
        };
    }

    const numberFormatter = new Intl.NumberFormat('en-IN');
    const currencyFormatter = new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 });
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

        if (typeof categoryLabels !== 'undefined') {
            createOrUpdateChart('categoryDistChart', {
                type: 'doughnut',
                data: {
                    labels: categoryLabels,
                    datasets: [{
                        data: categoryData,
                        backgroundColor: palette,
                        hoverOffset: 6,
                        borderWidth: 0,
                        borderRadius: 8,
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
                                padding: 14
                            }
                        },
                        tooltip: baseTooltip((ctx) => ctx.label + ': ' + numberFormatter.format(ctx.raw))
                    }
                }
            });
        }

        if (typeof statusLabels !== 'undefined') {
            createOrUpdateChart('bookingStatusChart', {
                type: 'bar',
                data: {
                    labels: statusLabels,
                    datasets: [{
                        label: 'Bookings',
                        data: statusData,
                        backgroundColor: colors.primary,
                        borderRadius: 8,
                        barThickness: 28
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: {
                        y: {
                            beginAtZero: true,
                            grid: { color: colors.grid },
                            ticks: { color: colors.tick }
                        },
                        x: {
                            grid: { display: false },
                            ticks: { color: colors.tick }
                        }
                    },
                    plugins: {
                        legend: { display: false },
                        tooltip: baseTooltip((ctx) => numberFormatter.format(ctx.raw))
                    }
                }
            });
        }

        if (typeof monthlyRevenueLabels !== 'undefined') {
            createOrUpdateChart('monthlyRevenueChart', {
                type: 'line',
                data: {
                    labels: monthlyRevenueLabels,
                    datasets: [{
                        label: 'Revenue (₹)',
                        data: monthlyRevenueData,
                        borderColor: colors.success,
                        backgroundColor: (context) => buildLineGradient(context.chart, colors.success, 0.3),
                        tension: 0.4,
                        fill: true,
                        borderWidth: 2.5,
                        pointRadius: 0,
                        pointHoverRadius: 4
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: {
                        y: {
                            beginAtZero: true,
                            grid: { color: colors.grid },
                            ticks: { color: colors.tick }
                        },
                        x: { grid: { display: false }, ticks: { color: colors.tick } }
                    },
                    plugins: {
                        legend: { display: false },
                        tooltip: baseTooltip((ctx) => currencyFormatter.format(ctx.raw))
                    }
                }
            });
        }
    }

    buildCharts();

    const themeObserver = new MutationObserver(() => {
        buildCharts();
    });
    themeObserver.observe(document.documentElement, { attributes: true, attributeFilter: ['data-theme'] });
});