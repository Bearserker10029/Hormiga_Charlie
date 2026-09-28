package com.example.hormiga_charlie.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tablero_celdas", schema = "hormiga_charlie")
public class TableroCelda {

    /** Una celda almacenada representa una parcela con feromona (negra). */
    @EmbeddedId
    private TableroCeldaId id;

    public TableroCelda() {}

    public TableroCelda(TableroCeldaId id) {
        this.id = id;
    }

    public Integer getPosX() {
        return id != null ? id.getPosX() : null;
    }

    public Integer getPosY() {
        return id != null ? id.getPosY() : null;
    }
}
