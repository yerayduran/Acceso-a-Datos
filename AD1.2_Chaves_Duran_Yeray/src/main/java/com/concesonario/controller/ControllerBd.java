package com.concesonario.controller;

import com.concesonario.model.Coche;
import com.concesonario.model.Concesionario;
import com.concesonario.service.*;
import com.concesonario.exceptions.DBException;
import com.concesonario.exceptions.SolicitarDatos;


/**
 * @ControllerBd es la clase que gestiona la lógica principal de mi programa.
 * Se comunica con las clases de modelo (Coche, Concesionario)
 * y las operaciones de persistencia en los gestores -> (XML, CSV, JSON, Informes, Properties).
 * Se realizan las operaciones CRUD
 *
 */

public class ControllerBd {

    private final GestorXML gestorXML;
    private final GestorCSV gestorCSV;
    private Concesionario concesionario;
    private final GestorJSON gestorJSON;
    private final GestorImportarJson gestorImportarJson;
    private final GeneradorInforme generadorInforme;


    public ControllerBd(GestorXML gestorXML, GestorCSV gestorCSV, GestorJSON gestorJSON, GestorImportarJson gestorImportarJson, GeneradorInforme generadorInforme) {
        this.gestorXML = gestorXML;
        this.gestorCSV = gestorCSV;
        this.gestorJSON = gestorJSON;
        this.gestorImportarJson = gestorImportarJson;
        this.generadorInforme = generadorInforme;
    }

    /**
     * Carga el concesionario desde la base de datos XML.
     * Si no existe o hay error, devuelve un concesionario vacío
     *@return Concesionario cargado o nuevo si falla la lectura
     */
    private Concesionario cargarConcesionario() {
        try {
            Concesionario concesionarioEnMemoria = gestorXML.leerBaseDeDatos();
            if (concesionarioEnMemoria == null) {
                return new Concesionario();
            }
            return concesionarioEnMemoria;
        } catch (DBException e) {
            System.err.println("Error cargando base de datos: " + e.getMessage());
            return new Concesionario(); // devuelve una instancia vacía
        }
    }


    /**
     * Importa coches desde un archivo CSV evitando duplicados y
     * guarda los cambios en la base de datos.
     */
    public void importarDesdeCSV() {
        try {
            gestorCSV.importarSinDuplicados(concesionario);
            guardarCambios(); //Llama al metodo para persistir en el fichero [concesionario_BBDD.xml]
        } catch (DBException e) {
            System.out.println("Error al importar CSV: " + e.getMessage());
        }
    }

    public Concesionario getConcesionario() {
        return concesionario;
    }


    /**
     * Llama ->> @gestorXML.guardarBaseDeDatos(concesionario)
     * para escribir el contenido del objeto concesionario en el fichero XML
     * (actualiza la base de datos).
     */
    public void guardarCambios() {
        try {
            gestorXML.guardarBaseDeDatos(concesionario);
        } catch (DBException e) {
            System.out.println("Error al guardar la base de datos: " + e.getMessage());
        }
    }





    /**
     * Inserta un nuevo coche solicitando los datos por consola
     * Valida que la matrícula no exista antes de agregarlo
     */
    public void insertaCocheDesdeConsola() {

        Coche cocheNuevo = SolicitarDatos.solicitarDatos();

        if (concesionario.buscarPorMatricula(cocheNuevo.getMatricula()) != null) {
            System.out.println("Ya existe un coche con esa matrícula");
            return;
        }

        SolicitarDatos.solicitarEquipamiento(cocheNuevo);
        concesionario.agregarCoche(cocheNuevo);
        guardarCambios(); // PERSISTIMOS EN LA BASE DE DATOs
        System.out.println("Coche insertado correctamente");
    }

    // hace mencion al ordenar

    public void ordenarPorMatricula() {
        concesionario.ordenarPorMatricula();
        guardarCambios();
        System.out.println("Coches ordenados por matrícula correctamente ");
    }

    // hace mencion al eliminar


    public void eliminarCochePorMatricula(String matricula) {

        boolean eliminado = concesionario.eliminarPorMatricula(matricula);
        if (eliminado) {
            guardarCambios();
            System.out.println("Coche eliminado correctamente");
        } else {
            System.out.println("No se encontró ningún coche con esa matrícula");
        }
    }

    // hace mencion al buscar

    public Coche buscarCochePorMatricula(String matricula) {
        return concesionario.buscarPorMatricula(matricula);
    }


    // PARTE ->> EXPORTAR - capa intermedia trabajamos con ->> @GestorJSON


    public void exportarConcesionarioAJSON() {
        try {
            gestorJSON.exportarConcesionarioAJSON(concesionario);
        } catch (DBException e) {
            System.out.println("Error al exportar concesionario: " + e.getMessage());
        }
    }

    public void exportarCochePorMatricula(String matricula) {
        Coche coche = buscarCochePorMatricula(matricula);
        if (coche == null) {
            System.out.println("No se encontró ningún coche con esa matrícula.");
            return;
        }

        try {
            gestorJSON.exportarCocheAJSON(coche);
        } catch (DBException e) {
            System.out.println("Error al exportar coche: " + e.getMessage());
        }
    }



    // PARTE ->> IMPORTAR - capa intermedia trabajamos con ->> @GestorImportJson



    public void importarConcesionarioDesdeJSON() {
        try {
            gestorImportarJson.importarConcesionarioDesdeJSON(gestorXML);
        } catch (DBException e) {
            System.out.println("Error al importar concesionario: " + e.getMessage());
        }
    }

    public void importarCocheDesdeJSON() {
        try {
            gestorImportarJson.importarCocheDesdeJSON(gestorXML);
        } catch (DBException e) {
            System.out.println("Error al importar coche: " + e.getMessage());
        }
    }

    // PARTE ->> INFORMES ->> capa intermedia trabajamos con ->> @GeneradorInforme


    public void generarInformeResumen() {
        generadorInforme.generarInforme(concesionario);
    }

}
