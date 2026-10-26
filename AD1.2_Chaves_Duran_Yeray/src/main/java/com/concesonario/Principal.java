package com.concesonario;

import java.io.IOException;

public class Principal {

    static void main(String[] args) {
        int opcion;

        do {
            mostrarMenu();
            opcion = leerEntero("Elige una opción: ");

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
                        borrarPorMatricula(registros);
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


}

