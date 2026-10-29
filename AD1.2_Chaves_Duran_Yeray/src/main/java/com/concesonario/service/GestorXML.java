package com.concesonario.service;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import com.concesonario.model.Concesionario;
import com.concesonario.exceptions.DBException;

import java.io.File; // Trabaja con rutas y archivos
import java.io.IOException;



/**
 * @GestorXML ->> clase que gestiona la lectura y escritura del archivo XML que actúa como base de datos.
 * Usa JAXB para convertir entre objetos Java y XML.
 */
public class GestorXML {

    String rutaOriginal = ConfPropierties.getOriginalDb();
    String rutaPantilla = ConfPropierties.getPantillaDb();

    //
    /**
     * Comprueba si el archivo XML principal existe.
     * Si no existe, crea un nuevo archivo vacío en la ruta configurada.
     */
    public void comprobarFicheroOriginalBb(){

        File originalBd = new File(rutaOriginal);

        if(!originalBd.exists()){
            crearRepositorioGeneral(originalBd, new File(rutaPantilla));
            System.out.println("Se ha creado la base de datos " +originalBd );

        }else {

            System.out.println("BBDD --> " + originalBd );
        }

    }

    private static void crearRepositorioGeneral(File destino, File plantilla){


        try {
            destino.getParentFile().mkdirs();
            destino.createNewFile();

        } catch (IOException e) {
            throw new RuntimeException("Error creando archivo vacío", e);
        }


    }



    // DESERIALIZACIÓN


    public Concesionario leerBaseDeDatos() throws DBException {
        try {
            // Primero nos aseguramos de que el archivo existe
            File archivoBD = new File(rutaOriginal);
            if (!archivoBD.exists()) {
                comprobarFicheroOriginalBb(); // Lo crea si no existe
            }


            // Verificar si el archivo está vacío
            if (archivoBD.length() == 0) {
                System.err.println("No existen registros para mostrar");
                return null;
            }

            // JAXB: XML → Objeto Java
            JAXBContext context = JAXBContext.newInstance(Concesionario.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            Concesionario concesionario = (Concesionario) unmarshaller.unmarshal(archivoBD);
            return concesionario;

        } catch (JAXBException e) {
            e.printStackTrace();
            throw new DBException("Error leyendo el fichero XML con JAXB");
        }
    }



    // SERIALIZACION

    public void guardarBaseDeDatos(Concesionario concesionario) throws DBException {
        try {
            File archivoBD = new File(rutaOriginal);

            JAXBContext context = JAXBContext.newInstance(Concesionario.class);
            Marshaller marshaller = context.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // Guardar el objeto en el archivo
            marshaller.marshal(concesionario, archivoBD);

            System.out.println("Base de datos guardada correctamente en XML.");

        } catch (JAXBException e) {
            e.printStackTrace();
            throw new DBException("Error guardando el fichero XML con JAXB");
        }
    }


}