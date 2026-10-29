package com.concesonario.view;

import com.concesonario.utils.MiEntradaSalida;

import java.io.IOException;

public class Menu {
    public static void main(String[] args) {
        int opcion;

        do {
            mostrarMenu();
            opcion = MiEntradaSalida.leerEntero("Elige una opción: ");

            try {
                switch (opcion) {
                    case 1:
                        cargarInformacion();
                        break;
                    case 2:
                        insertarRegistro();
                        break;
                    case 3:
                        ordenarPorMatricula();
                        break;
                    case 4:
                        borrarRegistro();
                        break;
                    case 5:
                        modificarRegistro();
                        break;
                    case 6:
                        mostrarRegistros();
                        break;
                    case 0:
                        System.out.println("Adios joven amo...");
                        break;
                    default:
                        System.out.println("Elije otra opcion.");
                }
            } catch (IOException e) {
                System.out.println("Error al introducir o devolver información: " + e.getMessage());
            }
        } while (opcion != 0);
    }






    private static void mostrarMenu() {
        System.out.println();
        System.out.println("-------MENU PARA GESTIONAR COCHE------");
        System.out.println("1. Cargar información desde CSV");
        System.out.println("2. Insertar un coche");
        System.out.println("3. Ordenar por matrícula");
        System.out.println("4. Borrar un registro");
        System.out.println("5. Modificar un registro");
        System.out.println("6. Mostrar registros");
        System.out.println("0. Salir");
    }
}
