package com.internshiptracker;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Component
public class CsrfFilter extends OncePerRequestFilter {
    private static final Logger logger =
            LoggerFactory.getLogger(CsrfFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        boolean safeMethod = "GET".equals(request.getMethod()) || "HEAD".equals(request.getMethod())
                || "OPTIONS".equals(request.getMethod()) || "TRACE".equals(request.getMethod());

        if(safeMethod) {
            filterChain.doFilter(request, response);
            return;
        }
        String path = request.getServletPath();
        boolean authEndpoint = "POST".equals(request.getMethod())
                && ("/auth/login".equals(path) || "/auth/register".equals(path));
        if(authEndpoint){
            filterChain.doFilter(request, response);
            return;
        }
        HttpSession session = request.getSession(false);

        if(session == null || session.getAttribute("userId") == null){
            logger.warn("Unauthenticated request rejected");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String requestToken = request.getHeader("X-CSRF-Token");
        String sessionToken = (String)session.getAttribute("csrfToken");
        if(requestToken == null || sessionToken == null || !requestToken.equals(sessionToken)){
            logger.warn("CSRF validation failed");
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        filterChain.doFilter(request, response);
    }
}