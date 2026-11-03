package com.concesionario.view;

import com.concesionario.exceptions.AppException;
import com.concesionario.exceptions.DBException;
import com.concesionario.exceptions.MiEntradaSalidaException;
import com.concesionario.model.Coche;
import com.concesionario.model.Concesionario;
import com.concesionario.services.*;
import com.concesionario.utils.MiEntradaSalida;
import com.concesionario.utils.PropertiesReader;

public class Menu {

    private final GestorXML gestorXML;
    private final GestorCSV gestorCSV;
    private final GestorJSON gestorJSON;
    private final GeneradorInforme generadorInforme;
    private final GestorImportJson gestorImportJson;

    public Menu() throws AppException {
        PropertiesReader.getInstance();

        gestorXML = new GestorXML();
        gestorCSV = new GestorCSV();
        gestorJSON = new GestorJSON();
        generadorInforme = new GeneradorInforme();
        gestorImportJson = new GestorImportJson();
    }

    public static void main(String[] args) {
        try {
            new Menu().iniciar();
        } catch (AppException e) {
            System.err.println("No se pudo iniciar la aplicación: " + e.getMessage());
        }
    }

    public void iniciar() {
        try {
            // Aseguramos que la BBDD XML exista al arrancar la app
            gestorXML.comprobarFicheroOriginalBb();
        } catch (DBException e) {
            System.err.println("Advertencia al comprobar la BBDD inicial: " + e.getMessage());
        }

        boolean continuar = true;

        while (continuar) {
            mostrarOpciones();

            int opcion;

            try {
                opcion = MiEntradaSalida.leerEnteroRango("Elige una opción: ", 0, 12);
            } catch (MiEntradaSalidaException e) {
                System.err.println("No se pudo leer la opción: " + e.getMessage());
                continue;
            }

            try {
                switch (opcion) {
                    case 1 -> mostrarCoches();
                    case 2 -> buscarCoche();
                    case 3 -> agregarCoche();
                    case 4 -> eliminarCoche();
                    case 5 -> ordenarCoches();
                    case 6 -> importarCSV(false);
                    case 7 -> importarCSV(true);
                    case 8 -> exportarConcesionarioJSON();
                    case 9 -> exportarCocheJSON();
                    case 10 -> importarConcesionarioJSON();
                    case 11 -> importarCocheJSON();
                    case 12 -> generarInforme();
                    case 0 -> {
                        continuar = false;
                        System.out.println("Hasta pronto.");
                    }
                    default -> System.out.println("Opción no válida.");
                }
            } catch (DBException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private void mostrarOpciones() {
        System.out.println("""
                
                ===== GESTIÓN DEL CONCESIONARIO =====
                1. Mostrar todos los coches
                2. Buscar coche por matrícula
                3. Añadir coche
                4. Eliminar coche
                5. Ordenar coches por matrícula
                6. Importar CSV (reemplazar concesionario)
                7. Importar CSV (añadir sin duplicados)
                8. Exportar concesionario a JSON
                9. Exportar un coche a JSON
                10. Importar concesionario desde JSON
                11. Importar un coche desde JSON
                12. Generar informe
                0. Salir
                ======================================
                """);
    }

    private Concesionario cargarConcesionario() throws DBException {
        Concesionario concesionario = gestorXML.leerBaseDeDatos();

        return concesionario == null ? new Concesionario() : concesionario;
    }

    private void mostrarCoches() throws DBException {
        Concesionario concesionario = cargarConcesionario();

        if (concesionario.getCoches().isEmpty()) {
            System.out.println("No hay coches registrados.");
            return;
        }

        for (Coche coche : concesionario.getCoches()) {
            System.out.println(coche);
        }
    }

    private void buscarCoche() throws DBException {
        String matricula = MiEntradaSalida.leerLinea("Matrícula: ").trim();

        Coche coche = cargarConcesionario().buscarPorMatricula(matricula);

        if (coche == null) {
            System.out.println("No se encontró ningún coche con esa matrícula.");
        } else {
            System.out.println(coche);
        }
    }

    private void agregarCoche() throws DBException {
        Concesionario concesionario = cargarConcesionario();

        String matricula = MiEntradaSalida.leerLinea("Matrícula: ").trim();

        if (concesionario.buscarPorMatricula(matricula) != null) {
            System.out.println("Ya existe un coche con esa matrícula.");
            return;
        }

        String marca = MiEntradaSalida.leerLinea("Marca: ").trim();
        String modelo = MiEntradaSalida.leerLinea("Modelo: ").trim();

        Coche coche = new Coche(matricula, marca, modelo);

        while (confirmar("¿Añadir un extra? (S/N): ")) {
            String extra = MiEntradaSalida.leerLinea("Extra: ").trim();

            if (!extra.isEmpty()) {
                coche.añadirExtra(extra);
            } else {
                System.out.println("El extra no puede estar vacío.");
            }
        }

        concesionario.agregarCoche(coche);
        gestorXML.guardarBaseDeDatos(concesionario);

        System.out.println("Coche añadido correctamente.");
    }

    private boolean confirmar(String mensaje) {
        while (true) {
            String respuesta = MiEntradaSalida.leerLinea(mensaje).trim();

            if (respuesta.equalsIgnoreCase("S")) {
                return true;
            }

            if (respuesta.equalsIgnoreCase("N")) {
                return false;
            }

            System.out.println("Respuesta no válida. Introduce S o N.");
        }
    }

    private void eliminarCoche() throws DBException {
        Concesionario concesionario = cargarConcesionario();

        String matricula = MiEntradaSalida.leerLinea(
                "Matrícula del coche que quieres eliminar: "
        ).trim();

        if (concesionario.eliminarPorMatricula(matricula)) {
            gestorXML.guardarBaseDeDatos(concesionario);
            System.out.println("Coche eliminado correctamente.");
        } else {
            System.out.println("No se encontró ningún coche con esa matrícula.");
        }
    }

    private void ordenarCoches() throws DBException {
        Concesionario concesionario = cargarConcesionario();

        concesionario.ordenarPorMatricula();
        gestorXML.guardarBaseDeDatos(concesionario);

        System.out.println("Coches ordenados por matrícula.");
    }

    private void importarCSV(boolean sinDuplicados) throws DBException {
        Concesionario concesionario;

        if (sinDuplicados) {
            concesionario = cargarConcesionario();
            gestorCSV.importarSinDuplicados(concesionario);
        } else {
            concesionario = gestorCSV.leerCSV();
        }

        gestorXML.guardarBaseDeDatos(concesionario);

        System.out.println("Importación CSV completada.");
    }

    private void exportarConcesionarioJSON() throws DBException {
        gestorJSON.exportarConcesionarioAJSON(cargarConcesionario());
    }

    private void exportarCocheJSON() throws DBException {
        String matricula = MiEntradaSalida.leerLinea(
                "Matrícula del coche que quieres exportar: "
        ).trim();

        Coche coche = cargarConcesionario().buscarPorMatricula(matricula);

        if (coche == null) {
            System.out.println("No se encontró ningún coche con esa matrícula.");
            return;
        }

        gestorJSON.exportarCocheAJSON(coche);
    }

    private void importarConcesionarioJSON() throws DBException {
        gestorImportJson.importarConcesionarioDesdeJSON(gestorXML);
    }

    private void importarCocheJSON() throws DBException {
        gestorImportJson.importarCocheDesdeJSON(gestorXML);
    }

    private void generarInforme() throws DBException {
        generadorInforme.generarInforme(cargarConcesionario());
    }
}