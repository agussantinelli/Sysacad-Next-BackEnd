package com.sysacad.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final String DIRECTORIO_PERFILES = "uploads/perfiles/";


    public String guardarFotoPerfil(MultipartFile archivo, String identificador, String rutaFotoAnterior) {
        if (archivo.isEmpty()) {
            throw new RuntimeException("El archivo está vacío");
        }

        try {
            
            Path directorioPath = Paths.get(DIRECTORIO_PERFILES);
            if (!Files.exists(directorioPath)) {
                Files.createDirectories(directorioPath);
            }

            
            borrarArchivo(rutaFotoAnterior);

            
            String nombreOriginal = archivo.getOriginalFilename();
            String extension = ".jpg";
            if (nombreOriginal != null && nombreOriginal.lastIndexOf(".") > 0) {
                extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
            }

            String hash = UUID.randomUUID().toString().substring(0, 8);
            String nombreArchivo = identificador + "-" + hash + extension;
            Path rutaDestino = directorioPath.resolve(nombreArchivo);

            
            Files.copy(archivo.getInputStream(), rutaDestino, StandardCopyOption.REPLACE_EXISTING);

            return rutaDestino.toString();

        } catch (IOException e) {
            throw new RuntimeException("Error crítico al guardar la imagen: " + e.getMessage());
        }
    }

    public void borrarArchivo(String ruta) {
        if (ruta == null || ruta.isEmpty()) return;

        try {
            Path path = Paths.get(ruta);
            
            if (ruta.contains("seeded-") || ruta.contains("seeded/")) {
                System.out.println(">> FileStorageService: OMITIDO borrado de imagen protegida: " + ruta);
                return;
            }

            if (Files.exists(path) && path.toString().startsWith("uploads")) {
                Files.delete(path);
            }
        } catch (IOException e) {
            System.out.println("Advertencia: No se pudo borrar el archivo anterior (" + ruta + "): " + e.getMessage());
        }
    }

    public void deleteAllPerfiles() {
        try {
            Path root = Paths.get(DIRECTORIO_PERFILES);
            if (Files.exists(root)) {
                Files.walk(root)
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            System.out.println("No se pudo borrar imagen: " + path + " " + e.getMessage());
                        }
                    });
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo inicializar la carpeta de uploads para limpieza");
        }
    }
}