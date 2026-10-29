package com.concesonario.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfPropierties {

    private static Properties properties = new Properties();

    // Bloque de inicialización estática, lo que significa que
    // este código se ejecuta AL CARGAR la clase
    // una sola vez, cuando la JVM carga ConfPropierties



    static {
        try (InputStream input = ConfPropierties.class.getClassLoader().getResourceAsStream("config.properties"))
        {
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("No se pudo cargar el fichero de configuración", ex);
        }
    }

    public static String getOriginalDb() {
        return properties.getProperty("original_db");
    }

    public static String getPantillaDb() {
        return properties.getProperty("pantilla_db");
    }

    //Permite acceder dinámicamente a cualquier propiedad definida en [config.properties]
    public static String get(String key) {
        return properties.getProperty(key);
    }
}
