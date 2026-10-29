package com.concesonario.model;

import jakarta.xml.bind.annotation.*;

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

    public Concesionario() {
        this.coches = new ArrayList<>();
    }

    public List<Coche> getCoches() {
        return coches;
    }
    public void setCoches(List<Coche> coches) {
        this.coches = coches;
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
            if (coche.getId() == 0) {
                int maxId = 0;
                for (Coche c : coches) {
                    if (c.getId() > maxId) {
                        maxId = c.getId();
                    }
                }
                coche.setId(maxId + 1);
            }
            coches.add(coche);
        }
    }


    public Coche buscarPorMatricula(String matricula) {
        for (Coche coche : coches) {
            if (coche.getMatricula().equals(matricula)) {
                return coche;
            }
        }
        return null;
    }


    public void ordenarPorMatricula() {
        if (coches != null && !coches.isEmpty()) {
            Collections.sort(coches);

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

