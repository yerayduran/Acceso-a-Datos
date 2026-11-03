package com.concesionario.services;

import com.concesionario.exceptions.DBException;
import com.concesionario.model.Coche;
import com.concesionario.model.Concesionario;
import com.concesionario.utils.PropertiesReader;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class ImportacionDeJSONService {

    private final String rutaImportacionConcesionario;
    private final String rutaImportacionCoche;

    public ImportacionDeJSONService() {
        this.rutaImportacionConcesionario =
                PropertiesReader.get("fichero_importar_concesionario_json");

        this.rutaImportacionCoche =
                PropertiesReader.get("fichero_importar_coche_json");
    }

    /**
     * Importa un concesionario completo desde un fichero JSON
     * y lo guarda como nueva base de datos XML.
     */
    public void importarConcesionarioDesdeJSON(XMLService XMLService) throws DBException {
        ObjectMapper mapper = crearObjectMapper();

        try {
            File fichero = new File(rutaImportacionConcesionario);
            if (!fichero.exists()) {
                throw new DBException("El fichero JSON de importación no existe en la ruta: " + rutaImportacionConcesionario);
            }

            Concesionario concesionario = mapper.readValue(fichero, Concesionario.class);

            XMLService.guardarBaseDeDatos(concesionario);

            System.out.println("Concesionario importado desde JSON y guardado como nueva base de datos.");

        } catch (IOException e) {
            throw new DBException(
                    "Error al importar concesionario desde JSON: " + e.getMessage()
            );
        }
    }

    /**
     * Importa un coche desde un fichero JSON y lo inserta
     * en la base de datos XML actual.
     */
    public void importarCocheDesdeJSON(XMLService XMLService) throws DBException {
        ObjectMapper mapper = crearObjectMapper();

        try {
            File fichero = new File(rutaImportacionCoche);
            if (!fichero.exists()) {
                throw new DBException("El fichero JSON de coche no existe en la ruta: " + rutaImportacionCoche);
            }

            Coche coche = mapper.readValue(fichero, Coche.class);

            Concesionario concesionario = XMLService.leerBaseDeDatos();
            concesionario.agregarCoche(coche);
            XMLService.guardarBaseDeDatos(concesionario);

            System.out.println("Coche importado desde JSON.");

        } catch (IOException e) {
            throw new DBException(
                    "Error al importar coche desde JSON: " + e.getMessage()
            );
        }
    }

    /**
     * Crea un ObjectMapper configurado para ignorar propiedades
     * del JSON que no existan en las clases Java.
     */
    private ObjectMapper crearObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }
}