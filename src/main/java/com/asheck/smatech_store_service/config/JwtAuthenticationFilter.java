package com.asheck.smatech_store_service.config;

import com.asheck.smatech_store_service.helper.RestTemplateService;
import com.asheck.smatech_store_service.user.UserDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final RestTemplateService restTemplateService;


    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        try {
            // Call microservice to validate token and fetch user details
            UserDto userDto = restTemplateService.getAuthenticatedUser(jwt);

            if (userDto == null || !userDto.enabled()) {
                throw new ResponseStatusException(
                        org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid or disabled account"
                );
            }

            // Convert authorities from UserDto to SimpleGrantedAuthority
            List<SimpleGrantedAuthority> authorities = userDto.authorities().stream()
                    .map(auth -> new SimpleGrantedAuthority(auth.authority()))
                    .toList();

            // Create UserDetails object
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    userDto.email(), // Username
                    "", // Password not needed for token-based auth
                    userDto.accountNonExpired(),
                    userDto.credentialsNonExpired(),
                    true,
                    userDto.accountNonLocked(),
                    authorities
            );

            // Set authentication context
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            return;
        }

        filterChain.doFilter(request, response);
    }


}
