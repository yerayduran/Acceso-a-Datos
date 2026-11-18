package com.concesionario.model;

import java.util.ArrayList;
import java.util.List;

public class Coche {

    private String matricula;
    private String marca;
    private String modelo;
    private List<String> extras;
    private double precio;
    private Integer idPropietario;

    public Coche(String matricula, String marca, String modelo, List<String> extras, double precio, Integer idPropietario) {
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.extras = (extras != null) ? extras : new ArrayList<>();        this.precio = precio;
        this.idPropietario = idPropietario;
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

    public List<String> getExtras() {
        return extras;
    }

    public void setExtras(ArrayList<String> extras) {
        this.extras = extras;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public Integer getIdPropietario() {
        return idPropietario;
    }

    @Override
    public String toString() {
        return "Coche{" +
                "matrícula='" + matricula + '\'' +
                ", marca='" + marca + '\'' +
                ", modelo='" + modelo + '\'' +
                ", extras=" + extras +
                ", precio=" + precio + "€" +
                ", idPropietario=" + (idPropietario != null ? idPropietario : "En Concesionario") +
                '}';
    }
}