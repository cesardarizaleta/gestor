
# Gestor de Memoria con Técnicas Avanzadas

Este proyecto es un simulador de gestión de memoria que implementa técnicas avanzadas como **compactación**, **reubicación** y **swapping**. Está desarrollado en Java utilizando JavaFX para la interfaz gráfica.

## Características Principales

- **Gestión de Memoria:** Asignación y liberación de procesos en memoria.
- **Compactación:** Reorganiza la memoria para reducir la fragmentación externa.
- **Reubicación:** Optimiza el espacio de memoria moviendo procesos.
- **Swapping:** Mueve procesos entre la memoria principal y el área de swapping (disco).
- **Visualización en Tiempo Real:** Gráficos y tablas que muestran el estado actual de la memoria.
- **Animaciones:** Transiciones suaves para visualizar cambios en la memoria.

## Requisitos

- **Java JDK 17 o superior**
- **JavaFX SDK**
- **IDE compatible con Java** (recomendado: IntelliJ IDEA)

## Instalación

1. Clona el repositorio:
   ```bash
   git clone https://github.com/tu-usuario/gestor-memoria.git
   ```
2. Abre el proyecto en tu IDE.
3. Configura el módulo de JavaFX en tu IDE.
4. Ejecuta la clase `HelloApplication`.

## Uso

### Interfaz Principal

- **Memoria Principal:** Muestra los bloques de memoria asignados y libres.
- **Área de Swapping:** Muestra los procesos que han sido movidos al disco.
- **Tabla de Procesos:** Lista todos los procesos activos en memoria.
- **Gráfico de Memoria:** Muestra la distribución de memoria en un gráfico de barras.

### Funcionalidades

1. **Agregar Proceso:**
   - Ingresa el nombre y tamaño del proceso.
   - Haz clic en "Agregar Proceso".

2. **Liberar Proceso:**
   - Ingresa el nombre del proceso.
   - Haz clic en "Liberar Proceso".

3. **Compactación:**
   - Haz clic en "Compactación" para agrupar la memoria libre.

4. **Reubicación:**
   - Haz clic en "Reubicación" para optimizar el espacio de memoria.

5. **Swapping:**
   - Selecciona un proceso en la tabla.
   - Haz clic en "Swapping" para moverlo al área de disco.

## Estructura del Proyecto

- `HelloApplication.java`: Clase principal que inicia la aplicación.
- `MemoryBlock.java`: Representa un bloque de memoria.
- `ProcessInfo.java`: Almacena información sobre los procesos.

## Ejemplos de Uso

### Agregar un Proceso
1. Ingresa "Proceso1" en el campo "Nombre Proceso".
2. Ingresa "256" en el campo "Tamaño (MB)".
3. Haz clic en "Agregar Proceso".

### Compactar Memoria
1. Haz clic en "Compactación".
2. Observa cómo los bloques libres se agrupan al final de la memoria.

### Swapping
1. Selecciona "Proceso1" en la tabla.
2. Haz clic en "Swapping".
3. Observa cómo el proceso se mueve al área de swapping.

## Contribuciones

Las contribuciones son bienvenidas. Si deseas mejorar el proyecto, sigue estos pasos:

1. Haz un fork del repositorio.
2. Crea una rama para tu feature (`git checkout -b feature/nueva-funcionalidad`).
3. Haz commit de tus cambios (`git commit -m 'Añade nueva funcionalidad'`).
4. Haz push a la rama (`git push origin feature/nueva-funcionalidad`).
5. Abre un Pull Request.

```
gestor-memoria/
├── src/
│   └── com/
│       └── cesardarizaleta/
│           └── gestor/
│               ├── HelloApplication.java
│               ├── MemoryBlock.java
│               └── ProcessInfo.java
├── README.md
├── LICENSE
└── screenshots/ (opcional)
    ├── main_interface.png
    └── compaction.png
```

