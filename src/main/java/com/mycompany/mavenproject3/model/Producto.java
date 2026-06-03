package com.mycompany.mavenproject3.model;

public class Producto {
    private String nombre;
    private double precio;
    private int cantidad;

    public Producto(String nombre, double precio, int cantidad) {
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    // Getters para obtener la información
    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    // Setters para actualizar la información (necesarios para tu método PUT)
    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    // Convertimos el objeto a formato JSON
    @Override
    public String toString() {
        return "{\"nombre\":\"" + nombre + "\",\"precio\":" + precio + ",\"cantidad\":" + cantidad + "}";
    }

    // --- Nuevos atributos y métodos para el uso con la Base de Datos (DAO) ---
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}