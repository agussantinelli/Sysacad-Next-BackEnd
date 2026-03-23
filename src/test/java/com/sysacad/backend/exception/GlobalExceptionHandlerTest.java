package com.sysacad.backend.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Debe manejar ResourceNotFoundException con status 404")
    void handleResourceNotFoundException_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/test/resource-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Recurso no encontrado")))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.path", containsString("/test/resource-not-found")));
    }

    @Test
    @DisplayName("Debe manejar BusinessLogicException con status 400")
    void handleBusinessLogicException_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/test/business-logic"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("Error de lógica")))
                .andExpect(jsonPath("$.path", containsString("/test/business-logic")));
    }

    @Test
    @DisplayName("Debe manejar AccessDeniedException con status 403 cuando el usuario está autenticado")
    void handleAccessDeniedException_ShouldReturn403() throws Exception {
        try (MockedStatic<SecurityContextHolder> utilities = Mockito.mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = Mockito.mock(SecurityContext.class);
            Authentication authentication = Mockito.mock(Authentication.class);
            
            utilities.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
            
            mockMvc.perform(get("/test/access-denied"))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.status", is(403)))
                    .andExpect(jsonPath("$.error", is("Forbidden")))
                    .andExpect(jsonPath("$.message", containsString("No tienes permisos suficientes")));
        }
    }

    @Test
    @DisplayName("Debe manejar AccessDeniedException con status 401 cuando el usuario no está autenticado")
    void handleAccessDeniedException_ShouldReturn401_WhenAnonymous() throws Exception {
        SecurityContextHolder.clearContext();
        mockMvc.perform(get("/test/access-denied"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.error", is("Unauthorized")))
                .andExpect(jsonPath("$.message", containsString("No estás autenticado")));
    }

    @Test
    @DisplayName("Debe manejar RuntimeException genérica con status 500")
    void handleRuntimeException_ShouldReturn500() throws Exception {
        mockMvc.perform(get("/test/runtime-exception"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.message", is("Error inesperado de runtime")));
    }

    @Test
    @DisplayName("Debe manejar Exception genérica con status 500")
    void handleGlobalException_ShouldReturn500() throws Exception {
        mockMvc.perform(get("/test/checked-exception"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.message", containsString("Ocurrió un error inesperado")));
    }

    @RestController
    static class TestController {
        @GetMapping("/test/resource-not-found")
        void throwResourceNotFound() {
            throw new ResourceNotFoundException("Recurso no encontrado");
        }

        @GetMapping("/test/business-logic")
        void throwBusinessLogic() {
            throw new BusinessLogicException("Error de lógica");
        }

        @GetMapping("/test/access-denied")
        void throwAccessDenied() {
            throw new AccessDeniedException("Forbidden");
        }

        @GetMapping("/test/runtime-exception")
        void throwRuntime() {
            throw new RuntimeException("Error inesperado de runtime");
        }
        
        @GetMapping("/test/checked-exception")
        void throwChecked() throws Exception {
            throw new Exception("Error base");
        }
    }
}
