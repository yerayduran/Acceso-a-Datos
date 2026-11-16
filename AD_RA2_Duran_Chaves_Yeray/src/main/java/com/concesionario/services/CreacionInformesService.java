package com.concesionario.services;

import com.concesionario.util.DatabaseConnection;
import com.concesionario.util.SQLSenteces;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Servicio encargado de generar el fichero informe_concesionario.txt.
 */
public class CreacionInformesService {

    /**
     * Genera el informe resumen con el total de coches (COUNT(*)),
     * listado agrupado por marca y el equipamiento extra más repetido.
     *
     * @param concesionarioServices Servicio activo para obtener la conexión al gestor seleccionado.
     * @return Ruta del archivo generado.
     * @throws SQLException Si ocurre un error en las consultas SQL.
     * @throws IOException  Si ocurre un error al escribir el archivo.
     */
    public String generarInformeResumen(ConcesionarioServices concesionarioServices) throws SQLException, IOException {
        String rutaInforme = DatabaseConnection.getProperty("INFORME_PATH");
        int totalCoches = 0;
        Map<String, Integer> cochesPorMarca = new LinkedHashMap<>();
        Map<String, Integer> frecuenciaExtras = new HashMap<>();

        try (Connection conn = concesionarioServices.obtenerConexion()) {
            // 1. Número total de coches (COUNT(*))
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(SQLSenteces.SQL_NUM_COCHES)) {
                if (rs.next()) {
                    totalCoches = rs.getInt(1);
                }
            }

            // 2. Listado agrupado por marca y conteo de extras
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(SQLSenteces.SQL_OBTENER_COCHES_TODOS)) {
                while (rs.next()) {
                    String marca = rs.getString("marca");
                    cochesPorMarca.put(marca, cochesPorMarca.getOrDefault(marca, 0) + 1);

                    String extrasRaw = rs.getString("extras");
                    if (extrasRaw != null && !extrasRaw.isBlank()) {
                        for (String extra : extrasRaw.split(",")) {
                            String limpio = extra.trim().toLowerCase();
                            if (!limpio.isEmpty()) {
                                frecuenciaExtras.put(limpio, frecuenciaExtras.getOrDefault(limpio, 0) + 1);
                            }
                        }
                    }
                }
            }
        }

        String extraMasRepetido = "Ninguno";
        int maxRepeticiones = 0;
        for (Map.Entry<String, Integer> entry : frecuenciaExtras.entrySet()) {
            if (entry.getValue() > maxRepeticiones) {
                maxRepeticiones = entry.getValue();
                extraMasRepetido = entry.getKey() + " (" + maxRepeticiones + " veces)";
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaInforme))) {
            writer.write("====================================================\n");
            writer.write("         INFORME RESUMEN DEL CONCESIONARIO          \n");
            writer.write("====================================================\n");
            writer.write("Fecha: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "\n");
            writer.write("Gestor BBDD: " + concesionarioServices.getGestorActivo().toUpperCase() + "\n\n");

            writer.write("1. NÚMERO TOTAL DE COCHES: " + totalCoches + "\n\n");

            writer.write("2. COCHES AGRUPADOS POR MARCA:\n");
            if (cochesPorMarca.isEmpty()) {
                writer.write("   - No hay coches registrados.\n");
            } else {
                for (Map.Entry<String, Integer> entry : cochesPorMarca.entrySet()) {
                    writer.write(String.format("   - %-15s : %d coche(s)\n", entry.getKey(), entry.getValue()));
                }
            }
            writer.write("\n");

            writer.write("3. EXTRA O EQUIPAMIENTO MÁS REPETIDO:\n");
            writer.write("   - " + extraMasRepetido + "\n");
            writer.write("====================================================\n");
        }

        return rutaInforme;
    }
}