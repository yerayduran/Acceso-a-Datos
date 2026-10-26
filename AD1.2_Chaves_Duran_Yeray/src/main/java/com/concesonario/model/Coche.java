package com.concesonario.model;

import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "coche")
@XmlAccessorType(XmlAccessType.FIELD)
public class Coche implements Comparable<Coche> {

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


    public Coche() {
        this.equipamiento = new ArrayList<>();
    }

    // Constructor para CSV
    public Coche(String matricula, String marca, String modelo) {
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
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
        return equipamiento;
    }

    public void setEquipamiento(List<String> equipamiento) {
        this.equipamiento = equipamiento;
    }

    /**
     * Compara dos coches según su matrícula
     */
    @Override
    public int compareTo(Coche otroCoche) {
        return this.matricula.compareTo(otroCoche.matricula);
    }
}
