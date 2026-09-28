package com.example.hormiga_charlie.service;

import com.example.hormiga_charlie.dto.CeldaDTO;
import com.example.hormiga_charlie.dto.DireccionDTO;
import com.example.hormiga_charlie.model.ConfiguracionJuego;
import com.example.hormiga_charlie.model.TableroCelda;
import com.example.hormiga_charlie.model.TableroCeldaId;
import com.example.hormiga_charlie.repository.ConfiguracionJuegoRepository;
import com.example.hormiga_charlie.repository.TableroCeldaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HormigaService {

    private static final int CONFIG_ID = 1;

    private final ConfiguracionJuegoRepository configuracionJuegoRepository;
    private final TableroCeldaRepository tableroCeldaRepository;

    public HormigaService(ConfiguracionJuegoRepository configuracionJuegoRepository,
                          TableroCeldaRepository tableroCeldaRepository) {
        this.configuracionJuegoRepository = configuracionJuegoRepository;
        this.tableroCeldaRepository = tableroCeldaRepository;
    }

    /** Configuracion de primer nivel: crea/reinicia el juego con los datos de entrada. */
    @Transactional
    public ConfiguracionJuego inicializar(int filas, int columnas, int aumentoVision, int posX, int posY, DireccionDTO direccion,
                                          int fotosPorActualizacion) {
        if (posX < 1 || posX > columnas || posY < 1 || posY > filas) {
            throw new IllegalArgumentException(
                    "La posicion inicial (" + posX + "," + posY + ") esta fuera de la zona " + columnas + "x" + filas);
        }
        if (aumentoVision < 2 || aumentoVision % 2 != 0) {
            throw new IllegalArgumentException(
                    "El aumento de vision debe ser un numero par mayor o igual a 2");
        }
        if (fotosPorActualizacion < 1) {
            throw new IllegalArgumentException("Las cantidades de fotos deben ser mayores a 0");
        }

        // Toda parcela comienza con miga: se limpian las feromonas previas
        tableroCeldaRepository.deleteAll();

        ConfiguracionJuego config = configuracionJuegoRepository.findById(CONFIG_ID)
                .orElseGet(ConfiguracionJuego::new);
        config.setId(CONFIG_ID);
        config.setFilasIniciales(filas);
        config.setColumnasIniciales(columnas);
        config.setFilas(filas);
        config.setColumnas(columnas);
        config.setAumentoVision(aumentoVision);
        config.setHormigaPosX(posX);
        config.setHormigaPosY(posY);
        config.setHormigaPosXInicial(posX);
        config.setHormigaPosYInicial(posY);
        config.setDireccion(direccion.name());
        config.setDireccionInicial(direccion.name());
        config.setFotosPorActualizacion(fotosPorActualizacion);
        config.setOffsetX(0);
        config.setOffsetY(0);

        return configuracionJuegoRepository.save(config);
    }

    /** Avanza la cantidad de fotos indicada en el panel. */
    @Transactional
    public ConfiguracionJuego avanzar(int pasos) {
        if (pasos < 1) {
            throw new IllegalArgumentException("Las cantidades de fotos deben ser mayores a 0");
        }
        ConfiguracionJuego config = obtenerConfiguracion();

        // Se guarda la cantidad elegida en el panel para mantenerla en la vista
        config.setFotosPorActualizacion(pasos);

        List<TableroCeldaId> estado = tableroCeldaRepository.findAll().stream()
                .map(TableroCelda::getId)
                .collect(Collectors.toList());

        List<TableroCeldaId> estadoInicial = new ArrayList<>(estado);

        DireccionDTO direccion = DireccionDTO.valueOf(config.getDireccion());
        int x = config.getHormigaPosX();
        int y = config.getHormigaPosY();

        for (int i = 0; i < pasos; i++) {
            TableroCeldaId actual = new TableroCeldaId(x, y);
            if (estado.contains(actual)) {
                // Hay feromona: deja miga y gira a la izquierda
                estado.remove(actual);
                direccion = direccion.girarIzquierda();
            } else {
                // Hay miga: deja feromona y gira a la derecha
                estado.add(actual);
                direccion = direccion.girarDerecha();
            }

            x += direccion.getDx();
            y += direccion.getDy();

            int vistaX = x - config.getOffsetX();
            int vistaY = y - config.getOffsetY();
            if (vistaX < 1 || vistaX > config.getColumnas() || vistaY < 1 || vistaY > config.getFilas()) {
                expandirTerreno(config);
            }
        }

        config.setDireccion(direccion.name());
        config.setHormigaPosX(x);
        config.setHormigaPosY(y);

        List<TableroCeldaId> aInsertar = new ArrayList<>();
        for (TableroCeldaId id : estado) {
            if (!estadoInicial.contains(id)) {
                aInsertar.add(id);
            }
        }

        List<TableroCeldaId> aEliminar = new ArrayList<>();
        for (TableroCeldaId id : estadoInicial) {
            if (!estado.contains(id)) {
                aEliminar.add(id);
            }
        }

        tableroCeldaRepository.deleteAllById(aEliminar);
        tableroCeldaRepository.saveAll(
                aInsertar.stream().map(TableroCelda::new).collect(Collectors.toList()));

        return configuracionJuegoRepository.save(config);
    }

    /** Reinicia el juego a partir de los valores iniciales guardados en la configuracion. */
    @Transactional
    public ConfiguracionJuego reiniciar() {
        ConfiguracionJuego config = obtenerConfiguracion();

        tableroCeldaRepository.deleteAll();

        config.setFilas(config.getFilasIniciales());
        config.setColumnas(config.getColumnasIniciales());
        config.setHormigaPosX(config.getHormigaPosXInicial());
        config.setHormigaPosY(config.getHormigaPosYInicial());
        config.setDireccion(config.getDireccionInicial());
        config.setOffsetX(0);
        config.setOffsetY(0);

        return configuracionJuegoRepository.save(config);
    }

    public ConfiguracionJuego obtenerConfiguracionSeguro() {
        return configuracionJuegoRepository.findById(CONFIG_ID).orElse(null);
    }

    public ConfiguracionJuego obtenerConfiguracion() {
        return configuracionJuegoRepository.findById(CONFIG_ID)
                .orElseThrow(() -> new IllegalStateException("No se encontro configuracion del juego"));
    }

    private void darPaso(ConfiguracionJuego config) {
        int worldX = config.getHormigaPosX();
        int worldY = config.getHormigaPosY();

        TableroCeldaId idActual = new TableroCeldaId(worldX, worldY);
        boolean tieneFeromona = tableroCeldaRepository.existsById(idActual);

        DireccionDTO direccion = DireccionDTO.valueOf(config.getDireccion());

        if (tieneFeromona) {
            // Hay feromona: deja miga, gira 90 grados a la izquierda y avanza
            tableroCeldaRepository.deleteById(idActual);
            direccion = direccion.girarIzquierda();
        } else {
            // Hay miga: deja feromona, gira 90 grados a la derecha y avanza
            tableroCeldaRepository.save(new TableroCelda(idActual));
            direccion = direccion.girarDerecha();
        }

        config.setDireccion(direccion.name());

        int nuevaWorldX = worldX + direccion.getDx();
        int nuevaWorldY = worldY + direccion.getDy();

        int vistaX = nuevaWorldX - config.getOffsetX();
        int vistaY = nuevaWorldY - config.getOffsetY();
        if (vistaX < 1 || vistaX > config.getColumnas() || vistaY < 1 || vistaY > config.getFilas()) {
            expandirTerreno(config);
        }

        config.setHormigaPosX(nuevaWorldX);
        config.setHormigaPosY(nuevaWorldY);
    }

    private void expandirTerreno(ConfiguracionJuego config) {
        int mitad = config.getAumentoVision() / 2;

        config.setOffsetX(config.getOffsetX() - mitad);
        config.setOffsetY(config.getOffsetY() - mitad);
        config.setColumnas(config.getColumnas() + config.getAumentoVision());
        config.setFilas(config.getFilas() + config.getAumentoVision());
    }

    public List<TableroCeldaId> obtenerFeromonas() {
        return tableroCeldaRepository.findAll().stream()
                .map(TableroCelda::getId)
                .collect(Collectors.toList());
    }

    public CeldaDTO construirCelda(int x, int y, ConfiguracionJuego config, List<TableroCeldaId> feromonas) {
        int worldX = x + config.getOffsetX();
        int worldY = y + config.getOffsetY();
        boolean esHormiga = worldX == config.getHormigaPosX() && worldY == config.getHormigaPosY();
        boolean esFeromona = feromonas.contains(new TableroCeldaId(worldX, worldY));
        DireccionDTO dir = DireccionDTO.valueOf(config.getDireccion());
        return new CeldaDTO(x, y, esFeromona, esHormiga, esHormiga ? dir.getSimbolo() : "");
    }
}