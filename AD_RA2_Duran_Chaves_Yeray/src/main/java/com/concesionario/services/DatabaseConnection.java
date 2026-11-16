package com.concesionario.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase de utilidad para gestionar la conexión JDBC a MySQL o SQLite
 * leyendo todos los parámetros (drivers, URLs, credenciales y rutas) desde config.properties.
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
     * Obtiene una conexión activa según el gestor seleccionado ("mysql" o "sqlite").
     *
     * @param tipoBD Tipo de gestor ("mysql" o "sqlite").
     * @return Objeto Connection de JDBC.
     * @throws SQLException Si hay un fallo al conectar con la base de datos.
     * @throws ClassNotFoundException Si no se encuentra la clase del driver configurado.
     */
    public static Connection getConnection(String tipoBD) throws SQLException, ClassNotFoundException {
        String dbType = (tipoBD != null) ? tipoBD.trim().toLowerCase() : "mysql";

        if (dbType.equals("sqlite")) {
            String driverLite = getCleanProperty("DR_LITE");
            String urlLite = getCleanProperty("URL_LITE");

            Class.forName(driverLite);
            return DriverManager.getConnection(urlLite);
        } else {
            String driverSql = getCleanProperty("DR_SQL");
            String urlSql = getCleanProperty("URL_SQL");
            String user = getCleanProperty("USER");
            String password = getCleanProperty("PASSWORD");

            Class.forName(driverSql);
            return DriverManager.getConnection(urlSql, user, password);
        }
    }

    /**
     * Lee una propiedad del fichero y elimina espacios o comillas dobles externas si existen.
     *
     * @param key Nombre de la propiedad.
     * @return Valor limpio de la propiedad.
     */
    public static String getCleanProperty(String key) {
        String value = prop.getProperty(key);
        if (value == null) {
            return null;
        }
        return value.trim().replaceAll("^\"|\"$", "");
    }
}