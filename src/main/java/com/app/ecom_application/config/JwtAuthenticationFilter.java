package com.app.ecom_application.config;

import com.app.ecom_application.Service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        System.out.println("REQUEST URI: " + request.getRequestURI());
        System.out.println("REQUEST METHOD: " + request.getMethod());

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            if (jwtService.isTokenValid(token)) {
                String email = jwtService.extractUsername(token);
                String role = jwtService.extractRole(token);
                if (role == null || role.isBlank()) {
                    role = "CUSTOMER";
                }
                UserDetails userDetails = User.withUsername(email).password("")
                    .authorities("ROLE_" + role)
                    .build();
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                request.setAttribute("userId", jwtService.extractUserId(token));
                System.out.println("JWT USER: " + email);
                System.out.println("JWT ROLE: " + role);
                System.out.println("AUTHORITIES: " + userDetails.getAuthorities());
                System.out.println("AUTHENTICATED: " +
                        SecurityContextHolder.getContext().getAuthentication().isAuthenticated());

                Long userId = jwtService.extractUserId(token);

                System.out.println("JWT USER ID: " + userId);

                request.setAttribute("userId", userId);
            }
        }

        filterChain.doFilter(request, response);
    }
}
