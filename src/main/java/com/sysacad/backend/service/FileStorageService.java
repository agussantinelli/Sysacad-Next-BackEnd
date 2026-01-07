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
            // 1. Asegurar que el directorio existe
            Path directorioPath = Paths.get(DIRECTORIO_PERFILES);
            if (!Files.exists(directorioPath)) {
                Files.createDirectories(directorioPath);
            }

            // 2. Limpieza: Borrar foto anterior si existe para no llenar el disco
            borrarArchivo(rutaFotoAnterior);

            // 3. Generar NOMBRE ÚNICO: {Identificador}-{Hash}.ext
            // Esto evita problemas de caché en navegadores
            String nombreOriginal = archivo.getOriginalFilename();
            String extension = ".jpg"; // Default
            if (nombreOriginal != null && nombreOriginal.lastIndexOf(".") > 0) {
                extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
            }

            String hash = UUID.randomUUID().toString().substring(0, 8);
            String nombreArchivo = identificador + "-" + hash + extension;
            Path rutaDestino = directorioPath.resolve(nombreArchivo);

            // 4. Guardar archivo (Reemplaza si existe casualmente el mismo nombre)
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
            if (Files.exists(path) && path.toString().startsWith("uploads")) {
                Files.delete(path);
            }
        } catch (IOException e) {
            System.out.println("Advertencia: No se pudo borrar el archivo anterior (" + ruta + "): " + e.getMessage());
        }
    }
}