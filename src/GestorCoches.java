import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GestorCoches {

    private static final String FICHERO_CSV = "BBDD Coches.csv";
    private static final String FICHERO_BINARIO = "fichero.txt";

    private static final int LONGITUD_MATRICULA = 7;
    private static final int LONGITUD_MARCA = 32;
    private static final int LONGITUD_MODELO = 32;

    private static final int LONGITUD_REGISTRO = LONGITUD_MATRICULA + LONGITUD_MARCA + LONGITUD_MODELO;

    private static final BufferedReader ENTRADA = new BufferedReader(new InputStreamReader(System.in));

    /**
     * Método principal del programa.
     *
     * @param args argumentos recibidos desde la línea de comandos
     */
    public static void main(String[] args) {
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
                        borrarRegistro();
                        break;

                    case 5:
                        modificarRegistro();
                        break;

                    case 6:
                        mostrarRegistros();
                        break;

                    case 0:
                        System.out.println("Saliendo del programa...");
                        break;

                    default:
                        System.out.println("Opción inválida.");
                }
            } catch (IOException e) {
                System.out.println("Error de entrada/salida: " + e.getMessage());
            }

        } while (opcion != 0);
    }

    /**
     * Muestra las opciones disponibles del menú.
     */
    public static void mostrarMenu() {
        System.out.println();
        System.out.println("===== BASE DE DATOS DE COCHES =====");
        System.out.println("1. Cargar información desde CSV");
        System.out.println("2. Insertar un coche");
        System.out.println("3. Ordenar por matrícula");
        System.out.println("4. Borrar un registro");
        System.out.println("5. Modificar un registro");
        System.out.println("6. Mostrar registros");
        System.out.println("0. Salir");
    }

    /**
     * Carga los registros del fichero CSV en el fichero binario.
     *
     * La primera línea del CSV se considera una cabecera y se ignora.
     * Antes de cargar la información se vacía el fichero binario para evitar
     * duplicar los registros si se ejecuta esta opción varias veces.
     *
     * @throws IOException si se produce un error al leer o escribir
     */
    public static void cargarInformacion() throws IOException {
        File fichero = new File(FICHERO_CSV);

        if (!fichero.exists()) {
            System.out.println("No existe el fichero: " + FICHERO_CSV);
            return;
        }

        List<String[]> registros = new ArrayList<>();

        try (BufferedReader lector = new BufferedReader(new FileReader(fichero))) {

            String linea;
            boolean primeraLinea = true;

            while ((linea = lector.readLine()) != null) {
                if (primeraLinea) {
                    primeraLinea = false;
                    continue;
                }

                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] campos = linea.split(",", -1);

                if (campos.length < 3) {
                    System.out.println("Línea ignorada por formato incorrecto: " + linea);
                    continue;
                }

                String matricula = campos[0].trim();
                String marca = campos[1].trim();
                String modelo = campos[2].trim();

                if (!registroValido(matricula, marca, modelo)) {
                    System.out.println("Línea ignorada por superar " + "la longitud permitida: " + linea);
                    continue;
                }

                if (buscarPorMatricula(registros, matricula) != -1) {
                    System.out.println("Matrícula repetida ignorada: " + matricula);
                    continue;
                }

                registros.add(new String[]{matricula, marca, modelo});
            }
        }

        guardarRegistros(registros);

        System.out.println("Información cargada correctamente.");
        System.out.println("Registros cargados: " + registros.size());
    }

    /**
     * Inserta un nuevo registro en una posición determinada.
     *
     * @throws IOException si se produce un error con el fichero
     */
    public static void insertarRegistro() throws IOException {
        List<String[]> registros = leerRegistros();

        int posicion = leerEntero("Introduce la posición de inserción: ");

        if (posicion < 0 || posicion > registros.size()) {
            System.out.println("Posición inválida.");
            return;
        }

        String[] nuevoRegistro = pedirDatosRegistro();

        if (buscarPorMatricula(registros, nuevoRegistro[0]) != -1) {
            System.out.println("Ya existe un registro con esa matrícula.");
            return;
        }

        registros.add(posicion, nuevoRegistro);
        guardarRegistros(registros);

        System.out.println("Registro insertado correctamente.");
    }

    /**
     * Ordena todos los registros alfabéticamente por matrícula.
     *
     * @throws IOException si se produce un error con el fichero
     */
    public static void ordenarPorMatricula() throws IOException {
        List<String[]> registros = leerRegistros();

        registros.sort(Comparator.comparing(registro -> registro[0]));

        guardarRegistros(registros);

        System.out.println("Registros ordenados por matrícula.");
    }

    /**
     * Permite elegir si se desea borrar por matrícula o por posición.
     *
     * @throws IOException si se produce un error con el fichero
     */
    public static void borrarRegistro() throws IOException {
        System.out.println();
        System.out.println("1. Borrar por matrícula");
        System.out.println("2. Borrar por posición");

        int opcion = leerEntero("Selecciona una opción: ");

        List<String[]> registros = leerRegistros();

        switch (opcion) {
            case 1:
                borrarPorMatricula(registros);
                break;

            case 2:
                borrarPorPosicion(registros);
                break;

            default:
                System.out.println("Opción inválida.");
        }
    }

    /**
     * Borra un registro buscando su matrícula.
     *
     * @param registros lista de registros
     * @throws IOException si se produce un error al guardar
     */
    public static void borrarPorMatricula(List<String[]> registros) throws IOException {

        String matricula = leerCadena("Introduce la matrícula: ");
        int indice = buscarPorMatricula(registros, matricula);

        if (indice == -1) {
            System.out.println("No existe esa matrícula.");
            return;
        }

        registros.remove(indice);
        guardarRegistros(registros);

        System.out.println("Registro borrado correctamente.");
    }

    /**
     * Borra un registro utilizando su posición.
     *
     * @param registros lista de registros
     * @throws IOException si se produce un error al guardar
     */
    public static void borrarPorPosicion(List<String[]> registros) throws IOException {

        int posicion = leerEntero("Introduce la posición: ");

        if (posicion < 0 || posicion >= registros.size()) {
            System.out.println("Posición inválida.");
            return;
        }

        registros.remove(posicion);
        guardarRegistros(registros);

        System.out.println("Registro borrado correctamente.");
    }

    /**
     * Permite modificar la marca y el modelo de un registro.
     *
     * <p>La matrícula no se modifica porque es el campo clave del registro.</p>
     *
     * @throws IOException si se produce un error con el fichero
     */
    public static void modificarRegistro() throws IOException {
        List<String[]> registros = leerRegistros();

        System.out.println();
        System.out.println("1. Modificar por matrícula");
        System.out.println("2. Modificar por posición");

        int opcion = leerEntero("Selecciona una opción: ");
        int indice;

        if (opcion == 1) {
            String matricula = leerCadena("Introduce la matrícula: ");

            indice = buscarPorMatricula(registros, matricula);
        } else if (opcion == 2) {
            int posicion = leerEntero("Introduce la posición: ");

            indice = posicionValida(registros, posicion) ? posicion : -1;
        } else {
            System.out.println("Opción inválida.");
            return;
        }

        if (indice == -1) {
            System.out.println("No se encontró el registro.");
            return;
        }

        String marca = leerCampo("Introduce la nueva marca: ", LONGITUD_MARCA);

        String modelo = leerCampo("Introduce el nuevo modelo: ", LONGITUD_MODELO);

        registros.get(indice)[1] = marca;
        registros.get(indice)[2] = modelo;

        guardarRegistros(registros);

        System.out.println("Registro modificado correctamente.");
    }

    /**
     * Muestra todos los registros y su posición.
     *
     * @throws IOException si se produce un error al leer
     */
    public static void mostrarRegistros() throws IOException {List<String[]> registros = leerRegistros();

        if (registros.isEmpty()) {
            System.out.println("No hay registros.");
            return;
        }

        System.out.println();
        System.out.println("===== REGISTROS =====");

        for (int i = 0; i < registros.size(); i++) {
            String[] registro = registros.get(i);
            System.out.println(i + " - Matrícula: " + registro[0] + ", Marca: " + registro[1] + ", Modelo: " + registro[2]);
        }
    }

    /**
     * Lee todos los registros almacenados en el fichero binario.
     *
     * @return lista de registros, donde cada registro es un array de tres posiciones: matrícula, marca y modelo
     * @throws IOException si se produce un error de lectura
     */
    public static List<String[]> leerRegistros() throws IOException {
        List<String[]> registros = new ArrayList<>();

        File fichero = new File(FICHERO_BINARIO);

        if (!fichero.exists() || fichero.length() == 0) {
            return registros;
        }

        if (fichero.length() % LONGITUD_REGISTRO != 0) {
            throw new IOException("El fichero no contiene registros completos.");
        }

        try (RandomAccessFile raf = new RandomAccessFile(fichero, "r")) {

            while (raf.getFilePointer() < raf.length()) {
                String matricula = leerCampoFijo(raf, LONGITUD_MATRICULA);

                String marca = leerCampoFijo(raf, LONGITUD_MARCA);

                String modelo = leerCampoFijo(raf, LONGITUD_MODELO);

                registros.add(new String[]{matricula, marca, modelo});
            }
        }

        return registros;
    }

    /**
     * Guarda todos los registros sobrescribiendo el fichero binario.
     *
     * @param registros lista de registros que se guardará
     * @throws IOException si se produce un error de escritura
     */
    public static void guardarRegistros(List<String[]> registros) throws IOException {

        try (RandomAccessFile raf = new RandomAccessFile(FICHERO_BINARIO, "rw")) {

            // Se borra el contenido anterior para reconstruir el fichero.
            raf.setLength(0);

            for (String[] registro : registros) {
                escribirCampoFijo(raf, registro[0], LONGITUD_MATRICULA);

                escribirCampoFijo(raf, registro[1], LONGITUD_MARCA);

                escribirCampoFijo(
                        raf,
                        registro[2],
                        LONGITUD_MODELO
                );
            }
        }
    }

    /**
     * Escribe una cadena con longitud fija.
     *
     * <p>Si la cadena es menor que el tamaño indicado, se completa con
     * espacios. Si es mayor, se lanza una excepción.</p>
     *
     * @param raf fichero de acceso aleatorio
     * @param texto texto que se escribirá
     * @param longitud longitud fija del campo
     * @throws IOException si la cadena supera la longitud permitida
     */
    public static void escribirCampoFijo(
            RandomAccessFile raf,
            String texto,
            int longitud) throws IOException {

        String valor = texto == null ? "" : texto;

        if (valor.length() > longitud) {
            throw new IOException(
                    "El campo supera la longitud máxima de "
                            + longitud + " caracteres."
            );
        }

        StringBuilder campo = new StringBuilder(valor);

        while (campo.length() < longitud) {
            campo.append(' ');
        }

        raf.write(campo.toString().getBytes(StandardCharsets.ISO_8859_1));
    }

    /**
     * Lee una cadena de tamaño fijo desde el fichero.
     *
     * @param raf fichero de acceso aleatorio
     * @param longitud número de bytes que se leerán
     * @return texto leído sin los espacios finales
     * @throws IOException si no se puede leer el campo completo
     */
    public static String leerCampoFijo(
            RandomAccessFile raf,
            int longitud) throws IOException {

        byte[] datos = new byte[longitud];

        raf.readFully(datos);

        return new String(
                datos,
                StandardCharsets.ISO_8859_1
        ).trim();
    }

    /**
     * Busca un registro por matrícula.
     *
     * @param registros lista de registros
     * @param matricula matrícula que se desea buscar
     * @return posición del registro o -1 si no existe
     */
    public static int buscarPorMatricula(
            List<String[]> registros,
            String matricula) {

        for (int i = 0; i < registros.size(); i++) {
            if (registros.get(i)[0].equalsIgnoreCase(matricula)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Comprueba si una posición pertenece a la lista.
     *
     * @param registros lista de registros
     * @param posicion posición que se desea comprobar
     * @return true si la posición es válida
     */
    public static boolean posicionValida(
            List<String[]> registros,
            int posicion) {

        return posicion >= 0 && posicion < registros.size();
    }

    /**
     * Comprueba que los campos de un registro sean válidos.
     *
     * @param matricula matrícula del registro
     * @param marca marca del vehículo
     * @param modelo modelo del vehículo
     * @return true si todos los campos son válidos
     */
    public static boolean registroValido(
            String matricula,
            String marca,
            String modelo) {

        return matricula != null
                && marca != null
                && modelo != null
                && !matricula.isBlank()
                && !marca.isBlank()
                && !modelo.isBlank()
                && matricula.length() <= LONGITUD_MATRICULA
                && marca.length() <= LONGITUD_MARCA
                && modelo.length() <= LONGITUD_MODELO;
    }

    /**
     * Solicita al usuario los datos completos de un nuevo registro.
     *
     * @return array con matrícula, marca y modelo
     */
    public static String[] pedirDatosRegistro() {
        String matricula = leerCampo(
                "Introduce la matrícula: ",
                LONGITUD_MATRICULA
        );

        String marca = leerCampo(
                "Introduce la marca: ",
                LONGITUD_MARCA
        );

        String modelo = leerCampo(
                "Introduce el modelo: ",
                LONGITUD_MODELO
        );

        return new String[]{matricula, marca, modelo};
    }

    /**
     * Solicita una cadena al usuario comprobando su longitud.
     *
     * @param mensaje mensaje mostrado al usuario
     * @param longitudMaxima longitud máxima permitida
     * @return cadena introducida
     */
    public static String leerCampo(
            String mensaje,
            int longitudMaxima) {

        String valor;

        do {
            valor = leerCadena(mensaje);

            if (valor.isBlank()) {
                System.out.println("El campo no puede estar vacío.");
            } else if (valor.length() > longitudMaxima) {
                System.out.println(
                        "El campo no puede superar "
                                + longitudMaxima
                                + " caracteres."
                );
            }
        } while (valor.isBlank() || valor.length() > longitudMaxima);

        return valor;
    }

    /**
     * Lee un número entero controlando el formato introducido.
     *
     * @param mensaje mensaje mostrado al usuario
     * @return número entero introducido
     */
    public static int leerEntero(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Integer.parseInt(ENTRADA.readLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Debes introducir un número entero.");
            } catch (IOException e) {
                System.out.println("Error al leer la entrada.");
                return 0;
            }
        }
    }

    /**
     * Lee una cadena desde la entrada estándar.
     *
     * @param mensaje mensaje mostrado al usuario
     * @return texto introducido
     */
    public static String leerCadena(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return ENTRADA.readLine().trim();
            } catch (IOException e) {
                System.out.println("Error al leer la entrada.");
            }
        }
    }
}