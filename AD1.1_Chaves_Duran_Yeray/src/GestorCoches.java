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
    private static final String FICHERO_BINARIO = "fichero.dat";

    private static final int LONGITUD_MATRICULA = 7;
    private static final int LONGITUD_MARCA = 32;
    private static final int LONGITUD_MODELO = 32;

    private static final int LONGITUD_REGISTRO = LONGITUD_MATRICULA + LONGITUD_MARCA + LONGITUD_MODELO;

    private static final BufferedReader ENTRADA = new BufferedReader(new InputStreamReader(System.in));




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







    /**
     * Carga los registros del fichero CSV en el fichero binario.
     *
     * La primera línea del CSV se considera una cabecera y se ignora.
     * Antes de cargar la información se vacía el fichero binario para evitar
     * duplicar los registros si se ejecuta esta opción varias veces.
     *
     * @throws IOException si se produce un error al leer o escribir
     */
    private static void cargarInformacion() throws IOException {
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
                    System.out.println("Línea ignorada por tener un formato incorrecto: " + linea);
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
     * Inserta un nuevo registro en una posición que tu quieras.
     *
     * @throws IOException si se produce un error con el fichero
     */
    private static void insertarRegistro() throws IOException {
        List<String[]> registros = leerRegistros();

        int posicionUsuario = leerEntero("Introduce la posición de inserción (1-" + (registros.size() + 1) + "): ");

        if (posicionUsuario < 1 || posicionUsuario > registros.size() + 1) {
            System.out.println("Posición no válida. Debe estar entre 1 y " + (registros.size() + 1) + ".");
            return;
        }

        String[] nuevoRegistro = pedirDatosRegistro();

        if (buscarPorMatricula(registros, nuevoRegistro[0]) != -1) {
            System.out.println("Ya existe un registro con esa matrícula en el fichero.");
            return;
        }

        int indiceInterno = posicionUsuario - 1;

        registros.add(indiceInterno, nuevoRegistro);
        guardarRegistros(registros);

        System.out.println("Registro insertado correctamente en la posición " + posicionUsuario + ".");
    }





    /**
     * Ordena todos los registros alfabéticamente por matrícula con el flujo sort.
     *
     * @throws IOException si se produce un error con el fichero
     */
    private static void ordenarPorMatricula() throws IOException {
        List<String[]> registros = leerRegistros();

        registros.sort(Comparator.comparing(registro -> registro[0]));

        guardarRegistros(registros);

        System.out.println("Registros ordenados por matrícula.");
    }





    /**
     * Permite elegir si quieres borrar por matrícula o por posición.
     *
     * @throws IOException si se produce un error con el fichero
     */
    private static void borrarRegistro() throws IOException {
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
    private static void borrarPorMatricula(List<String[]> registros) throws IOException {

        String matricula = leerCadena("Introduce la matrícula: ");
        int indice = buscarPorMatricula(registros, matricula);

        if (indice == -1) {
            System.out.println("No existe dicha matrícula.");
            return;
        }

        registros.remove(indice);
        guardarRegistros(registros);

        System.out.println("Registro borrado de manera correcta.");
    }






    /**
     * Borra un registro utilizando su posición.
     *
     * @param registros lista de registros
     * @throws IOException si se produce un error al guardar
     */
    private static void borrarPorPosicion(List<String[]> registros) throws IOException {

        int posicionUsuario = leerEntero("Introduce la posición: ");

        if (posicionUsuario < 1 || posicionUsuario > registros.size()) {

            System.out.println("Posición inválida.");
            return;
        }

        int indiceInterno = posicionUsuario - 1;

        registros.remove(indiceInterno);
        guardarRegistros(registros);

        System.out.println("Registro " + posicionUsuario + " borrado correctamente.");
    }





    /**
     * Permite modificar la marca y el modelo de un registro.
     *
     * La matrícula no se modifica porque es el campo clave del registro.
     *
     * @throws IOException si se produce un error con el fichero
     */
    private static void modificarRegistro() throws IOException {
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

            int posicionUsuario = leerEntero("Introduce la posición: ");

            if (posicionUsuario >= 1 && posicionUsuario <= registros.size()) {

                indice = posicionUsuario - 1;

            } else {

                indice = -1;

            }
        }else {
            System.out.println("Opción no valida.");
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

        System.out.println("Registro modificado de manera correcto.");
    }






    /**
     * Muestra todos los registros y su posición.
     *
     * @throws IOException si se produce un error al leer
     */
    private static void mostrarRegistros() throws IOException {

        List<String[]> registros = leerRegistros();

        if (registros.isEmpty()) {
            System.out.println("No hay registros.");
            return;
        }

        System.out.println();
        System.out.println("-----REGISTROS-----");

        for (int i = 0; i < registros.size(); i++) {
            String[] registro = registros.get(i);

            System.out.println((i + 1)
                            + " - Matrícula: " + registro[0]
                            + ", Marca: " + registro[1]
                            + ", Modelo: " + registro[2]
            );
        }
    }






    /**
     * Lee todos los registros almacenados en el fichero binario.
     *
     * @return lista de registros, donde cada registro es un array de tres posiciones: matrícula, marca y modelo
     * @throws IOException si se produce un error de lectura
     */
    private static List<String[]> leerRegistros() throws IOException {
        List<String[]> registros = new ArrayList<>();

        File fichero = new File(FICHERO_BINARIO);

        if (!fichero.exists() || fichero.length() == 0) {
            return registros;
        }

        if (fichero.length() % LONGITUD_REGISTRO != 0) {
            throw new IOException("El fichero no contiene registros completos.");
        }

        try (RandomAccessFile acessoArchivo = new RandomAccessFile(fichero, "r")) {

            while (acessoArchivo.getFilePointer() < acessoArchivo.length()) {
                String matricula = leerCampoFijo(acessoArchivo, LONGITUD_MATRICULA);

                String marca = leerCampoFijo(acessoArchivo, LONGITUD_MARCA);

                String modelo = leerCampoFijo(acessoArchivo, LONGITUD_MODELO);

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
    private static void guardarRegistros(List<String[]> registros) throws IOException {

        try (RandomAccessFile acessoArchivo = new RandomAccessFile(FICHERO_BINARIO, "rw")) {

            acessoArchivo.setLength(0); //Aqui se borra el registro anterior para reconstruir el fichero

            for (String[] registro : registros) {
                escribirCampoFijo(acessoArchivo, registro[0], LONGITUD_MATRICULA);

                escribirCampoFijo(acessoArchivo, registro[1], LONGITUD_MARCA);

                escribirCampoFijo(acessoArchivo, registro[2], LONGITUD_MODELO);
            }
        }
    }






    /**
     * Escribe una cadena con longitud fija.
     *
     * Si la cadena es menor que el tamaño indicado en los atributos anteriormente mencionados, se completa con
     * espacios. Si es mayor, se lanza una excepción.
     *
     * @param acessoArchivo fichero de acceso aleatorio
     * @param texto texto que se escribirá
     * @param longitud longitud fija del campo
     * @throws IOException si la cadena supera la longitud permitida
     */
    private static void escribirCampoFijo(RandomAccessFile acessoArchivo, String texto, int longitud) throws IOException {

        String valor = texto == null ? "" : texto;

        if (valor.length() > longitud) {
            throw new IOException("El campo supera la longitud máxima de " + longitud + " caracteres.");
        }

        StringBuilder campo = new StringBuilder(valor);

        while (campo.length() < longitud) {
            campo.append(' ');
        }

        acessoArchivo.write(campo.toString().getBytes(StandardCharsets.UTF_8));
    }




    /**
     * Lee una cadena de tamaño fijo desde el fichero.
     *
     * @param acessoArchivo fichero de acceso aleatorio
     * @param longitud número de bytes que se leerán
     * @return texto leído sin los espacios finales
     * @throws IOException si no se puede leer el campo completo
     */
    private static String leerCampoFijo(RandomAccessFile acessoArchivo, int longitud) throws IOException {

        byte[] datos = new byte[longitud];

        acessoArchivo.readFully(datos);

        return new String(datos, StandardCharsets.UTF_8).trim();
    }





    /**
     * Busca un registro por matrícula.
     *
     * @param registros lista de registros
     * @param matricula matrícula que se desea buscar
     * @return posición del registro o -1 si no existe
     */
    private static int buscarPorMatricula(List<String[]> registros, String matricula) {

        for (int i = 0; i < registros.size(); i++) {
            if (registros.get(i)[0].equalsIgnoreCase(matricula)) {
                return i;
            }
        }

        return -1;
    }




    /**
     * Comprueba que los campos de un registro sean válidos.
     *
     * @param matricula matrícula del registro
     * @param marca marca del vehículo
     * @param modelo modelo del vehículo
     * @return true si todos los campos son válidos
     */
    private static boolean registroValido(String matricula, String marca, String modelo) {

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
    private static String[] pedirDatosRegistro() {
        String matricula = leerCampo("Introduce la matrícula: ", LONGITUD_MATRICULA);

        String marca = leerCampo("Introduce la marca: ", LONGITUD_MARCA);

        String modelo = leerCampo("Introduce el modelo: ", LONGITUD_MODELO);

        return new String[]{matricula, marca, modelo};
    }




    /**
     * Solicita una cadena al usuario comprobando su longitud.
     *
     * @param mensaje mensaje mostrado al usuario
     * @param longitudMaxima longitud máxima permitida
     * @return cadena introducida
     */
    private static String leerCampo(String mensaje, int longitudMaxima) {

        String valor;

        do {
            valor = leerCadena(mensaje);

            if (valor.isBlank()) {
                System.out.println("El campo no puede estar vacío.");
            } else if (valor.length() > longitudMaxima) {
                System.out.println("El campo no puede superar " + longitudMaxima + " caracteres.");
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
    private static int leerEntero(String mensaje) {
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
    private static String leerCadena(String mensaje) {
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