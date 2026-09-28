package com.example.hormiga_charlie.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "configuracion_juego", schema = "hormiga_charlie")
public class ConfiguracionJuego {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "filas_iniciales", nullable = false)
    private Integer filasIniciales;

    @Column(name = "columnas_iniciales", nullable = false)
    private Integer columnasIniciales;

    @Column(name = "filas", nullable = false)
    private Integer filas;

    @Column(name = "columnas", nullable = false)
    private Integer columnas;

    @Column(name = "aumento_vision", nullable = false)
    private Integer aumentoVision;

    @Column(name = "hormiga_pos_x", nullable = false)
    private Integer hormigaPosX;

    @Column(name = "hormiga_pos_y", nullable = false)
    private Integer hormigaPosY;

    @Column(name = "hormiga_pos_x_inicial", nullable = false)
    private Integer hormigaPosXInicial;

    @Column(name = "hormiga_pos_y_inicial", nullable = false)
    private Integer hormigaPosYInicial;

    @Column(name = "direccion", nullable = false, length = 20)
    private String direccion;

    @Column(name = "direccion_inicial", nullable = false, length = 20)
    private String direccionInicial;

    @Column(name = "fotos_por_actualizacion", nullable = false)
    private Integer fotosPorActualizacion;

    @Column(name = "offset_x", nullable = false)
    private int offsetX = 0;

    @Column(name = "offset_y", nullable = false)
    private int offsetY = 0;
}
