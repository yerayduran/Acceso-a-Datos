package com.concesionario.view;

import com.concesionario.exceptions.AppException;
import com.concesionario.exceptions.DBException;
import com.concesionario.model.Coche;
import com.concesionario.model.ConcesionarioService;
import com.concesionario.utils.MiEntradaSalida;

import java.io.IOException;
import java.nio.file.Path;

public class Menu {
    public static void main(String[] args) {
        ConcesionarioService service;
        try {
            service = new ConcesionarioService();
        } catch (AppException e) {
            System.out.println(e.getMessage());
            return;
        }

        boolean ejecutando = true;
        while (ejecutando) {
            mostrarMenu();
            int opcion = MiEntradaSalida.leerEntero("Elige una opción:");

            try {
                switch (opcion) {
                    case 1 -> insertarCoche(service);
                    case 2 -> importarCsv(service);
                    case 3 -> {
                        service.ordenarPorMatricula();
                        System.out.println("Coches ordenados por matrícula.");
                    }
                    case 4 -> borrarCoche(service);
                    case 5 -> modificarCoche(service);
                    case 6 -> service.exportarConcesionarioJson(Path.of("./datos/concesionario_BBDD.json"));
                    case 7 -> exportarCocheJson(service);
                    case 8 -> importarConcesionarioJson(service);
                    case 9 -> importarCocheJson(service);
                    case 10 -> {
                        service.generarInformeResumen();
                        System.out.println("Informe resumen creado correctamente.");
                    }
                    case 0 -> {
                        System.out.println("Adios joven amo...");
                        ejecutando = false;
                    }
                    default -> System.out.println("Elije otra opcion.");
                }
            } catch (IOException | DBException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void insertarCoche(ConcesionarioService service) throws DBException {
        String matricula = MiEntradaSalida.leerTexto("Matrícula: ");
        String marca = MiEntradaSalida.leerTexto("Marca: ");
        String modelo = MiEntradaSalida.leerTexto("Modelo: ");
        Coche coche = new Coche(matricula, marca, modelo);

        int cantidadExtras = MiEntradaSalida.leerEnteroPositivo("Número de extras: ", true);
        for (int i = 0; i < cantidadExtras; i++) {
            String extra = MiEntradaSalida.leerTexto("Extra " + (i + 1) + ": ");
            coche.añadirExtra(extra);
        }

        service.insertarCoche(coche);
        System.out.println("Coche insertado correctamente.");
    }

    private static void importarCsv(ConcesionarioService service) throws IOException, DBException {
        String ruta = MiEntradaSalida.leerTexto("Ruta del CSV: ");
        service.importarDesdeCsv(Path.of(ruta));
        System.out.println("CSV importado correctamente.");
    }

    private static void borrarCoche(ConcesionarioService service) throws DBException {
        String matricula = MiEntradaSalida.leerTexto("Matrícula a borrar: ");
        service.borrarPorMatricula(matricula);
        System.out.println("Coche borrado.");
    }

    private static void modificarCoche(ConcesionarioService service) throws DBException {
        String matricula = MiEntradaSalida.leerTexto("Matrícula del coche a modificar: ");
        String marca = MiEntradaSalida.leerTexto("Nueva marca: ");
        String modelo = MiEntradaSalida.leerTexto("Nuevo modelo: ");
        Coche nuevo = new Coche(matricula, marca, modelo);

        int cantidadExtras = MiEntradaSalida.leerEnteroPositivo("Número de extras: ", true);
        for (int i = 0; i < cantidadExtras; i++) {
            String extra = MiEntradaSalida.leerTexto("Extra " + (i + 1) + ": ");
            nuevo.añadirExtra(extra);
        }

        service.modificarPorMatricula(matricula, nuevo);
        System.out.println("Coche modificado.");
    }

    private static void exportarCocheJson(ConcesionarioService service) throws IOException, DBException {
        String matricula = MiEntradaSalida.leerTexto("Matrícula del coche: ");
        String destino = MiEntradaSalida.leerTexto("Ruta JSON de salida: ");
        service.exportarCocheJson(matricula, Path.of(destino));
        System.out.println("Coche exportado a JSON.");
    }

    private static void importarConcesionarioJson(ConcesionarioService service) throws IOException, DBException {
        String ruta = MiEntradaSalida.leerTexto("Ruta del JSON del concesionario: ");
        service.importarConcesionarioDesdeJson(Path.of(ruta));
        System.out.println("Concesionario importado desde JSON.");
    }

    private static void importarCocheJson(ConcesionarioService service) throws IOException, DBException {
        String ruta = MiEntradaSalida.leerTexto("Ruta del JSON del coche: ");
        service.importarCocheDesdeJson(Path.of(ruta));
        System.out.println("Coche importado desde JSON.");
    }

    private static void mostrarMenu() {
        System.out.println();
        System.out.println("-------MENU PARA GESTIONAR COCHE------");
        System.out.println("1. Insertar coche");
        System.out.println("2. Importar coches desde CSV");
        System.out.println("3. Ordenar coches por matrícula");
        System.out.println("4. Borrar coche");
        System.out.println("5. Modificar coche");
        System.out.println("6. Exportar concesionario a JSON");
        System.out.println("7. Exportar coche a JSON");
        System.out.println("8. Importar concesionario desde JSON");
        System.out.println("9. Importar coche desde JSON");
        System.out.println("10. Crear resumen");
        System.out.println("0. Salir");
    }
}