package com.example.hormiga_charlie.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TableroDTO {
    private int filas;
    private int columnas;
    private int hormigaPosX;
    private int hormigaPosY;
    private String direccion;
    private String simboloDireccion;
    private int aumentoVision;
    private int fotosPorActualizacion;

    public TableroDTO() {}

    public TableroDTO(int filas, int columnas, int hormigaPosX, int hormigaPosY,
                      String direccion, String simboloDireccion, int aumentoVision, int fotosPorActualizacion) {
        this.filas = filas;
        this.columnas = columnas;
        this.hormigaPosX = hormigaPosX;
        this.hormigaPosY = hormigaPosY;
        this.direccion = direccion;
        this.simboloDireccion = simboloDireccion;
        this.aumentoVision = aumentoVision;
        this.fotosPorActualizacion = fotosPorActualizacion;
    }
}
