package com.concesionario.util;

import com.concesionario.exceptions.MiEntradaSalidaException;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Scanner;

/**
 * Clase de utilidad para la gestión de Entradas y Salidas por consola
 * con control de errores y reintentos automáticos.
 */
public class MiEntradaSalida {
    private static Scanner sc;

    private static Scanner getScanner() {
        if (sc == null) {
            sc = new Scanner(System.in);
        }
        return sc;
    }

    /**
     * Lee un entero mostrando el mensaje pasado como parámetro.
     * Si el formato es incorrecto, vuelve a pedirlo.
     *
     * @param mensaje El mensaje a mostrar
     * @return el entero leído por teclado
     */
    public static int leerEntero(String mensaje) {
        int integer = 0;
        boolean flag = true;

        while (flag) {
            System.out.println(mensaje);
            try {
                integer = Integer.parseInt(getScanner().nextLine().trim());
                flag = false;
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe introducir un número entero.");
            }
        }
        return integer;
    }

    /**
     * Número entero positivo o cero según se acepte.
     *
     * @param mensaje      El mensaje a mostrar
     * @param seAceptaCero Si el 0 está incluido
     * @return El entero leído por teclado
     */
    public static int leerEnteroPositivo(String mensaje, boolean seAceptaCero) {
        int a;
        do {
            a = leerEntero(mensaje);
            if (a < 0 || (a == 0 && !seAceptaCero)) {
                System.out.println("Número no válido.");
            }
        } while (a < 0 || (a == 0 && !seAceptaCero));
        return a;
    }

    /**
     * Leer un decimal de tipo double con reintento ante errores.
     *
     * @param mensaje El mensaje a introducir
     * @return Lo introducido por el usuario
     */
    public static double leerDouble(String mensaje) {
        double valor = 0;
        boolean flag = true;

        while (flag) {
            System.out.println(mensaje);
            try {
                valor = Double.parseDouble(getScanner().nextLine().trim());
                flag = false;
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe introducir un número decimal válido.");
            }
        }
        return valor;
    }

    /**
     * Leer un decimal de tipo Float.
     *
     * @param mensaje mensaje a mostrar
     * @return lo introducido por el usuario
     */
    public static float leerFloat(String mensaje) {
        float valor = 0;
        boolean flag = true;

        while (flag) {
            System.out.println(mensaje);
            try {
                valor = Float.parseFloat(getScanner().nextLine().trim());
                flag = false;
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe introducir un número decimal.");
            }
        }
        return valor;
    }

    /**
     * Generar un número aleatorio determinado por un máximo.
     *
     * @param max hasta qué número se va a generar
     * @return número generado aleatoriamente
     */
    public static int generaAleatorio(int max) {
        return (int) (Math.random() * max + 1);
    }

    /**
     * Generar un número aleatorio entre un máximo y un mínimo.
     *
     * @param min              Primer número del intervalo
     * @param max              Segundo número del intervalo
     * @param seAceptaElMaximo ¿El máximo entra en ese intervalo?
     * @return El número generado aleatoriamente
     */
    public static int generaAleatorioEntre(int min, int max, boolean seAceptaElMaximo) {
        int ventana;
        if (seAceptaElMaximo) {
            ventana = max - min + 1;
        } else {
            ventana = max - min;
        }
        return (int) (Math.random() * ventana) + min;
    }

    /**
     * Leer una cadena de texto simple (utiliza next).
     *
     * @param mensaje Mensaje a mostrar
     * @return String leído
     */
    public static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return getScanner().next();
    }

    /**
     * Leer entero en un rango determinado.
     *
     * @param mensaje mensaje a mostrar
     * @param min     valor mínimo incluido
     * @param max     valor máximo incluido
     * @return Número leído por teclado
     */
    public static int leerEnteroRango(String mensaje, int min, int max) throws MiEntradaSalidaException {
        if (min > max) {
            throw new MiEntradaSalidaException("El mínimo es mayor que el máximo.");
        }
        int a;
        do {
            a = leerEntero(mensaje);
            if (a < min || a > max) {
                System.out.println("Número no válido, está fuera del rango (" + min + " - " + max + ").");
            }
        } while (a < min || a > max);
        return a;
    }

    /**
     * Recoger el carácter 'S' o 'N'.
     *
     * @param mensaje Mensaje a mostrar
     * @return El carácter obtenido por teclado
     */
    public static char leerSN(String mensaje) {
        char caracter;
        do {
            String texto = leerTexto(mensaje).toUpperCase();
            if (texto.isEmpty()) {
                caracter = ' ';
            } else {
                caracter = texto.charAt(0);
            }
            if (caracter != 'S' && caracter != 'N') {
                System.out.println("Respuesta no válida. Introduce 'S' o 'N'.");
            }
        } while (caracter != 'S' && caracter != 'N');
        return caracter;
    }

    /**
     * Leer una línea completa de texto (permite blancos si es opcional).
     *
     * @param mensaje mensaje a mostrar
     * @return mensaje introducido
     */
    public static String leerLinea(String mensaje) {
        System.out.print(mensaje);
        return getScanner().nextLine();
    }

    /**
     * Lee una línea de texto obligatoria. Si el usuario pulsa Enter sin escribir nada
     * o deja espacios en blanco, vuelve a pedir el dato obligatoriamente.
     *
     * @param mensaje Mensaje a mostrar
     * @return String no vacío
     */
    public static String leerLineaNoVacia(String mensaje) {
        String texto;
        do {
            System.out.print(mensaje);
            texto = getScanner().nextLine().trim();
            if (texto.isEmpty()) {
                System.out.println("Error: Este campo no puede estar vacío. Por favor, introduce un valor.");
            }
        } while (texto.isEmpty());
        return texto;
    }

    public static LocalDate fecha(String mensaje) throws MiEntradaSalidaException {
        int año = leerEnteroPositivo("Introduce el año: ", true);
        int mes = leerEnteroPositivo("Introduce el mes: ", true);
        int dia = leerEnteroPositivo("Introduce el día: ", true);
        try {
            return LocalDate.of(año, mes, dia);
        } catch (DateTimeException e) {
            throw new MiEntradaSalidaException("Fecha no válida.");
        }
    }

    public static char leerChar(String mensaje) throws MiEntradaSalidaException {
        String texto = leerLineaNoVacia(mensaje);
        if (texto.length() != 1) {
            throw new MiEntradaSalidaException("Debes introducir un único carácter.");
        }
        return texto.charAt(0);
    }
}