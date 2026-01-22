package com.sysacad.backend.controller;

import com.sysacad.backend.config.JwtService;
import com.sysacad.backend.dto.AuthResponse;
import com.sysacad.backend.dto.LoginRequest;
import com.sysacad.backend.dto.UsuarioResponse;
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

        @PostMapping("/login")
        public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
                // 1. Autenticar
                Usuario usuario = usuarioService.autenticar(request.getIdentificador(), request.getPassword());

                // 2. Token
                String jwtToken = jwtService.generateToken(usuario);

                // 3. Response Base
                UsuarioResponse usuarioResponse = new UsuarioResponse(usuario);
                usuarioResponse.setTipoIdentificador(request.getTipoIdentificador());

                // 4. Enriquecer si es estudiante
                if (usuario.getRol() == RolUsuario.ESTUDIANTE) {
                        List<Matriculacion> estudios = matriculacionService.obtenerCarrerasPorAlumno(usuario.getId());

                        // Carreras
                        List<UsuarioResponse.InfoCarrera> carrerasInfo = estudios.stream()
                                        .map(e -> new UsuarioResponse.InfoCarrera(
                                                        e.getPlan().getCarrera().getNombre(),
                                                        e.getPlan().getCarrera().getFacultades().stream()
                                                                .findFirst()
                                                                .map(f -> f.getCiudad() + ", " + f.getProvincia())
                                                                .orElse("Sin Facultad")))
                                        .collect(Collectors.toList());
                        usuarioResponse.setCarreras(carrerasInfo);

                        // Año Ingreso (Mínimo)
                        estudios.stream()
                                        .map(Matriculacion::getFechaInscripcion)
                                        .min(LocalDate::compareTo)
                                        .ifPresent(fecha -> usuarioResponse.setAnioIngreso(fecha.getYear()));
                }

                AuthResponse authResponse = AuthResponse.builder()
                                .token(jwtToken)
                                .usuario(usuarioResponse)
                                .build();

                return ResponseEntity.ok(authResponse);
        }
}