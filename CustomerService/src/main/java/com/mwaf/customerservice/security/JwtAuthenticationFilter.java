package com.mwaf.customerservice.security;

import com.mwaf.customerservice.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    /* ---------- 1.  Skip JWT processing for public endpoints ---------- */

    private static final Pattern POST_CUSTOMER      = Pattern.compile("^/api/customers$");
    private static final Pattern GET_BY_USER_ID     = Pattern.compile("^/api/customers/byUserId/\\d+$");

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path   = request.getServletPath();   // e.g. "/api/customers"
        String method = request.getMethod();        // e.g. "POST"

        // allow: POST /api/customers
        if ("POST".equals(method) && POST_CUSTOMER.matcher(path).matches()) {
            return true;
        }

        // allow: GET /api/customers/byUserId/{id}
        if ("GET".equals(method) && GET_BY_USER_ID.matcher(path).matches()) {
            return true;
        }

        // filter everything else
        return false;
    }

    /* ---------- 2.  Normal JWT processing for the remaining requests ---------- */

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // No header or not a Bearer token → let Spring Security handle it later
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String userId      = jwtUtil.getUserIdFromToken(token);
            List<String> roles = jwtUtil.getRolesFromToken(token);

            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception ex) {
            // invalid token → clear context but keep processing (Spring will reject later)
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
