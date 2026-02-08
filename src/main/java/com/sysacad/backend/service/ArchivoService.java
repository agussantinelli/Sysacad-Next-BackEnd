package com.sysacad.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ArchivoService {

    
    private final Path rootLocation = Paths.get("uploads");

    public ArchivoService() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo inicializar la carpeta de almacenamiento", e);
        }
    }

    public String guardarFoto(MultipartFile archivo) {
        try {
            if (archivo.isEmpty()) {
                throw new RuntimeException("Error: Archivo vacío");
            }

            
            String extension = obtenerExtension(archivo.getOriginalFilename());
            String nombreArchivo = "foto_" + UUID.randomUUID().toString() + extension;

            
            Files.copy(archivo.getInputStream(), this.rootLocation.resolve(nombreArchivo));

            return nombreArchivo; 
        } catch (IOException e) {
            throw new RuntimeException("Falló al guardar la foto", e);
        }
    }

    public byte[] cargarFoto(String nombreArchivo) {
        try {
            Path file = rootLocation.resolve(nombreArchivo);
            return Files.readAllBytes(file);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el archivo: " + nombreArchivo);
        }
    }

    private String obtenerExtension(String filename) {
        return filename != null && filename.contains(".")
                ? filename.substring(filename.lastIndexOf("."))
                : "";
    }
}