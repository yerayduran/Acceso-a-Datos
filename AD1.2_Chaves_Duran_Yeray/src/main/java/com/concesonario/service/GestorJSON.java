package com.concesonario.service;

import com.concesonario.exceptions.DBException;
import com.concesonario.model.Coche;
import com.concesonario.model.Concesionario;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GestorJSON {

    private final String rutaJSON;

    public GestorJSON() {
        this.rutaJSON = ConfPropierties.get("fichero_json");
    }

    public void exportarConcesionarioAJSON(Concesionario concesionario) throws DBException {
        exportarAJSON(concesionario, rutaJSON, "concesionario");
    }

    public void exportarCocheAJSON(Coche coche) throws DBException {
        exportarAJSON(coche, rutaJSON, "coche");
    }

    private void exportarAJSON(Object objeto, String ruta, String nombre) throws DBException {
        JsonbConfig config = new JsonbConfig().withFormatting(true);

        try (Jsonb jsonb = JsonbBuilder.create(config);
             FileWriter writer = new FileWriter(new File(ruta))) {

            jsonb.toJson(objeto, writer);

            System.out.println(nombre.substring(0, 1).toUpperCase() + nombre.substring(1) + " exportado correctamente a JSON.");

        } catch (IOException e) {
            throw new DBException("Error de E/S al exportar " + nombre + " a JSON: " + e.getMessage());

        } catch (Exception e) {
            throw new DBException("Error al serializar " + nombre + " a JSON: " + e.getMessage());
        }
    }
}