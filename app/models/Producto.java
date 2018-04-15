package models;

import java.io.File;
import java.util.ArrayList;

public class Producto {

    public String nombre;
    public Double precio;
    public String url;

    public Producto(String nombre, Double precio, String url) {
        this.nombre = nombre;
        this.precio = precio;
        this.url = url;
    }


    public File getImageFile() {
       return new File(this.url);
    }

}

