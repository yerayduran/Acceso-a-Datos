package com.concesionario.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
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
        return coches == null ? new ArrayList<>() : coches;
    }

    public void setCoches(List<Coche> coches) {
        this.coches = coches == null ? new ArrayList<>() : coches;
    }
}