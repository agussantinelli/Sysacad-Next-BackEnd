package com.sysacad.backend.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userLegajo;

        // 1. Validar que la cabecera exista y tenga el formato correcto
        // FIX: Agregamos validación de longitud para evitar "Bearer " vacío
        if (authHeader == null || !authHeader.startsWith("Bearer ") || authHeader.length() <= 7) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 2. Extraer token
            jwt = authHeader.substring(7);

            // 3. Extraer usuario (Lanzará excepción si el token está mal formado, la capturamos abajo)
            userLegajo = jwtService.extractUsername(jwt);

            // 4. Validar seguridad
            if (userLegajo != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userLegajo);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Logueamos el error pero NO detenemos la cadena violentamente.
            // Si el token es inválido, simplemente el usuario seguirá como "no autenticado" (null)
            // y Spring Security devolverá 403 Forbidden más adelante.
            System.out.println("Error procesando JWT: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}