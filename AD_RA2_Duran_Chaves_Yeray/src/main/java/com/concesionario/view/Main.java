package com.concesionario.view;

import com.concesionario.model.Coche;
import com.concesionario.services.ConcesionarioServices;
import com.concesionario.services.CreacionInformesService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Clase principal con el menú de texto para la gestión del concesionario.
 */
public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final ConcesionarioServices service = new ConcesionarioServices();
    private static final CreacionInformesService informesService = new CreacionInformesService();
    private static boolean conexionProbada = false;

    public static void main(String[] args) {
        int opcion = -1;
        do {
            mostrarMenu();
            try {
                opcion = Integer.parseInt(sc.nextLine().trim());
                ejecutarOpcion(opcion);
            } catch (NumberFormatException e) {
                System.err.println("[Error] Debes introducir un número entero válido.");
            }
        } while (opcion != 0);
        sc.close();
    }

    /**
     * Imprime el menú principal con las 12 opciones.
     */
    private static void mostrarMenu() {
        System.out.println("\n================ GESTIÓN CONCESIONARIO ================");
        System.out.println("Gestor activo: " + service.getGestorActivo().toUpperCase()
                + (conexionProbada ? " [CONECTADO]" : " [SIN PROBAR]"));
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
        System.out.print("Seleccione una opción: ");
    }

    /**
     * Ejecuta la opción seleccionada y gestiona de forma específica las excepciones.
     */
    private static void ejecutarOpcion(int opcion) {
        try {
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
        } catch (SQLException e) {
            gestionarSQLException(e);
        } catch (IOException e) {
            System.err.println("[Error de E/S] Problema al acceder al fichero: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("[Error de formato numérico] Valor no válido: " + e.getMessage());
        }
    }

    private static void opcionConectarBBDD() throws SQLException {
        System.out.print("Elige gestor (1 = MySQL, 2 = SQLite): ");
        String eleccion = sc.nextLine().trim();
        String tipo = "2".equals(eleccion) ? "sqlite" : ("1".equals(eleccion) ? "mysql" : null);

        if (tipo == null) {
            System.out.println("Selección no válida.");
            return;
        }

        String producto = service.conectarYProbar(tipo);
        conexionProbada = true;
        System.out.println("Conexión establecida correctamente con " + producto + " (" + tipo.toUpperCase() + ").");
    }

    private static void opcionInicializarTablas() throws SQLException {
        service.inicializarTablas();
        System.out.println("Tablas inicializadas correctamente en " + service.getGestorActivo().toUpperCase() + ".");
    }

    private static void opcionRegistrarPropietario() throws SQLException {
        System.out.print("DNI: ");
        String dni = sc.nextLine().trim();
        System.out.print("Nombre: ");
        String nombre = sc.nextLine().trim();
        System.out.print("Apellidos: ");
        String apellidos = sc.nextLine().trim();
        System.out.print("Teléfono: ");
        String telefono = sc.nextLine().trim();

        service.registrarPropietario(dni, nombre, apellidos, telefono);
        System.out.println("Propietario registrado correctamente.");
    }

    private static void opcionInsertarCoche() throws SQLException {
        System.out.print("Matrícula: ");
        String matricula = sc.nextLine().trim();
        Coche coche = pedirDatosCocheSinMatricula(matricula);
        service.insertarCoche(coche);
        System.out.println("Coche añadido al concesionario.");
    }

    private static void opcionImportarCSV() throws SQLException, IOException {
        int insertados = service.importarCochesCSV();
        System.out.println("Importación transaccional completada: " + insertados + " coches insertados.");
    }

    private static void opcionListarConcesionario() throws SQLException {
        List<Coche> coches = service.listarCochesConcesionario();
        System.out.println("\n--- COCHES EN EL CONCESIONARIO (SIN PROPIETARIO) ---");
        if (coches.isEmpty()) {
            System.out.println("No hay coches disponibles.");
        } else {
            coches.forEach(System.out::println);
        }
    }

    private static void opcionListarPropietarios() throws SQLException {
        List<String> coches = service.listarCochesPropietarios();
        System.out.println("\n--- COCHES CON PROPIETARIO ---");
        if (coches.isEmpty()) {
            System.out.println("No hay coches asignados a propietarios.");
        } else {
            coches.forEach(System.out::println);
        }
    }

    private static void opcionModificarCoche() throws SQLException {
        System.out.print("Matrícula del coche a modificar: ");
        String matricula = sc.nextLine().trim();
        Coche coche = pedirDatosCocheSinMatricula(matricula);
        if (service.modificarCoche(coche)) {
            System.out.println("Coche actualizado correctamente.");
        } else {
            System.out.println("No se encontró ningún coche con matrícula " + matricula);
        }
    }

    private static void opcionBorrarCoche() throws SQLException {
        System.out.print("Matrícula del coche a eliminar: ");
        String matricula = sc.nextLine().trim();
        if (service.borrarCoche(matricula)) {
            System.out.println("Coche eliminado correctamente.");
        } else {
            System.out.println("No se encontró ningún coche con matrícula " + matricula);
        }
    }

    private static void opcionRealizarTraspaso() throws SQLException {
        System.out.print("DNI del comprador: ");
        String dni = sc.nextLine().trim();
        System.out.print("Matrícula del coche: ");
        String matricula = sc.nextLine().trim();
        System.out.print("Monto económico (€): ");
        double monto = Double.parseDouble(sc.nextLine().trim());

        service.realizarTraspaso(dni, matricula, monto);
        System.out.println("Traspaso realizado con éxito.");
    }

    private static void opcionProcedimientoAlmacenado() throws SQLException {
        if (!"mysql".equalsIgnoreCase(service.getGestorActivo())) {
            System.out.println("[Aviso] Esta opción solo está disponible en MySQL.");
            return;
        }
        System.out.print("Introduce la marca a consultar: ");
        String marca = sc.nextLine().trim();
        List<Coche> coches = service.ejecutarProcedimientoPorMarca(marca);

        System.out.println("\n--- RESULTADO PROCEDIMIENTO sp_coches_por_marca ---");
        if (coches.isEmpty()) {
            System.out.println("No hay coches de la marca " + marca + ".");
        } else {
            coches.forEach(System.out::println);
        }
    }

    private static void opcionGenerarInforme() throws SQLException, IOException {
        String ruta = informesService.generarInformeResumen(service);
        System.out.println("Informe generado correctamente en: " + ruta);
    }

    /**
     * Método auxiliar para solicitar los datos de un coche sin repetir código entre alta y modificación.
     */
    private static Coche pedirDatosCocheSinMatricula(String matricula) {
        System.out.print("Marca: ");
        String marca = sc.nextLine().trim();
        System.out.print("Modelo: ");
        String modelo = sc.nextLine().trim();
        System.out.print("Extras (separados por comas): ");
        String extrasRaw = sc.nextLine().trim();
        List<String> extras = extrasRaw.isEmpty() ? new ArrayList<>() : Arrays.asList(extrasRaw.split(","));
        System.out.print("Precio (€): ");
        double precio = Double.parseDouble(sc.nextLine().trim());

        return new Coche(matricula, marca, modelo, extras, precio, null);
    }

    /**
     * Gestiona errores específicos de SQL como claves duplicadas o integridad referencial.
     */
    private static void gestionarSQLException(SQLException e) {
        if (e.getErrorCode() == 1062 || (e.getMessage() != null && e.getMessage().contains("UNIQUE constraint failed"))) {
            System.err.println("[Error de Clave Única] Ya existe un registro con ese DNI o Matrícula.");
        } else if (e.getErrorCode() == 1451 || (e.getMessage() != null && e.getMessage().contains("FOREIGN KEY constraint failed"))) {
            System.err.println("[Error de Integridad] No se puede eliminar porque tiene registros asociados.");
        } else {
            System.err.println("[Error SQL (" + e.getErrorCode() + ")] " + e.getMessage());
        }
    }
}