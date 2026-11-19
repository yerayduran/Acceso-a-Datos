package com.concesionario.view;

import com.concesionario.exceptions.ConcesionarioException;
import com.concesionario.exceptions.MiEntradaSalidaException;
import com.concesionario.model.Coche;
import com.concesionario.services.ConcesionarioServices;
import com.concesionario.services.CreacionInformesService;
import com.concesionario.util.MiEntradaSalida;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Clase principal con el menú de texto para la gestión del concesionario.
 */
public class Main {

    private static final ConcesionarioServices service = new ConcesionarioServices();
    private static final CreacionInformesService informesService = new CreacionInformesService();
    private static boolean conexionProbada = false;

    public static void main(String[] args) {
        int opcion = -1;
        do {
            mostrarCabeceraMenu();
            try {
                opcion = MiEntradaSalida.leerEnteroRango("Seleccione una opción: ", 0, 12);
                ejecutarOpcion(opcion);
            } catch (MiEntradaSalidaException e) {
                System.err.println("[Error de Entrada/Salida] " + e.getMessage());
            } catch (ConcesionarioException e) {
                System.err.println("[Error de Negocio / BBDD] " + e.getMessage());
            } catch (IOException e) {
                System.err.println("[Error de Ficheros] Problema al acceder al fichero: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("[Error Inesperado] " + e.getMessage());
            }
        } while (opcion != 0);
    }

    /**
     * Imprime la cabecera y el menú principal.
     */
    private static void mostrarCabeceraMenu() {
        System.out.println("\n================ GESTIÓN CONCESIONARIO ================");
        System.out.println("Gestor activo: " + service.getGestorActivo().toUpperCase() + (conexionProbada ? " [CONECTADO]" : " [SIN PROBAR]"));

        System.out.println("-------------------------------------------------------");
        System.out.println(" 1. Conectar BBDD (Elegir y probar MySQL o SQLite)");
        System.out.println(" 2. Crear Tablas");
        System.out.println(" 3. Registrar Propietario");
        System.out.println(" 4. Insertar Coche (Stock Concesionario)");
        System.out.println(" 5. Importar Coches (CSV transaccional)");
        System.out.println(" 6. Listar Coches (Concesionario)");
        System.out.println(" 7. Listar Coches (Propietarios con JOIN)");
        System.out.println(" 8. Modificar Coche");
        System.out.println(" 9. Borrar Coche");
        System.out.println("10. Realizar Traspaso (Transaccional)");
        System.out.println("11. Ejecutar Procedimiento Almacenado (Solo MySQL)");
        System.out.println("12. Generar Informe Resumen (informe_concesionario.txt)");
        System.out.println(" 0. Salir");
    }

    /**
     * Ejecuta la opción seleccionada propagando ConcesionarioException e IOException.
     */
    private static void ejecutarOpcion(int opcion) throws ConcesionarioException, IOException {
        switch (opcion) {
            case 1 -> opcionConectarBBDD();
            case 2 -> opcionInicializarTablas();
            case 3 -> opcionRegistrarPropietario();
            case 4 -> opcionInsertarCoche();
            case 5 -> opcionImportarCSV();
            case 6 -> opcionListarConcesionario();
            case 7 -> opcionListarPropietarios();
            case 8 -> opcionModificarCoche();
            case 9 -> opcionBorrarCoche();
            case 10 -> opcionRealizarTraspaso();
            case 11 -> opcionProcedimientoAlmacenado();
            case 12 -> opcionGenerarInforme();
            case 0 -> System.out.println("Saliendo del programa...");
            default -> System.out.println("Opción no válida.");
        }
    }

    private static void opcionConectarBBDD() throws ConcesionarioException {
        try {
            int eleccion = MiEntradaSalida.leerEnteroRango("Elige gestor (1 = MySQL, 2 = SQLite): ", 1, 2);
            String tipo = (eleccion == 2) ? "sqlite" : "mysql";

            String producto = service.conectarYProbar(tipo);
            conexionProbada = true;
            System.out.println("Conexión establecida correctamente con " + producto + " (" + tipo.toUpperCase() + ").");
        } catch (Exception e) {
            throw new ConcesionarioException("Fallo al conectar con la base de datos: " + e.getMessage());
        }
    }

    private static void opcionInicializarTablas() throws ConcesionarioException {
        try {
            service.inicializarTablas();
            System.out.println("Tablas inicializadas correctamente en " + service.getGestorActivo().toUpperCase() + ".");
        } catch (Exception e) {
            throw new ConcesionarioException("Error al crear las tablas: " + e.getMessage());
        }
    }

    private static void opcionRegistrarPropietario() throws ConcesionarioException {
        String dni = MiEntradaSalida.leerLineaNoVacia("DNI: ");
        String nombre = MiEntradaSalida.leerLineaNoVacia("Nombre: ");
        String apellidos = MiEntradaSalida.leerLineaNoVacia("Apellidos: ");
        String telefono = MiEntradaSalida.leerLinea("Teléfono (opcional): ").trim();

        try {
            service.registrarPropietario(dni, nombre, apellidos, telefono);
            System.out.println("Propietario registrado correctamente.");
        } catch (Exception e) {
            throw new ConcesionarioException("No se pudo registrar el propietario (¿DNI duplicado?): " + e.getMessage());
        }
    }

    private static void opcionInsertarCoche() throws ConcesionarioException {
        String matricula = MiEntradaSalida.leerLineaNoVacia("Matrícula: ");
        Coche coche = pedirDatosCocheSinMatricula(matricula);
        try {
            service.insertarCoche(coche);
            System.out.println("Coche añadido al concesionario.");
        } catch (Exception e) {
            throw new ConcesionarioException("Error al insertar el coche (¿Matrícula duplicada?): " + e.getMessage());
        }
    }

    private static void opcionImportarCSV() throws ConcesionarioException {
        try {
            int insertados = service.importarCochesCSV();
            System.out.println("Importación transaccional completada: " + insertados + " coches insertados.");
        } catch (Exception e) {
            throw new ConcesionarioException("Error en la importación CSV (Rollback ejecutado): " + e.getMessage());
        }
    }

    private static void opcionListarConcesionario() throws ConcesionarioException {
        try {
            List<Coche> coches = service.listarCochesConcesionario();
            System.out.println("\n--- COCHES EN EL CONCESIONARIO (SIN PROPIETARIO) ---");
            if (coches.isEmpty()) {
                System.out.println("No hay coches disponibles.");
            } else {
                coches.forEach(System.out::println);
            }
        } catch (Exception e) {
            throw new ConcesionarioException("Error al listar los coches: " + e.getMessage());
        }
    }

    private static void opcionListarPropietarios() throws ConcesionarioException {
        try {
            List<String> coches = service.listarCochesPropietarios();
            System.out.println("\n--- COCHES CON PROPIETARIO ---");
            if (coches.isEmpty()) {
                System.out.println("No hay coches asignados a propietarios.");
            } else {
                coches.forEach(System.out::println);
            }
        } catch (Exception e) {
            throw new ConcesionarioException("Error al listar los coches con propietario: " + e.getMessage());
        }
    }

    private static void opcionModificarCoche() throws ConcesionarioException {
        String matricula = MiEntradaSalida.leerLineaNoVacia("Matrícula del coche a modificar: ");
        Coche coche = pedirDatosCocheSinMatricula(matricula);
        try {
            if (service.modificarCoche(coche)) {
                System.out.println("Coche actualizado correctamente.");
            } else {
                System.out.println("No se encontró ningún coche con matrícula " + matricula);
            }
        } catch (Exception e) {
            throw new ConcesionarioException("Error al modificar el coche: " + e.getMessage());
        }
    }

    private static void opcionBorrarCoche() throws ConcesionarioException {
        String matricula = MiEntradaSalida.leerLineaNoVacia("Matrícula del coche a eliminar: ");
        try {
            if (service.borrarCoche(matricula)) {
                System.out.println("Coche eliminado correctamente.");
            } else {
                System.out.println("No se encontró ningún coche con matrícula " + matricula);
            }
        } catch (Exception e) {
            throw new ConcesionarioException("Error al borrar el coche (puede tener dependencias/traspasos): " + e.getMessage());
        }
    }

    private static void opcionRealizarTraspaso() throws ConcesionarioException {
        String dni = MiEntradaSalida.leerLineaNoVacia("DNI del comprador: ");
        String matricula = MiEntradaSalida.leerLineaNoVacia("Matrícula del coche: ");
        double monto = MiEntradaSalida.leerDouble("Monto económico (€): ");

        try {
            service.realizarTraspaso(dni, matricula, monto);
            System.out.println("Traspaso realizado con éxito.");
        } catch (Exception e) {
            throw new ConcesionarioException("Error en el traspaso (Rollback ejecutado): " + e.getMessage());
        }
    }

    private static void opcionProcedimientoAlmacenado() throws ConcesionarioException {
        if (!"mysql".equalsIgnoreCase(service.getGestorActivo())) {
            System.out.println("[Aviso] Esta opción solo está disponible en MySQL.");
            return;
        }
        String marca = MiEntradaSalida.leerLineaNoVacia("Introduce la marca a consultar: ");
        try {
            List<Coche> coches = service.ejecutarProcedimientoPorMarca(marca);
            System.out.println("\n--- RESULTADO PROCEDIMIENTO ---");
            if (coches.isEmpty()) {
                System.out.println("No hay coches de la marca " + marca + ".");
            } else {
                coches.forEach(System.out::println);
            }
        } catch (Exception e) {
            throw new ConcesionarioException("Error al ejecutar el procedimiento almacenado: " + e.getMessage());
        }
    }

    private static void opcionGenerarInforme() throws ConcesionarioException {
        try {
            String ruta = informesService.generarInformeResumen(service);
            System.out.println("Informe generado correctamente en: " + ruta);
        } catch (Exception e) {
            throw new ConcesionarioException("Error al generar el informe: " + e.getMessage());
        }
    }

    /**
     * Método auxiliar para solicitar los datos de un coche sin repetir código.
     */
    private static Coche pedirDatosCocheSinMatricula(String matricula) {
        String marca = MiEntradaSalida.leerLineaNoVacia("Marca: ");
        String modelo = MiEntradaSalida.leerLineaNoVacia("Modelo: ");
        String extrasRaw = MiEntradaSalida.leerLinea("Extras (separados por comas, opcional): ").trim();
        List<String> extras = extrasRaw.isEmpty() ? new ArrayList<>() : Arrays.asList(extrasRaw.split(","));
        double precio = MiEntradaSalida.leerDouble("Precio (€): ");

        return new Coche(matricula, marca, modelo, extras, precio, null);
    }
}