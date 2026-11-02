package com.concesionario.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@XmlRootElement(name = "concesionario-de-coches")
@XmlAccessorType(XmlAccessType.FIELD)
public class Concesionario {

    @XmlElementWrapper(name = "listado-coches")
    @XmlElement(name = "coche")
    private List<Coche> coches;

    /**
     * Constructor requerido por JAXB y Jackson.
     */
    public Concesionario() {
        this.coches = new ArrayList<>();
    }

    /** @return coches que contiene el concesionario */
    public List<Coche> getCoches() {
        return coches == null ? new ArrayList<>() : coches;
    }

    /** @param coches coches que contendrá el concesionario */
    public void setCoches(List<Coche> coches) {
        this.coches = coches == null ? new ArrayList<>() : coches;
    }

    public int totalCoches() {
        return coches.size();
    }


    /**
     * @agregarCoche
     * Agrega un nuevo coche al concesionario.
     * Si el coche no tiene un identificador asignado (ID = 0),
     * se genera automáticamente un nuevo ID basado en el valor máximo
     * de los IDs existentes en la lista actual de coches, asegurando
     */
    public void agregarCoche(Coche coche) {
        if (coche != null) {
            // Si no tiene ID, se genera uno nuevo basado en el mayor ID actual
            if (coche.getId() == 0) {
                int maxId = 0;
                // Recorremos la lista para encontrar el ID más alto
                for (Coche c : coches) {
                    if (c.getId() > maxId) {
                        maxId = c.getId();
                    }
                }
                // Asignamos el siguiente ID disponible
                coche.setId(maxId + 1);
            }
            coches.add(coche);
        }
    }


    // =================================== MÉT-DOS AUXILIAR ->> PARA EVITAR DUPLICADOS -> @coches
    public Coche buscarPorMatricula(String matricula) {
        for (Coche coche : coches) {
            if (coche.getMatricula().equals(matricula)) {
                return coche;
            }
        }
        return null;
    }


    // =================================== MÉT-DOS AUXILIAR ->> PARA ORDENAR -> @coches
    public void ordenarPorMatricula() {
        if (coches != null && !coches.isEmpty()) {
            Collections.sort(coches); // usa compareTo() de @Coche
            //inversa -> (coches, Comparator.reverseOrder());
        }
    }


    public boolean eliminarPorMatricula(String matricula) {
        if (coches == null || coches.isEmpty()) return false;

        Iterator<Coche> iterator = coches.iterator();
        while (iterator.hasNext()) {
            Coche coche = iterator.next();
            if (coche.getMatricula().equalsIgnoreCase(matricula)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }
}