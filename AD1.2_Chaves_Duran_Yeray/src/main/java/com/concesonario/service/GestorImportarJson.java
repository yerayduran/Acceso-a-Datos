package com.concesonario.service;

import com.concesonario.exceptions.DBException;
import com.concesonario.model.Coche;
import com.concesonario.model.Concesionario;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class GestorImportarJson {

    private final String rutaJSON;

    public GestorImportarJson() {
        this.rutaJSON = ConfPropierties.get("fichero_json");
    }

    /**
     * Importa un concesionario completo desde un fichero JSON
     * y lo guarda como nueva base de datos XML.
     */
    public void importarConcesionarioDesdeJSON(GestorXML gestorXML) throws DBException {
        File fichero = new File(rutaJSON);

        if (!fichero.exists() || fichero.length() == 0) {
            throw new DBException("El fichero JSON no existe o está vacío: " + rutaJSON);
        }

        JsonbConfig config = new JsonbConfig()
                .withFormatting(true);

        try (Jsonb jsonb = JsonbBuilder.create(config);
             InputStream input = new FileInputStream(fichero)) {

            Concesionario concesionario = jsonb.fromJson(input, Concesionario.class);

            if (concesionario == null) {
                throw new DBException("El fichero JSON no contiene un concesionario válido.");
            }

            gestorXML.guardarBaseDeDatos(concesionario);

            System.out.println("Concesionario importado desde JSON y guardado como nueva base de datos.");

        } catch (IOException e) {
            throw new DBException("Error de E/S al importar concesionario desde JSON: " + e.getMessage());
        } catch (Exception e) {
            throw new DBException("Error al deserializar el concesionario desde JSON: " + e.getMessage());
        }
    }

    /**
     * Importa un coche desde un fichero JSON y lo inserta
     * en la base de datos XML actual.
     */
    public void importarCocheDesdeJSON(GestorXML gestorXML) throws DBException {
        File fichero = new File(rutaJSON);

        if (!fichero.exists() || fichero.length() == 0) {
            throw new DBException("El fichero JSON no existe o está vacío: " + rutaJSON);
        }

        JsonbConfig config = new JsonbConfig().withFormatting(true);

        try (Jsonb jsonb = JsonbBuilder.create(config);

             InputStream input = new FileInputStream(fichero)) {

            Coche coche = jsonb.fromJson(input, Coche.class);

            if (coche == null) {
                throw new DBException("El fichero JSON no contiene un coche válido.");
            }

            Concesionario concesionario = gestorXML.leerBaseDeDatos();

            if (concesionario == null) {
                concesionario = new Concesionario();
            }

            concesionario.agregarCoche(coche);
            gestorXML.guardarBaseDeDatos(concesionario);

            System.out.println("Coche importado desde JSON e insertado en la base de datos XML.");

        } catch (IOException e) {
            throw new DBException("Error de E/S al importar coche desde JSON: " + e.getMessage());
        } catch (Exception e) {
            throw new DBException("Error al deserializar el coche desde JSON: " + e.getMessage());
        }
    }
}