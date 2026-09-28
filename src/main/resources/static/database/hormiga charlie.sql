-- =============================================================
-- LA HORMIGA CHARLIE
-- Base de datos MySQL
-- =============================================================
CREATE DATABASE IF NOT EXISTS hormiga_charlie
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE hormiga_charlie;

-- -------------------------------------------------------------
-- Configuracion de primer y segundo nivel del juego.
-- Guarda el avance (movimientos_realizados) para poder recuperar progresivamente la visualizacion si se pierde la sesion.
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS configuracion_juego (
	id                      INT          NOT NULL PRIMARY KEY,
	filas_iniciales         INT          NOT NULL,
	columnas_iniciales      INT          NOT NULL,
	filas                   INT          NOT NULL,
	columnas                INT          NOT NULL,
	aumento_vision          INT          NOT NULL,
	hormiga_pos_x           INT          NOT NULL,
	hormiga_pos_y           INT          NOT NULL,
	hormiga_pos_x_inicial   INT          NOT NULL,
	hormiga_pos_y_inicial   INT          NOT NULL,
	direccion               VARCHAR(20)  NOT NULL,
	direccion_inicial       VARCHAR(20)  NOT NULL,
	fotos_por_actualizacion INT          NOT NULL,
	offset_x INT NOT NULL DEFAULT 0,
	offset_y INT NOT NULL DEFAULT 0
    );

-- -------------------------------------------------------------
-- Solo se almacenan las parcelas con feromona (parcelas negras).
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tablero_celdas (
	pos_x INT NOT NULL,
	pos_y INT NOT NULL,
	PRIMARY KEY (pos_x, pos_y)
    );