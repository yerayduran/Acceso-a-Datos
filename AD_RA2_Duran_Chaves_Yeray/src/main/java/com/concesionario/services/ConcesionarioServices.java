package com.concesionario.services;

import com.concesionario.model.Coche;
import com.concesionario.util.DatabaseConnection;
import com.concesionario.util.SQLSenteces;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio que gestiona todas las operaciones CRUD, transacciones,
 * importación CSV y procedimientos almacenados del concesionario.
 */
public class ConcesionarioServices {

    private final DatabaseConnection dbConnection = new DatabaseConnection();
    private String gestorActivo = "mysql";

    public String getGestorActivo() {
        return gestorActivo;
    }

    /**
     * Obtiene una conexión JDBC según el gestor seleccionado ("mysql" o "sqlite").
     *
     * @return Conexión activa.
     * @throws SQLException Si falla la conexión.
     */
    public Connection obtenerConexion() throws SQLException {
        if ("sqlite".equalsIgnoreCase(gestorActivo)) {
            return dbConnection.getConnectionSQLite();
        }
        return dbConnection.getConnectionMySQL();
    }

    /**
     * Opción 1: Selecciona el gestor (MySQL o SQLite) y prueba la conexión.
     *
     * @param tipoGestor "mysql" o "sqlite".
     * @return Nombre del producto de base de datos conectado.
     * @throws SQLException Si no es posible establecer conexión.
     */
    public String conectarYProbar(String tipoGestor) throws SQLException {
        this.gestorActivo = tipoGestor.trim().toLowerCase();

        if ("mysql".equals(this.gestorActivo)) {
            try (Connection connVacia = dbConnection.getConnectionVacia();
                 Statement st = connVacia.createStatement()) {
                st.executeUpdate(SQLSenteces.SQL_CREATE_SCHEME_MYSQL);
            }
        }

        try (Connection conn = obtenerConexion()) {
            return conn.getMetaData().getDatabaseProductName();
        }
    }

    /**
     * Opción 2: Crea las tablas propietarios, coches y traspasos (y el procedimiento en MySQL).
     *
     * @throws SQLException Si ocurre un error ejecutando el DDL.
     */
    public void inicializarTablas() throws SQLException {
        boolean esSqlite = "sqlite".equalsIgnoreCase(gestorActivo);

        if (!esSqlite) {
            try (Connection connVacia = dbConnection.getConnectionVacia();
                 Statement st = connVacia.createStatement()) {
                st.executeUpdate(SQLSenteces.SQL_CREATE_SCHEME_MYSQL);
            }
        }

        try (Connection conn = obtenerConexion();
             Statement stmt = conn.createStatement()) {
            if (esSqlite) {
                stmt.executeUpdate(SQLSenteces.SQL_CREATE_TABLE_PROPIETARIOS_MYSQLITE);
                stmt.executeUpdate(SQLSenteces.SQL_CREATE_TABLE_COCHES_MYSQLITE);
                stmt.executeUpdate(SQLSenteces.SQL_CREATE_TABLE_TRASPASOS_MYSQLITE);
            } else {
                stmt.executeUpdate(SQLSenteces.SQL_CREATE_TABLE_PROPIETARIOS_MYSQL);
                stmt.executeUpdate(SQLSenteces.SQL_CREATE_TABLE_COCHES_MYSQL);
                stmt.executeUpdate(SQLSenteces.SQL_CREATE_TABLE_TRASPASOS_MYSQL);
                stmt.execute(SQLSenteces.SQL_BORRAR_PROCEDIMIENTO_ALMACENADO);
                stmt.execute(SQLSenteces.SQL_PROCEDIMIENTO_ALMACENADO_CREACION);
            }
        }
    }

    /**
     * Opción 3: Registra un nuevo propietario en la base de datos.
     */
    public void registrarPropietario(String dni, String nombre, String apellidos, String telefono) throws SQLException {
        try (Connection conn = obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQLSenteces.SQL_INSERTAR_PROPIETARIO)) {
            ps.setString(1, dni.toUpperCase());
            ps.setString(2, nombre);
            ps.setString(3, apellidos);
            ps.setString(4, telefono);
            ps.executeUpdate();
        }
    }

    /**
     * Opción 4: Inserta un coche en el stock del concesionario (sin propietario).
     */
    public void insertarCoche(Coche coche) throws SQLException {
        try (Connection conn = obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQLSenteces.SQL_INSERT_COCHE_SQL)) {
            ps.setString(1, coche.getMatricula().toUpperCase());
            ps.setString(2, coche.getMarca());
            ps.setString(3, coche.getModelo());
            ps.setString(4, String.join(", ", coche.getExtras()));
            ps.setDouble(5, coche.getPrecio());
            ps.executeUpdate();
        }
    }

    /**
     * Opción 5: Importa coches desde CSV_PATH de forma transaccional (todo o nada).
     *
     * @return Número de coches insertados.
     */
    public int importarCochesCSV() throws SQLException, IOException {
        String rutaCsv = DatabaseConnection.getProperty("CSV_PATH");
        int contador = 0;

        try (Connection conn = obtenerConexion();
             BufferedReader br = new BufferedReader(new FileReader(rutaCsv));
             PreparedStatement ps = conn.prepareStatement(SQLSenteces.SQL_INSERT_COCHE_SQL)) {

            conn.setAutoCommit(false);
            try {
                String linea;
                int numLinea = 0;
                while ((linea = br.readLine()) != null) {
                    numLinea++;
                    linea = linea.trim();
                    if (linea.isEmpty()) continue;
                    if (numLinea == 1 && linea.toLowerCase().startsWith("matricula")) continue;

                    String separador = linea.contains(";") ? ";" : ",";
                    String[] campos = linea.split(separador);
                    if (campos.length < 5) {
                        throw new IOException("Formato inválido en línea " + numLinea + ": " + linea);
                    }

                    ps.setString(1, campos[0].trim().toUpperCase());
                    ps.setString(2, campos[1].trim());
                    ps.setString(3, campos[2].trim());
                    ps.setString(4, campos[3].trim());
                    ps.setDouble(5, Double.parseDouble(campos[4].trim()));
                    ps.executeUpdate();
                    contador++;
                }
                conn.commit();
                return contador;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /**
     * Opción 6: Devuelve la lista de coches que pertenecen al concesionario (id_propietario IS NULL).
     */
    public List<Coche> listarCochesConcesionario() throws SQLException {
        List<Coche> coches = new ArrayList<>();
        try (Connection conn = obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQLSenteces.SQL_COCHES_SIN_PROPIETARIO);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                coches.add(mapearCoche(rs));
            }
        }
        return coches;
    }

    /**
     * Opción 7: Devuelve los coches con propietario asignado junto a su DNI usando JOIN.
     */
    public List<String> listarCochesPropietarios() throws SQLException {
        List<String> resultados = new ArrayList<>();
        try (Connection conn = obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQLSenteces.SQL_COCHES_CON_PROPIETARIO_JOIN);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultados.add(String.format("Matrícula: %-10s | Marca: %-10s | Modelo: %-10s | Precio: %8.2f € | DNI Propietario: %s",
                        rs.getString("matricula"),
                        rs.getString("marca"),
                        rs.getString("modelo"),
                        rs.getDouble("precio"),
                        rs.getString("dni")));
            }
        }
        return resultados;
    }

    /**
     * Opción 8: Actualiza los datos de un coche a partir de su matrícula.
     */
    public boolean modificarCoche(Coche coche) throws SQLException {
        try (Connection conn = obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQLSenteces.SQL_ACTUALIZAR_COCHE)) {
            ps.setString(1, coche.getMarca());
            ps.setString(2, coche.getModelo());
            ps.setString(3, String.join(", ", coche.getExtras()));
            ps.setDouble(4, coche.getPrecio());
            ps.setString(5, coche.getMatricula().toUpperCase());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Opción 9: Elimina un coche por su matrícula.
     */
    public boolean borrarCoche(String matricula) throws SQLException {
        try (Connection conn = obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(SQLSenteces.SQL_DELETE_COCHE)) {
            ps.setString(1, matricula.toUpperCase());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Opción 10: Realiza un traspaso de forma transaccional verificando comprador y coche.
     */
    public void realizarTraspaso(String dniComprador, String matricula, double monto) throws SQLException {
        try (Connection conn = obtenerConexion()) {
            conn.setAutoCommit(false);
            try {
                // 1. Verificar que el comprador existe
                int idComprador;
                try (PreparedStatement psComp = conn.prepareStatement(SQLSenteces.SQL_OBTENER_PROPIETARIO)) {
                    psComp.setString(1, dniComprador.toUpperCase());
                    try (ResultSet rs = psComp.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("El comprador con DNI " + dniComprador + " no existe.");
                        }
                        idComprador = rs.getInt("id_propietario");
                    }
                }

                // 2. Verificar que el coche existe y obtener su propietario actual (vendedor)
                Integer idVendedor;
                try (PreparedStatement psCoche = conn.prepareStatement(SQLSenteces.SQL_OBTENER_COCHE)) {
                    psCoche.setString(1, matricula.toUpperCase());
                    try (ResultSet rs = psCoche.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("El coche con matrícula " + matricula + " no existe.");
                        }
                        int idProp = rs.getInt("id_propietario");
                        idVendedor = rs.wasNull() ? null : idProp;
                    }
                }

                if (idVendedor != null && idVendedor == idComprador) {
                    throw new SQLException("El comprador ya es el propietario actual de este vehículo.");
                }

                // 3. Insertar registro en traspasos
                try (PreparedStatement psTraspaso = conn.prepareStatement(SQLSenteces.SQL_INSERTAR_TRASPASO)) {
                    psTraspaso.setString(1, matricula.toUpperCase());
                    if (idVendedor == null) {
                        psTraspaso.setNull(2, Types.INTEGER);
                    } else {
                        psTraspaso.setInt(2, idVendedor);
                    }
                    psTraspaso.setInt(3, idComprador);
                    psTraspaso.setDouble(4, monto);
                    psTraspaso.executeUpdate();
                }

                // 4. Actualizar id_propietario en coches
                try (PreparedStatement psUpdate = conn.prepareStatement(SQLSenteces.SQL_ACTUALIZAR_ID_EN_COCHE)) {
                    psUpdate.setInt(1, idComprador);
                    psUpdate.setString(2, matricula.toUpperCase());
                    psUpdate.executeUpdate();
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /**
     * Opción 11: Ejecuta el procedimiento almacenado sp_coches_por_marca (solo MySQL).
     */
    public List<Coche> ejecutarProcedimientoPorMarca(String marca) throws SQLException {
        List<Coche> coches = new ArrayList<>();
        try (Connection conn = dbConnection.getConnectionMySQL();
             CallableStatement cs = conn.prepareCall(SQLSenteces.SQL_LLAMADA_PROCEDIMIENTO_ALMACENADO)) {
            cs.setString(1, marca);
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    coches.add(mapearCoche(rs));
                }
            }
        }
        return coches;
    }

    /**
     * Método auxiliar para construir un objeto Coche desde un ResultSet.
     * Soporta separación por comas ',' y por barras verticales '|' (CSV).
     */
    private Coche mapearCoche(ResultSet rs) throws SQLException {
        String extrasRaw = rs.getString("extras");
        List<String> extras = new ArrayList<>();
        if (extrasRaw != null && !extrasRaw.isBlank()) {
            // Separa tanto por comas ',' como por barras verticales '|'
            for (String item : extrasRaw.split("[,|]")) {
                String extraLimpio = item.trim();
                if (!extraLimpio.isEmpty() && !extraLimpio.equals("-")) {
                    extras.add(extraLimpio);
                }
            }
        }
        int idProp = rs.getInt("id_propietario");
        Integer idPropietario = rs.wasNull() ? null : idProp;

        return new Coche(
                rs.getString("matricula"),
                rs.getString("marca"),
                rs.getString("modelo"),
                extras,
                rs.getDouble("precio"),
                idPropietario
        );
    }
}