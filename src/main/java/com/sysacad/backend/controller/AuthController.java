package com.sysacad.backend.controller;

import com.sysacad.backend.config.security.JwtService;
import com.sysacad.backend.dto.auth.AuthResponse;
import com.sysacad.backend.dto.auth.LoginRequest;
import com.sysacad.backend.dto.usuario.UsuarioResponse;
import com.sysacad.backend.modelo.Matriculacion;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.service.MatriculacionService;
import com.sysacad.backend.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class AuthController {

        private final UsuarioService usuarioService;
        private final JwtService jwtService;
        private final MatriculacionService matriculacionService;
        private final com.sysacad.backend.mapper.UsuarioMapper usuarioMapper;

        @PostMapping("/login")
        public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
                
                Usuario usuario = usuarioService.autenticar(request.getIdentificador(), request.getPassword());

                
                String jwtToken = jwtService.generateToken(usuario);

                
                UsuarioResponse usuarioResponse = usuarioMapper.toDTO(usuario);
                usuarioResponse.setTipoIdentificador(request.getTipoIdentificador());

                
                if (usuario.getRol() == RolUsuario.ESTUDIANTE) {
                        List<Matriculacion> estudios = matriculacionService.obtenerCarrerasPorAlumno(usuario.getId());

                        
                        List<UsuarioResponse.InfoCarrera> carrerasInfo = estudios.stream()
                                        .map(e -> new UsuarioResponse.InfoCarrera(
                                                        e.getPlan().getCarrera().getNombre(),
                                                        e.getPlan().getCarrera().getFacultades().stream()
                                                                .findFirst()
                                                                .map(f -> f.getCiudad() + ", " + f.getProvincia())
                                                                .orElse("Sin Facultad"),
                                                        e.getPlan().getId().getNroPlan()))
                                        .collect(Collectors.toList());
                        usuarioResponse.setCarreras(carrerasInfo);

                        
                        estudios.stream()
                                        .map(Matriculacion::getFechaInscripcion)
                                        .min(LocalDate::compareTo)
                                        .ifPresent(fecha -> usuarioResponse.setAnioIngreso(fecha.getYear()));
                }

                AuthResponse authResponse = AuthResponse.builder()
                                .token(jwtToken)
                                .usuario(usuarioResponse)
                                .bootId(JwtService.getBootId())
                                .build();

                return ResponseEntity.ok(authResponse);
        }

        @PostMapping("/forgot-password")
        public ResponseEntity<?> forgotPassword(@RequestBody com.sysacad.backend.dto.auth.ForgotPasswordRequest request) {
                usuarioService.solicitarRecuperacionPassword(request.getIdentificador());
                return ResponseEntity.ok(java.util.Collections.singletonMap("mensaje", 
                        "Si el email existe en nuestro sistema, recibirás las instrucciones para recuperar tu contraseña."));
        }

        @PostMapping("/reset-password")
        public ResponseEntity<?> resetPassword(@RequestBody com.sysacad.backend.dto.auth.ResetPasswordRequest request) {
                usuarioService.resetPassword(request.getToken(), request.getNewPassword());
                return ResponseEntity.ok(java.util.Collections.singletonMap("mensaje", 
                        "La contraseña ha sido restablecida exitosamente."));
        }
}