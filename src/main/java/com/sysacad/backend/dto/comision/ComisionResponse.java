package com.sysacad.backend.dto.comision;

import com.sysacad.backend.modelo.Comision;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class ComisionResponse {
    private UUID id;
    private String nombre;
    private String turno;
    private Integer anio;
    private Integer nivel;

    private String nombreCarrera;
    private UUID idCarrera;


    private String nombreSalon;
    private String ubicacionSalon; // Ej: "Aula 302 - Facultad Rosario"

    // Listas simplificadas
    private List<String> materiasNombres;
    private List<ProfesorResumenDTO> profesores;
    
    // Lista detallada solicitada
    private List<MateriaDetalleDTO> materiasDetalle;

    public ComisionResponse(Comision comision) {
        this.id = comision.getId();
        this.nombre = comision.getNombre();
        this.turno = comision.getTurno();
        this.anio = comision.getAnio();
        this.nivel = comision.getNivel();

        if (comision.getCarrera() != null) {
            this.nombreCarrera = comision.getCarrera().getNombre();
            this.idCarrera = comision.getCarrera().getId();
        }


        if (comision.getSalon() != null) {
            this.nombreSalon = comision.getSalon().getNombre();
            this.ubicacionSalon = comision.getSalon().getPiso() + ", " +
                    comision.getSalon().getFacultad().getCiudad();
        }

        if (comision.getMaterias() != null) {
            this.materiasNombres = comision.getMaterias().stream()
                    .map(m -> m.getNombre())
                    .collect(Collectors.toList());
        }

        if (comision.getProfesores() != null) {
            this.profesores = comision.getProfesores().stream()
                    .map(p -> new ProfesorResumenDTO(p.getLegajo(), p.getNombre() + " " + p.getApellido()))
                    .collect(Collectors.toList());
        }
    }

    @Data
    @NoArgsConstructor
    public static class ProfesorResumenDTO {
        private String legajo;
        private String nombreCompleto;

        public ProfesorResumenDTO(String legajo, String nombreCompleto) {
            this.legajo = legajo;
            this.nombreCompleto = nombreCompleto;
        }
    }
    
    @Data
    @NoArgsConstructor
    public static class MateriaDetalleDTO {
        private String nombreMateria;
        private UUID idMateria;
        private List<ProfesorResumenDTO> profesores;
        
        public MateriaDetalleDTO(String nombre, UUID id, List<ProfesorResumenDTO> profes) {
            this.nombreMateria = nombre;
            this.idMateria = id;
            this.profesores = profes;
        }
    }
}