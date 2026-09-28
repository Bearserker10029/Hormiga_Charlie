package com.example.hormiga_charlie.dto;

import lombok.Getter;

@Getter
public enum DireccionDTO {
    ARRIBA(0, -1, "U", "↑"),
    ABAJO(0, 1, "D", "↓"),
    IZQUIERDA(-1, 0, "L", "←"),
    DERECHA(1, 0, "R", "→");

    private final int dx;
    private final int dy;
    private final String codigo;
    private final String simbolo;

    DireccionDTO(int dx, int dy, String codigo, String simbolo) {
        this.dx = dx;
        this.dy = dy;
        this.codigo = codigo;
        this.simbolo = simbolo;
    }

    /** Convierte el codigo de entrada (R, L, U, D) al enum correspondiente. */
    public static DireccionDTO fromCodigo(String codigo) {
        for (DireccionDTO d : values()) {
            if (d.codigo.equalsIgnoreCase(codigo.trim())) {
                return d;
            }
        }
        throw new IllegalArgumentException("Direccion invalida: " + codigo);
    }

    public DireccionDTO girarDerecha() {
        return switch (this) {
            case ARRIBA -> DERECHA;
            case DERECHA -> ABAJO;
            case ABAJO -> IZQUIERDA;
            default -> ARRIBA;
        };
    }

    public DireccionDTO girarIzquierda() {
        return switch (this) {
            case ARRIBA -> IZQUIERDA;
            case IZQUIERDA -> ABAJO;
            case ABAJO -> DERECHA;
            default -> ARRIBA;
        };
    }
}