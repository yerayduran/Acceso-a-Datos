package com.concesionario.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
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

    /**
     * @return lista de coches del concesionario
     */
    public List<Coche> getCoches() {
        if (coches == null) {
            coches = new ArrayList<>();
        }

        return coches;
    }

    /**
     * @param coches lista de coches que contendrá el concesionario
     */
    public void setCoches(List<Coche> coches) {
        this.coches = coches == null ? new ArrayList<>() : new ArrayList<>(coches);
    }

    /**
     * @return número total de coches registrados
     */
    public int totalCoches() {
        return getCoches().size();
    }

    /**
     * Agrega un coche al concesionario.
     * Si el coche no tiene ID, genera el siguiente ID disponible.
     *
     * @param coche coche que se desea añadir
     */
    public void agregarCoche(Coche coche) {
        if (coche == null) {
            return;
        }

        if (coche.getId() == 0) {
            coche.setId(siguienteIdDisponible());
        }

        getCoches().add(coche);
    }

    /**
     * Calcula el siguiente ID disponible a partir del mayor ID existente.
     *
     * @return siguiente identificador numérico disponible
     */
    private int siguienteIdDisponible() {
        int maxId = 0;

        for (Coche coche : getCoches()) {
            if (coche != null && coche.getId() > maxId) {
                maxId = coche.getId();
            }
        }

        return maxId + 1;
    }

    /**
     * Busca un coche por su matrícula.
     *
     * @param matricula matrícula del coche
     * @return coche encontrado o null si no existe
     */
    public Coche buscarPorMatricula(String matricula) {
        if (matricula == null) {
            return null;
        }

        for (Coche coche : getCoches()) {
            if (coche != null && coche.getMatricula() != null
                    && coche.getMatricula().equalsIgnoreCase(matricula)) {
                return coche;
            }
        }

        return null;
    }

    /**
     * Ordena los coches por matrícula usando compareTo() de Coche.
     */
    public void ordenarPorMatricula() {
        if (!getCoches().isEmpty()) {
            Collections.sort(getCoches());
        }
    }

    /**
     * Elimina un coche por matrícula.
     *
     * @param matricula matrícula del coche a eliminar
     * @return true si se eliminó, false si no se encontró
     */
    public boolean eliminarPorMatricula(String matricula) {
        if (matricula == null || getCoches().isEmpty()) {
            return false;
        }

        Iterator<Coche> iterator = getCoches().iterator();

        while (iterator.hasNext()) {
            Coche coche = iterator.next();

            if (coche != null && coche.getMatricula() != null
                    && coche.getMatricula().equalsIgnoreCase(matricula)) {
                iterator.remove();
                return true;
            }
        }

        return false;
    }
}