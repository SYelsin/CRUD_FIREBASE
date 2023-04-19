package com.example.crud_firebase;

public class DataClass {
    private String id;
    private String nombre;
    private String apellido;
    private String fechan;
    private String dataImage;
    private String key;
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getFechan() {
        return fechan;
    }

    public String getDataImage() {
        return dataImage;
    }

    public DataClass(String id, String nombre, String apellido, String fechan, String dataImage) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechan = fechan;
        this.dataImage = dataImage;
    }

    public DataClass(){
    }
}