package com.example.hormiga_charlie.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CeldaDTO {
    private int posX;
    private int posY;
    private boolean esFeromona;
    private boolean esHormiga;
    private String simboloDireccion;

    public CeldaDTO() {}

    public CeldaDTO(int posX, int posY, boolean esFeromona, boolean esHormiga, String simboloDireccion) {
        this.posX = posX;
        this.posY = posY;
        this.esFeromona = esFeromona;
        this.esHormiga = esHormiga;
        this.simboloDireccion = simboloDireccion;
    }
}
