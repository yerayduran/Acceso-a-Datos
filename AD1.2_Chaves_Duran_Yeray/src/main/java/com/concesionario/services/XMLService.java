package com.concesionario.services;

import com.concesionario.exceptions.DBException;
import com.concesionario.model.Concesionario;
import com.concesionario.utils.PropertiesReader;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.io.IOException;

/**
 * Gestiona la lectura y escritura del archivo XML que actúa como base de datos.
 * Usa JAXB para convertir entre objetos Java y XML.
 */
public class XMLService {

    private final String rutaOriginal;
    private final String rutaPlantilla;

    public XMLService() {
        this.rutaOriginal = PropertiesReader.get("original_db");
        this.rutaPlantilla = PropertiesReader.get("plantilla_db");
    }

    /**
     * Comprueba si el archivo XML principal existe.
     * Si no existe, crea un archivo vacío en la ruta configurada.
     */
    public void comprobarFicheroOriginalBb() throws DBException {
        File originalBd = new File(rutaOriginal);

        if (!originalBd.exists()) {
            crearRepositorioGeneral(originalBd);
            System.out.println("Se ha creado la base de datos: " + originalBd.getAbsolutePath());
        } else {
            System.out.println("BBDD --> " + originalBd.getAbsolutePath());
        }
    }

    /**
     * Crea el archivo XML vacío, incluyendo las carpetas necesarias.
     */
    private void crearRepositorioGeneral(File destino) throws DBException {
        try {
            File carpetaPadre = destino.getParentFile();

            if (carpetaPadre != null) {
                carpetaPadre.mkdirs();
            }

            destino.createNewFile();

        } catch (IOException e) {
            throw new DBException("Error creando el archivo XML vacío: " + e.getMessage());
        }
    }

    /**
     * Lee la base de datos XML y la convierte en un objeto Concesionario.
     */
    public Concesionario leerBaseDeDatos() throws DBException {
        try {
            File archivoBD = new File(rutaOriginal);

            if (!archivoBD.exists()) {
                comprobarFicheroOriginalBb();
            }

            if (archivoBD.length() == 0) {
                System.out.println("No existen registros: se devuelve un concesionario vacío.");
                return new Concesionario();
            }

            JAXBContext context = JAXBContext.newInstance(Concesionario.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            Concesionario concesionario = (Concesionario) unmarshaller.unmarshal(archivoBD);

            if (concesionario == null) {
                concesionario = new Concesionario();
            }

            return concesionario;

        } catch (JAXBException e) {
            // Si el archivo está corrupto o vacío con formato inválido, devolvemos uno nuevo para evitar bloqueos
            System.err.println("Aviso: El archivo XML está vacío o corrupto. Se inicializará un concesionario nuevo.");
            return new Concesionario();
        }
    }

    /**
     * Guarda un objeto Concesionario como fichero XML.
     */
    public void guardarBaseDeDatos(Concesionario concesionario) throws DBException {
        try {
            if (concesionario == null) {
                concesionario = new Concesionario();
            }

            File archivoBD = new File(rutaOriginal);

            File carpetaPadre = archivoBD.getParentFile();

            if (carpetaPadre != null) {
                carpetaPadre.mkdirs();
            }

            JAXBContext context = JAXBContext.newInstance(Concesionario.class);
            Marshaller marshaller = context.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            marshaller.marshal(concesionario, archivoBD);

            System.out.println("Base de datos guardada correctamente en XML.");

        } catch (JAXBException e) {
            throw new DBException("Error guardando el fichero XML con JAXB: " + e.getMessage());
        }
    }
}