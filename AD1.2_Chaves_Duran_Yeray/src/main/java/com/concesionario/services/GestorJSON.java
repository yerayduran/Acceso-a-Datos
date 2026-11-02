package com.concesionario.services;

import com.concesionario.exceptions.DBException;
import com.concesionario.utils.PropertiesReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.concesionario.model.Concesionario;
import com.concesionario.model.Coche;

import java.io.File;
import java.io.IOException;

/**
 * Clase que gestiona la exportación de datos del concesionario a formato JSON
 * Usa la librería Jackson para convertir objetos Java en archivos JSON
 */
public class GestorJSON {

    private final String rutaJSON;

    public GestorJSON() {
        this.rutaJSON = PropertiesReader.get("fichero_json");
    }

    /**
     * Exporta todo el concesionario a un fichero JSON
     */
    public void exportarConcesionarioAJSON(Concesionario concesionario) throws DBException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            mapper.writeValue(new File(rutaJSON), concesionario);
            System.out.println("Concesionario exportado correctamente a JSON.");
        } catch (IOException e) {
            throw new DBException("Error al exportar concesionario a JSON: " + e.getMessage());
        }
    }

    /**
     * Exporta un coche específico a un fichero JSON
     */
    public void exportarCocheAJSON(Coche coche) throws DBException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            mapper.writeValue(new File(rutaJSON), coche);
            System.out.println("Coche exportado correctamente a JSON.");
        } catch (IOException e) {
            throw new DBException("Error al exportar coche a JSON: " + e.getMessage());
        }
    }
}
