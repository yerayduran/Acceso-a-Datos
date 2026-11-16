package com.concesionario.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase de utilidad para gestionar la conexión JDBC a MySQL o SQLite
 * leyendo los parámetros desde config.properties.
 */
public class DatabaseConnection {
    private static final Properties prop = new Properties();
    private static final String CONFIG_PATH = "src/main/resources/properties/config.properties";

    static {
        try (FileInputStream input = new FileInputStream(CONFIG_PATH)) {
            prop.load(input);
        } catch (IOException e) {
            System.err.println("Error al cargar el fichero de configuración (" + CONFIG_PATH + "): " + e.getMessage());
        }
    }

    /**
     * Lee una propiedad eliminando espacios y comillas dobles externas si existieran.
     */
    public static String getProperty(String key) {
        String value = prop.getProperty(key);
        if (value == null) {
            return null;
        }
        return value.trim().replaceAll("^\"|\"$", "");
    }

    public Connection getConnectionVacia() throws SQLException {
        return DriverManager.getConnection(
                getProperty("URL_VACIO"),
                getProperty("USER"),
                getProperty("PASSWORD")
        );
    }

    public Connection getConnectionMySQL() throws SQLException {
        return DriverManager.getConnection(
                getProperty("URL_SQL"),
                getProperty("USER"),
                getProperty("PASSWORD")
        );
    }

    public Connection getConnectionSQLite() throws SQLException {
        return DriverManager.getConnection(getProperty("URL_LITE"));
    }
}