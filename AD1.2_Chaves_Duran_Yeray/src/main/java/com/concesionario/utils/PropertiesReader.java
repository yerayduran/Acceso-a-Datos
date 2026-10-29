package com.concesionario.utils;

import com.concesionario.exceptions.AppException;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

public class PropertiesReader {
    private static final List<String> CONFIG_CANDIDATES = List.of(
            "src/main/resources/properties/config.properties",
            "src/main/resources/config.properties",
            "config.properties"
    );

    private final Properties p;
    private static PropertiesReader instance = null;

    private PropertiesReader() throws AppException {
        p = new Properties();
        Path configFile = resolveConfigPath();

        try (BufferedReader lector = Files.newBufferedReader(configFile, StandardCharsets.UTF_8)) {
            p.load(lector);
        } catch (IOException e) {
            throw new AppException("❌ ERROR: No se ha podido leer el archivo config.properties en la ruta: " + configFile.toAbsolutePath());
        }
    }

    private static Path resolveConfigPath() throws AppException {
        for (String candidate : CONFIG_CANDIDATES) {
            Path path = Path.of(candidate);
            if (Files.exists(path)) {
                return path;
            }
        }
        throw new AppException("❌ ERROR: El archivo config.properties no se encuentra en ninguna de las rutas esperadas: " + CONFIG_CANDIDATES);
    }

    public static PropertiesReader getInstance() throws AppException {
        if (instance == null) {
            instance = new PropertiesReader();
        }
        return instance;
    }

    public String get(String propiedad) {
        return p.getProperty(propiedad);
    }
}