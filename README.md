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
Abre el proyecto en tu IDE.

Configura el módulo de JavaFX en tu IDE.

Ejecuta la clase HelloApplication.

## Uso
Interfaz Principal
Memoria Principal: Muestra los bloques de memoria asignados y libres.

Área de Swapping: Muestra los procesos que han sido movidos al disco.

Tabla de Procesos: Lista todos los procesos activos en memoria.

Gráfico de Memoria: Muestra la distribución de memoria en un gráfico de barras.

## Funcionalidades
Agregar Proceso:

Ingresa el nombre y tamaño del proceso.

Haz clic en "Agregar Proceso".

Liberar Proceso:

Ingresa el nombre del proceso.

Haz clic en "Liberar Proceso".

### Compactación:

Haz clic en "Compactación" para agrupar la memoria libre.

### Reubicación:

Haz clic en "Reubicación" para optimizar el espacio de memoria.

### Swapping:

Selecciona un proceso en la tabla.

Haz clic en "Swapping" para moverlo al área de disco.

Estructura del Proyecto
HelloApplication.java: Clase principal que inicia la aplicación.

MemoryBlock.java: Representa un bloque de memoria.

ProcessInfo.java: Almacena información sobre los procesos.

Ejemplos de Uso
Agregar un Proceso
Ingresa "Proceso1" en el campo "Nombre Proceso".

Ingresa "256" en el campo "Tamaño (MB)".

Haz clic en "Agregar Proceso".

Compactar Memoria
Haz clic en "Compactación".

Observa cómo los bloques libres se agrupan al final de la memoria.

Swapping
Selecciona "Proceso1" en la tabla.

Haz clic en "Swapping".

Observa cómo el proceso se mueve al área de swapping.
