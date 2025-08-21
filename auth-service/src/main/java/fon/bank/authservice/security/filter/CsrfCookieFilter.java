package fon.bank.authservice.security.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.web.csrf.CsrfToken;
import java.io.IOException;

public class CsrfCookieFilter implements Filter {
    @Override public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        chain.doFilter(req, res);
    }
}
