package com.internshiptracker;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class CsrfFilterTest {

    @Test
    void getRequestShouldBypassCsrfValidation() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        CsrfFilter csrfFilter = new CsrfFilter();
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        csrfFilter.doFilter(request,response,filterChain);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void postRequestWithoutCsrfTokenShouldBeForbidden() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("POST");
        when(request.getServletPath()).thenReturn("/applications");
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(4L);
        CsrfFilter csrfFilter = new CsrfFilter();
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        csrfFilter.doFilter(request,response,filterChain);
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN);
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void postRequestWithValidCsrfTokenShouldPass() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("POST");
        when(request.getServletPath()).thenReturn("/applications");
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(4L);
        when(session.getAttribute("csrfToken")).thenReturn("valid-token-123");
        when(request.getHeader("X-CSRF-Token")).thenReturn("valid-token-123");
        CsrfFilter csrfFilter = new CsrfFilter();
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        csrfFilter.doFilter(request,response,filterChain);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void postRequestWithInvalidCsrfTokenShouldReject() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("POST");
        when(request.getServletPath()).thenReturn("/applications");
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("userId")).thenReturn(4L);
        when(session.getAttribute("csrfToken")).thenReturn("valid-token-123");
        when(request.getHeader("X-CSRF-Token")).thenReturn("Invalid-token-123");
        CsrfFilter csrfFilter = new CsrfFilter();
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        csrfFilter.doFilter(request,response,filterChain);
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN);
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void noAuthenticatedSessionShouldReject() throws ServletException, IOException{
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("POST");
        when(request.getServletPath()).thenReturn("/applications");
        when(request.getSession(false)).thenReturn(null);
        CsrfFilter csrfFilter = new CsrfFilter();
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        csrfFilter.doFilter(request,response,filterChain);
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void sessionWithoutUserIdShouldReject() throws ServletException, IOException{
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("POST");
        when(request.getServletPath()).thenReturn("/applications");
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);

        CsrfFilter csrfFilter = new CsrfFilter();
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        csrfFilter.doFilter(request,response,filterChain);
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void postLoginShouldBypassCsrfValidation() throws ServletException, IOException{
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("POST");
        when(request.getServletPath()).thenReturn("/auth/login");
        CsrfFilter csrfFilter = new CsrfFilter();
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        csrfFilter.doFilter(request,response,filterChain);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void postRegisterShouldBypassCsrfValidation() throws ServletException, IOException{
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("POST");
        when(request.getServletPath()).thenReturn("/auth/register");
        CsrfFilter csrfFilter = new CsrfFilter();
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        csrfFilter.doFilter(request,response,filterChain);
        verify(filterChain).doFilter(request, response);
    }
}
