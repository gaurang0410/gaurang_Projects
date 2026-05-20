package filter;

import model.User;
import utils.SessionUtil;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AuthFilter implements Filter {

    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
            "/login", "/register", "/index.jsp", "/css/", "/js/", "/images/", "/error.jsp", "/forgotPassword", "/resetPassword"
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        // Check if path is excluded
        boolean isExcluded = EXCLUDED_PATHS.stream().anyMatch(path::startsWith) || path.equals("/") || path.isEmpty();

        if (isExcluded) {
            chain.doFilter(request, response);
            return;
        }

        User user = SessionUtil.getLoggedUser(httpRequest);
        if (user == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        // Role-based access control
        if (isAdminPath(path)) {
            if (!"ADMIN".equals(user.getRole())) {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/customer/dashboard");
                return;
            }
        } else if (isCustomerPath(path)) {
            // Allow ADMIN to access customer invoice route
            if (!"CUSTOMER".equals(user.getRole()) && !(path.startsWith("/customer/invoice") && "ADMIN".equals(user.getRole()))) {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/admin/dashboard");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private boolean isAdminPath(String path) {
        return path.startsWith("/admin/");
    }

    private boolean isCustomerPath(String path) {
        return path.startsWith("/customer/");
    }

    @Override
    public void destroy() {}
}
