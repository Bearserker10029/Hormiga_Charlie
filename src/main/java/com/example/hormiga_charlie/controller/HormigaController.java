package com.example.hormiga_charlie.controller;

import com.example.hormiga_charlie.dto.CeldaDTO;
import com.example.hormiga_charlie.dto.DireccionDTO;
import com.example.hormiga_charlie.dto.TableroDTO;
import com.example.hormiga_charlie.model.ConfiguracionJuego;
import com.example.hormiga_charlie.model.TableroCeldaId;
import com.example.hormiga_charlie.service.HormigaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/charlie")
public class HormigaController {

    /** Valor por defecto de segundo nivel: fotos por actualizacion. */
    private static final int FOTOS_POR_ACTUALIZACION_DEFAULT = 10;

    private final HormigaService hormigaService;

    public HormigaController(HormigaService hormigaService) {
        this.hormigaService = hormigaService;
    }

    /** Configuracion de primer nivel: datos de entrada del problema. */
    @GetMapping("/config")
    public String configuracion(Model model) {
        Optional<ConfiguracionJuego> config = Optional.ofNullable(
                hormigaService.obtenerConfiguracionSeguro());
        config.ifPresent(c -> model.addAttribute("config", c));
        return "charlie/config";
    }

    @PostMapping("/config")
    public String guardarConfiguracion(@RequestParam String dimensionInicial,
                                       @RequestParam int aumentoVision,
                                       @RequestParam String posicionInicial,
                                       @RequestParam String direccionInicial,
                                       RedirectAttributes redirectAttributes) {
        try {
            // Formato de entrada: "filas,columnas" y "posX,posY" (ej. "5,5" y "2,2")
            String[] dimension = dimensionInicial.split(",");
            String[] posicion = posicionInicial.split(",");
            if (dimension.length != 2 || posicion.length != 2) {
                throw new IllegalArgumentException(
                        "Formato invalido. Use filas,columnas y posX,posY (ej. 5,5 y 2,2)");
            }
            int filas = Integer.parseInt(dimension[0].trim());
            int columnas = Integer.parseInt(dimension[1].trim());
            int posX = Integer.parseInt(posicion[0].trim());
            int posY = Integer.parseInt(posicion[1].trim());
            DireccionDTO direccion = DireccionDTO.fromCodigo(direccionInicial);

            hormigaService.inicializar(filas, columnas, aumentoVision, posX, posY,
                    direccion, FOTOS_POR_ACTUALIZACION_DEFAULT);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/charlie/config";
        }
        return "redirect:/charlie/";
    }

    @GetMapping({"/", ""})
    public String juego(Model model) {
        ConfiguracionJuego config = hormigaService.obtenerConfiguracionSeguro();
        if (config == null) {
            return "redirect:/charlie/config";
        }
        cargarEstado(model, config);
        return "charlie/index";
    }

    @PostMapping("/avanzar")
    public String avanzar(@RequestParam int fotosPorActualizacion, Model model) {
        ConfiguracionJuego config = hormigaService.avanzar(fotosPorActualizacion);
        cargarEstado(model, config);
        return "charlie/index";
    }

    @PostMapping("/reiniciar")
    public String reiniciar(Model model) {
        ConfiguracionJuego config = hormigaService.reiniciar();
        cargarEstado(model, config);
        return "charlie/index";
    }

    private void cargarEstado(Model model, ConfiguracionJuego config) {
        // Se cargan las feromonas una sola vez para no hacer una consulta por celda
        List<TableroCeldaId> feromonas = hormigaService.obtenerFeromonas();

        Map<String, CeldaDTO> celdasByPos = new HashMap<>();
        for (int y = 1; y <= config.getFilas(); y++) {
            for (int x = 1; x <= config.getColumnas(); x++) {
                String key = x + "-" + y;
                celdasByPos.put(key, hormigaService.construirCelda(x, y, config, feromonas));
            }
        }

        DireccionDTO dir = DireccionDTO.valueOf(config.getDireccion());
        TableroDTO tableroDTO = new TableroDTO(
                config.getFilas(),
                config.getColumnas(),
                config.getHormigaPosX(),
                config.getHormigaPosY(),
                config.getDireccion(),
                dir.getSimbolo(),
                config.getAumentoVision(),
                config.getFotosPorActualizacion()
        );

        double zoom = Math.min(1.0, 700.0 / (config.getFilas() * 12.0));
        model.addAttribute("zoom", zoom);

        model.addAttribute("tableroDTO", tableroDTO);
        model.addAttribute("celdasByPos", celdasByPos);
    }
}