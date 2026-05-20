package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class SecurityHeadersFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        if (response instanceof HttpServletResponse) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            HttpServletRequest httpRequest = (HttpServletRequest) request;

            httpResponse.setHeader("X-Content-Type-Options", "nosniff");
            httpResponse.setHeader("X-Frame-Options", "DENY");
            httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
            httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

            // Cache-Control: no-store for authenticated pages
            String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
            if (isProtectedPath(path)) {
                httpResponse.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                httpResponse.setHeader("Pragma", "no-cache");
            }
        }
        
        chain.doFilter(request, response);
    }

    private boolean isProtectedPath(String path) {
        return path.startsWith("/dashboard") || 
               path.startsWith("/customerDashboard") || 
               path.startsWith("/settings") || 
               path.startsWith("/manage") || 
               path.startsWith("/viewCars") || 
               path.startsWith("/myBookings");
    }

    @Override
    public void destroy() {}
}
