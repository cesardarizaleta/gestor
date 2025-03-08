package com.cesardarizaleta.gestor;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.Map;

public class GestorApp extends Application {
    private final int TOTAL_MEMORY = 1024; // 1GB
    private Map<String, Integer> processes = new HashMap<>();
    private HBox memoryContainer = new HBox(2);
    private TextField processName = new TextField();
    private TextField processSize = new TextField();
    private Label statusLabel = new Label("Ready");

    // Nuevos componentes para procesos y gráfico
    private TableView<ProcessInfo> processTable = new TableView<>();
    private BarChart<String, Number> memoryChart = new BarChart<>(new CategoryAxis(), new NumberAxis());
    private Label memorySummary = new Label();

    @Override
    public void start(Stage stage) {
        // Configurar contenedor principal
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Barra superior
        ToolBar toolbar = new ToolBar();
        toolbar.getItems().add(new Label("Memory Manager"));
        root.setTop(toolbar);

        // Contenedor principal dividido
        SplitPane mainContent = new SplitPane();
        mainContent.setDividerPositions(0.4);

        // Panel izquierdo (Memoria y formulario)
        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(10));

        // Visualización de memoria
        VBox memorySection = new VBox(10);
        memorySection.getChildren().add(new Label("Memory Visualization"));
        memoryContainer.setMinHeight(40);
        memoryContainer.setStyle("-fx-border-color: #ccc; -fx-padding: 5px;");
        memorySection.getChildren().add(memoryContainer);
        memorySection.getChildren().add(memorySummary);

        // Formulario
        GridPane form = createForm();
        leftPanel.getChildren().addAll(memorySection, form);

        // Panel derecho (Procesos y gráfico)
        VBox rightPanel = new VBox(10);
        rightPanel.setPadding(new Insets(10));

        // Configurar tabla de procesos
        setupProcessTable();
        // Configurar gráfico
        setupChart();

        rightPanel.getChildren().addAll(
                new Label("Process List"),
                processTable,
                new Label("Memory Distribution"),
                memoryChart
        );

        mainContent.getItems().addAll(leftPanel, rightPanel);
        root.setCenter(mainContent);

        // Barra de estado
        HBox statusBar = new HBox(statusLabel);
        statusBar.setPadding(new Insets(5, 10, 5, 10));
        root.setBottom(statusBar);

        // Configurar escena
        Scene scene = new Scene(root, 1000, 600);
        stage.setTitle("Memory Manager");
        stage.setScene(scene);
        stage.show();

        updateMemoryVisualization();
    }

    private GridPane createForm() {
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.addRow(0, new Label("Process Name:"), processName);
        form.addRow(1, new Label("Memory Size (MB):"), processSize);

        Button addButton = new Button("Add Process");
        addButton.setOnAction(e -> addProcess());
        Button removeButton = new Button("Remove Process");
        removeButton.setOnAction(e -> removeProcess());

        HBox buttons = new HBox(10, addButton, removeButton);
        form.add(buttons, 0, 2, 2, 1);
        return form;
    }

    private void setupProcessTable() {
        TableColumn<ProcessInfo, String> nameCol = new TableColumn<>("Process Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<ProcessInfo, Number> sizeCol = new TableColumn<>("Size (MB)");
        sizeCol.setCellValueFactory(new PropertyValueFactory<>("size"));

        processTable.getColumns().addAll(nameCol, sizeCol);
        processTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        processTable.setPrefHeight(150);
    }

    private void setupChart() {
        memoryChart.setLegendVisible(false);
        memoryChart.setAnimated(false);
        ((CategoryAxis) memoryChart.getXAxis()).setLabel("Processes");
        ((NumberAxis) memoryChart.getYAxis()).setLabel("Memory (MB)");
        memoryChart.setPrefHeight(250);
    }

    private void addProcess() {
        String name = processName.getText();
        String sizeText = processSize.getText();

        if (name.isEmpty() || sizeText.isEmpty()) {
            updateStatus("Error: Empty fields", "error");
            return;
        }

        try {
            int size = Integer.parseInt(sizeText);
            if (getUsedMemory() + size > TOTAL_MEMORY) {
                updateStatus("Error: Not enough memory", "error");
                return;
            }

            processes.put(name, size);
            updateMemoryVisualization();
            updateStatus("Process added: " + name, "success");
        } catch (NumberFormatException e) {
            updateStatus("Error: Invalid size", "error");
        }
    }

    private void removeProcess() {
        String name = processName.getText();
        if (processes.containsKey(name)) {
            processes.remove(name);
            updateMemoryVisualization();
            updateStatus("Process removed: " + name, "warning");
        } else {
            updateStatus("Error: Process not found", "error");
        }
    }

    private void updateMemoryVisualization() {
        // Actualizar visualización de memoria
        memoryContainer.getChildren().clear();
        int used = getUsedMemory();
        int free = TOTAL_MEMORY - used;

        addMemoryBlock("Used", used, "#dc3545");
        addMemoryBlock("Free", free, "#28a745");

        // Actualizar resumen de memoria
        double usedPercentage = (used * 100.0) / TOTAL_MEMORY;
        memorySummary.setText(String.format("Total Used: %d MB (%.1f%%) | Free: %d MB",
                used, usedPercentage, free));

        // Actualizar tabla de procesos
        ObservableList<ProcessInfo> processData = FXCollections.observableArrayList();
        processes.forEach((name, size) -> processData.add(new ProcessInfo(name, size)));
        processTable.setItems(processData);

        // Actualizar gráfico
        updateChart();
    }

    private void updateChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        processes.forEach((name, size) ->
                series.getData().add(new XYChart.Data<>(name, size)));

        memoryChart.getData().clear();
        memoryChart.getData().add(series);
    }

    private void addMemoryBlock(String label, int size, String color) {
        Pane block = new Pane();
        block.setPrefWidth((double) size / TOTAL_MEMORY * 700);
        block.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 3px;");

        Tooltip tooltip = new Tooltip(label + ": " + size + "MB");
        Tooltip.install(block, tooltip);

        memoryContainer.getChildren().add(block);
    }

    private int getUsedMemory() {
        return processes.values().stream().mapToInt(Integer::intValue).sum();
    }

    private void updateStatus(String message, String type) {
        statusLabel.setText(message);
        String color = switch(type) {
            case "success" -> "#28a745";
            case "warning" -> "#ffc107";
            case "error" -> "#dc3545";
            default -> "#6c757d";
        };
        statusLabel.setStyle("-fx-text-fill: " + color + ";");
    }

    public static void main(String[] args) {
        launch();
    }

    // Clase para representar los datos de la tabla
    public static class ProcessInfo {
        private final String name;
        private final Integer size;

        public ProcessInfo(String name, Integer size) {
            this.name = name;
            this.size = size;
        }

        public String getName() { return name; }
        public Integer getSize() { return size; }
    }
}