package com.concesionario.model;

import java.util.ArrayList;
import java.util.List;

public class Coche implements Comparable<Coche>{

    private int id;
    private String matricula;
    private String marca;
    private String modelo;
    private List<String> extras;
    private double precio;

    public Coche(int id, String matricula, String marca, String modelo, List<String> extras, double precio) {
        this.id = id;
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.extras = new ArrayList<>();
        this.precio = precio;
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

    public List<String> getExtras() {
        return extras;
    }

    public void setExtras(List<String> extras) {
        this.extras = extras;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    @Override
    public int compareTo(Coche o) {
        return Double.compare(this.precio, o.precio);
    }

    @Override
    public String toString() {
        return "Coche{" +
                "id=" + id +
                ", matricula='" + matricula + '\'' +
                ", marca='" + marca + '\'' +
                ", modelo='" + modelo + '\'' +
                ", extras=" + extras +
                ", precio=" + precio +
                '}';
    }
}
