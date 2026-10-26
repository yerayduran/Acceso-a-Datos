package com.concesonario.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "listado-coches")
public class Coche {

    @XmlElement
    private int id;

    private String matricula;
    private String marca;
    private String modelo;

    @XmlElement
    private
}
