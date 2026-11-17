package com.concesionario.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase de utilidad para gestionar la conexión JDBC a MySQL o SQLite
 * cargando los drivers y parámetros desde config.properties.
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
     *
     * @param key Clave de la propiedad.
     * @return Valor limpio de la propiedad.
     */
    public static String getProperty(String key) {
        String value = prop.getProperty(key);
        if (value == null) {
            return null;
        }
        return value.trim().replaceAll("^\"|\"$", "");
    }

    /**
     * Registra el driver JDBC correspondiente en el DriverManager.
     *
     * @param driverKey Clave del driver en config.properties ("DR_SQL" o "DR_LITE").
     * @throws SQLException Si no se encuentra la clase del driver especificado.
     */
    private void cargarDriver(String driverKey) throws SQLException {
        String driverClass = getProperty(driverKey);
        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se pudo cargar el driver JDBC (" + driverClass + "): " + e.getMessage(), e);
        }
    }

    /**
     * Devuelve una conexión al servidor MySQL sin base de datos seleccionada (para crear el esquema).
     *
     * @return Objeto Connection.
     * @throws SQLException Si falla la carga del driver o la conexión.
     */
    public Connection getConnectionVacia() throws SQLException {
        cargarDriver("DR_SQL");
        return DriverManager.getConnection(
                getProperty("URL_VACIO"),
                getProperty("USER"),
                getProperty("PASSWORD")
        );
    }

    /**
     * Devuelve la conexión con la base de datos MySQL del concesionario.
     *
     * @return Objeto Connection.
     * @throws SQLException Si falla la carga del driver o la conexión.
     */
    public Connection getConnectionMySQL() throws SQLException {
        cargarDriver("DR_SQL");
        return DriverManager.getConnection(
                getProperty("URL_SQL"),
                getProperty("USER"),
                getProperty("PASSWORD")
        );
    }

    /**
     * Devuelve la conexión con la base de datos embebida SQLite.
     *
     * @return Objeto Connection.
     * @throws SQLException Si falla la carga del driver o la conexión.
     */
    public Connection getConnectionSQLite() throws SQLException {
        cargarDriver("DR_LITE");
        return DriverManager.getConnection(getProperty("URL_LITE"));
    }
}