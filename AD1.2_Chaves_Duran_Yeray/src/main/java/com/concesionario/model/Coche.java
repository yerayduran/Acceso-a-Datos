package com.concesionario.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Coche implements Comparable<Coche> {

    @XmlAttribute(name = "id")
    private int id;

    @JsonProperty("matricula-coche")
    @XmlElement(name = "matricula-coche")
    private String matricula;

    @XmlElement(name = "marca")
    private String marca;

    @XmlElement(name = "modelo")
    private String modelo;

    @JsonProperty("extras")
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
     */
    public Coche(String matricula, String marca, String modelo) {
        this();
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
    }

    /**
     * Crea un coche con su equipamiento.
     */
    public Coche(String matricula, String marca, String modelo, List<String> equipamiento) {
        this(matricula, marca, modelo);
        this.equipamiento = equipamiento == null ? new ArrayList<>() : new ArrayList<>(equipamiento);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public List<String> getEquipamiento() {
        return equipamiento == null ? new ArrayList<>() : equipamiento;
    }

    public void setEquipamiento(List<String> equipamiento) {
        this.equipamiento = equipamiento == null ? new ArrayList<>() : equipamiento;
    }

    public void añadirExtra(String extra) {
        if (this.equipamiento == null) {
            this.equipamiento = new ArrayList<>();
        }

        if (extra != null && !extra.isBlank()) {
            this.equipamiento.add(extra.trim());
        }
    }

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
        if (this.matricula == null && otroCoche.matricula == null) return 0;
        if (this.matricula == null) return -1;
        if (otroCoche.matricula == null) return 1;
        return this.matricula.compareTo(otroCoche.matricula);
    }
}