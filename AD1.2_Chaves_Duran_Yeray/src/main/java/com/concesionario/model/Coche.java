package com.concesionario.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "coche")
@XmlAccessorType(XmlAccessType.FIELD)
public class Coche implements Comparable<Coche>{

    @XmlAttribute(name = "id")
    private int id;

    @XmlElement(name = "matricula-coche")
    private String matricula;

    @XmlElement(name = "marca")
    private String marca;

    @XmlElement(name = "modelo")
    private String modelo;

    @XmlElementWrapper(name = "equipamiento")
    @XmlElement(name = "extra")
    private List<String> equipamiento;

    /**
     * Constructor requerido por JAXB y Jackson.
     */
    public Coche() {
        this.equipamiento = new ArrayList<>();
    }

    /**
     * Crea un coche sin equipamiento.
     *
     * @param matricula matrícula del coche
     * @param marca marca del coche
     * @param modelo modelo del coche
     */
    public Coche(String matricula, String marca, String modelo) {
        this();
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
    }

    /**
     * Crea un coche con su equipamiento.
     *
     * @param matricula matrícula del coche
     * @param marca marca del coche
     * @param modelo modelo del coche
     * @param equipamiento extras del coche
     */
    public Coche(String matricula, String marca, String modelo, List<String> equipamiento) {
        this(matricula, marca, modelo);
        this.equipamiento = equipamiento == null ? new ArrayList<>() : new ArrayList<>(equipamiento);
    }

    /** @return identificador numérico del registro */
    public int getId() {
        return id;
    }

    /** @param id identificador numérico del registro */
    public void setId(int id) {
        this.id = id;
    }

    /** @return matrícula del coche */
    public String getMatricula() {
        return matricula;
    }

    /** @param matricula matrícula del coche */
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    /** @return marca del coche */
    public String getMarca() {
        return marca;
    }

    /** @param marca marca del coche */
    public void setMarca(String marca) {
        this.marca = marca;
    }

    /** @return modelo del coche */
    public String getModelo() {
        return modelo;
    }

    /** @param modelo modelo del coche */
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    /** @return lista de extras del coche */
    public List<String> getEquipamiento() {
        return equipamiento == null ? new ArrayList<>() : equipamiento;
    }

    /** @param equipamiento lista de extras del coche */
    public void setEquipamiento(List<String> equipamiento) {
        this.equipamiento = equipamiento == null ? new ArrayList<>() : equipamiento;
    }

    /**
     * Añade un extra no vacío al equipamiento.
     *
     * @param extra extra que se añade
     */
    public void añadirExtra(String extra) {
        if (this.equipamiento == null) {
            this.equipamiento = new ArrayList<>();
        }
        if (extra != null && !extra.isBlank()) {
            this.equipamiento.add(extra.trim());
        }
    }

    /** @return representación legible del coche */
    @Override
    public String toString() {
        return "Coche{" +
                "id=" + id +
                ", matricula='" + matricula + '\'' +
                ", marca='" + marca + '\'' +
                ", modelo='" + modelo + '\'' +
                ", equipamiento=" + equipamiento +
                '}';
    }

    @Override
    public int compareTo(Coche otroCoche) {
        return this.matricula.compareTo(otroCoche.matricula);
    }
}