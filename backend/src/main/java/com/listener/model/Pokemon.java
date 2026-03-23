package com.listener.model;

public class Pokemon {
    private String id;
    private String nombre;
    private int vida;
    private int nivel;

    public Pokemon() {}

    public Pokemon(String id, String nombre, int vida, int nivel) {
        this.id = id;
        this.nombre = nombre;
        this.vida = vida;
        this.nivel = nivel;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getVida() { return vida; }
    public void setVida(int vida) { this.vida = vida; }

    public int getNivel() { return nivel; }
    public void setNivel(int nivel) { this.nivel = nivel; }
}