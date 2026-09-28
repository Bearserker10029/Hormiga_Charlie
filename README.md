# 🐜 Charlie la Hormiga (Langton's Ant)

> Aplicación web desarrollada con Spring Boot y Thymeleaf para simular el comportamiento de Charlie, una hormiga roja que deja feromonas y migas al desplazarse, como parte del Laboratorio 05 del curso Gestión de Servicios de TICs.

## 📋 Tabla de Contenidos

- [Descripción](#-descripción-del-proyecto)
- [Tecnologías](#-tecnologías-usadas)
- [Estructura](#-estructura-principal)
- [Flujo Funcional](#-flujo-funcional-implementado)
- [Reglas del Juego](#-reglas-del-juego)
- [Persistencia](#-persistencia-en-base-de-datos)
- [Optimizaciones](#-optimizaciones-de-rendimiento)
- [Estado](#-estado-frente-a-la-consigna)
- [Ejecución](#-cómo-ejecutar)

---

## 📝 Descripción del Proyecto

Simulación de la "Isla del Capricho" donde Charlie la hormiga se desplaza según las reglas de la hormiga de Langton. La aplicación permite:

✅ Configurar la zona inicial (dimensiones, posición y dirección de la hormiga)  
✅ Definir el aumento de visión para la expansión progresiva del terreno  
✅ Ejecutar la simulación por lotes de "fotos" (movimientos)  
✅ Ampliar el terreno automáticamente cuando la hormiga llega a los límites  
✅ Recuperar el avance progresivamente gracias a la persistencia en MySQL  
✅ Reiniciar el juego a la configuración inicial en cualquier momento

## 💻 Tecnologías Usadas

| Tecnología | Versión | Uso |
|-----------|---------|-----|
| Java | 25 | Lenguaje base |
| Spring Boot | 4.0.3 | Framework MVC |
| Spring MVC | Incluido | Controladores HTTP |
| Spring Data JPA | Incluido | Persistencia con Hibernate |
| Thymeleaf | Starter | Motor de plantillas |
| MySQL | 8.x | Base de datos relacional |
| Maven | Wrapper | Gestor de dependencias |
| Lombok | Latest | Anotaciones de código |
| Bootstrap | 5.x | Estilos de la interfaz |

## 📂 Estructura Principal

```
hormiga_charlie/
├── src/
│   ├── main/
│   │   ├── java/com/example/hormiga_charlie/
│   │   │   ├── HormigaCharlieApplication.java
│   │   │   ├── controller/
│   │   │   │   └── HormigaController.java
│   │   │   ├── service/
│   │   │   │   └── HormigaService.java
│   │   │   ├── model/
│   │   │   │   ├── ConfiguracionJuego.java
│   │   │   │   ├── TableroCelda.java
│   │   │   │   └── TableroCeldaId.java
│   │   │   ├── dto/
│   │   │   │   ├── CeldaDTO.java
│   │   │   │   ├── DireccionDTO.java
│   │   │   │   └── TableroDTO.java
│   │   │   └── repository/
│   │   │       ├── ConfiguracionJuegoRepository.java
│   │   │       └── TableroCeldaRepository.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/
│   │       └── templates/
│   │           └── charlie/
│   │               ├── config.html
│   │               ├── index.html
│   │               ├── _tablero.html
│   │               ├── _panel.html
│   │               └── _celda.html
│   └── test/
├── sql/
│   └── hormiga_charlie.sql
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

## 🔄 Flujo Funcional Implementado

```
┌─────────────────────────────────────────────────────────────┐
│                 FLUJO DE LA SIMULACIÓN                      │
├─────────────────────────────────────────────────────────────┤
│  1. GET /charlie/config                                     │
│     ↓ Formulario de primer nivel (datos de entrada)         │
│                                                             │
│  2. POST /charlie/config                                    │
│     ↓ Valida y persiste la configuración inicial            │
│                                                             │
│  3. GET /charlie/                                           │
│     ↓ Muestra el tablero con la hormiga                     │
│                                                             │
│  4. POST /charlie/avanzar                                   │
│     ↓ Simula N fotos en memoria y persiste el resultado     │
│                                                             │
│  5. POST /charlie/reiniciar                                 │
│     ↓ Restaura posición, dirección y terreno iniciales      │
└─────────────────────────────────────────────────────────────┘
```

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/charlie/config` | GET | Formulario de configuración |
| `/charlie/config` | POST | Guarda dimensiones, posición, dirección y aumento de visión |
| `/charlie/` | GET | Visualiza el tablero y la hormiga |
| `/charlie/avanzar` | POST | Ejecuta la cantidad de fotos indicada |
| `/charlie/reiniciar` | POST | Reinicia el juego a la configuración inicial |

## 🐜 Reglas del Juego

En cada paso (foto), Charlie actúa según la parcela donde se encuentra:

| Parcela | Acción de Charlie |
|---------|-------------------|
| **Miga** (parcela blanca) | Deja una feromona, gira 90° a la **derecha** y avanza 1 hormigómetro |
| **Feromona** (parcela negra) | Deja una miga, gira 90° a la **izquierda** y avanza 1 hormigómetro |

**Expansión del terreno:** cuando la hormiga alcanza los límites de la zona visible, el terreno se amplía en `aumento_vision` unidades (debe ser un número par mayor o igual a 2). Ejemplo: zona 5×5 con aumento 4 → 9×9.

**Persistencia del avance:** la cantidad de fotos por actualización (segundo nivel de configuración) se guarda en la base de datos, lo que permite recuperar la visualización progresivamente si se pierde la sesión.

## 🗄️ Persistencia en Base de Datos

Base de datos: **`hormiga_charlie`** (MySQL)

| Tabla | Contenido |
|-------|-----------|
| `configuracion_juego` | Configuración inicial y estado actual (posición, dirección, dimensiones, offsets, fotos por actualización) |
| `tablero_celdas` | Solo las parcelas con feromona (PK compuesta `pos_x`, `pos_y`); una parcela sin registro equivale a miga |

El uso de **offsets** (`offset_x`, `offset_y`) permite expander el terreno sin desplazar las feromonas almacenadas: la vista es una ventana deslizante sobre el mundo de la hormiga.

## ⚡ Optimizaciones de Rendimiento

- **Simulación en memoria:** las fotos se ejecutan sobre una lista en memoria; al final se persisten solo la diferencia neta (un `DELETE` masivo y un `INSERT` masivo), en lugar de una consulta por paso. Esto permite ejecutar 20,000 fotos en segundos.
- **Carga única de feromonas:** la vista consulta la base de datos una sola vez y no por cada celda del tablero.
- **Zoom automático:** el tablero se escala con CSS (`transform: scale`) para ajustarse a la pantalla sin barras de desplazamiento.

## ✅ Estado frente a la Consigna

### ✔️ Implementado

- [x] Configuración de primer nivel: dimensiones, posición inicial y dirección de la hormiga
- [x] Configuración de segundo nivel: cantidad de fotos por actualización persistida
- [x] Reglas de movimiento (giro a la derecha con miga, giro a la izquierda con feromona)
- [x] Expansión progresiva del terreno al llegar a los límites
- [x] Persistencia del avance en MySQL para recuperación progresiva
- [x] Visualización de la hormiga con símbolo de dirección (↑ ↓ ← →)
- [x] Reinicio a la configuración inicial
- [x] Validación de datos de entrada (posición fuera de zona, aumento de visión impar, fotos ≤ 0)
- [x] Simulación de 20,000 fotos con tiempos de respuesta aceptables

### 💡 Observaciones

⚠️ La expansión del tablero crece el DOM de la página; para valores muy grandes de fotos se recomienda el zoom automático incluido o migrar la visualización a `<canvas>`.

## 🚀 Cómo Ejecutar

### Requisitos Previos

- Java 25+ (o compatible con Spring Boot 4.0.3)
- Maven 3.6+ (incluido como wrapper)
- MySQL 8.x

### 1. Crear la base de datos

Ejecutar el script `sql/hormiga_charlie.sql`, que crea la base de datos `hormiga_charlie` con las tablas `configuracion_juego` y `tablero_celdas`.

### 2. Configurar conexión

En `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hormiga_charlie
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
```

### 3. Ejecutar la Aplicación

**En Windows (PowerShell):**

```powershell
.\mvnw.cmd spring-boot:run
```

**En Linux/macOS:**

```bash
chmod +x mvnw
./mvnw spring-boot:run
```

### 4. Acceder a la Aplicación

```
http://localhost:8080/charlie/config
```

**Puerto por defecto:** 8080

---

## 📚 Recursos Adicionales

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Thymeleaf Guide](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)

---

## 📄 Licencia

Este proyecto es de uso académico y educativo como parte de un laboratorio de curso universitario.
