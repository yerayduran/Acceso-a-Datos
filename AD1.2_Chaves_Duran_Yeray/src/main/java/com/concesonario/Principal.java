package com.concesonario;

import com.concesonario.model.Coche;
import com.concesonario.model.Concesionario;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;

public class Principal {

    static void main(String[] args) {
        Concesionario concesionario = new Concesionario();

        Coche coche = new Coche("2444vvv", "ljkhdwhig", "jcdhs");
        concesionario.getCoches().add(coche);




        try {
            JAXBContext context = JAXBContext.newInstance(Concesionario.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            File archivoXML = new File("src/main/java/com/concesonario/concesionario_plantilla.xml");

            Concesionario concesionarioAXML = (Concesionario) unmarshaller.unmarshal(archivoXML);

            System.out.println("Concesionario cargado: " + concesionarioAXML.getCoches());
        }catch (JAXBException e){
            e.printStackTrace();
        }


    }
}
