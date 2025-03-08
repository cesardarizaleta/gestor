package com.cesardarizaleta.gestor;

import javafx.animation.*;
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
import javafx.util.Duration;
import java.util.*;

public class AdvancedGestorApp extends Application {
    private final int TOTAL_MEMORY = 1024;
    private List<MemoryBlock> memoryBlocks = new ArrayList<>();
    private HBox memoryVisualization = new HBox(2);
    private TextField processName = new TextField();
    private TextField processSize = new TextField();
    private Label statusLabel = new Label("Ready");
    private TableView<ProcessInfo> processTable = new TableView<>();
    private BarChart<String, Number> memoryChart = new BarChart<>(new CategoryAxis(), new NumberAxis());
    private Label memorySummary = new Label();
    private ListView<String> swapList = new ListView<>();

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #f0f2f5;");

        initializeMemory();

        ToolBar toolbar = createToolBar();
        root.setTop(toolbar);

        SplitPane mainContent = new SplitPane();
        mainContent.setDividerPositions(0.4);

        VBox leftPanel = createLeftPanel();
        VBox rightPanel = createRightPanel();

        mainContent.getItems().addAll(leftPanel, rightPanel);
        root.setCenter(mainContent);

        root.setBottom(createStatusBar());

        Scene scene = new Scene(root, 1200, 700);
        stage.setTitle("Advanced Memory Manager");
        stage.setScene(scene);
        stage.show();
    }

    private void initializeMemory() {
        memoryBlocks.add(new MemoryBlock("Free", TOTAL_MEMORY, "#28a745"));
    }

    private ToolBar createToolBar() {
        ToolBar toolbar = new ToolBar();
        Button btnCompact = createManagementButton("Compactación", this::compactMemory);
        Button btnSwap = createManagementButton("Swapping", this::handleSwapping);
        Button btnRelocate = createManagementButton("Reubicación", this::relocateProcess);

        toolbar.getItems().addAll(
                new Label("Memory Manager - Técnicas de Gestión "),
                new Separator(),
                btnCompact,
                btnRelocate,
                btnSwap
        );
        return toolbar;
    }

    private Button createManagementButton(String text, Runnable action) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: #4a90e2; -fx-text-fill: white;");
        btn.setOnAction(e -> action.run());
        return btn;
    }

    private VBox createLeftPanel() {
        VBox leftPanel = new VBox(15);
        leftPanel.setPadding(new Insets(15));

        VBox memorySection = new VBox(10);
        memorySection.getChildren().add(new Label("Espacio de Memoria Principal"));
        memoryVisualization.setMinHeight(60);
        memoryVisualization.setStyle("-fx-border-color: #ccc; -fx-background-color: #fff;");
        updateMemoryDisplay();
        memorySection.getChildren().addAll(memoryVisualization, memorySummary);

        VBox swapSection = new VBox(10);
        swapSection.getChildren().add(new Label("Área de Swapping (Disco)"));
        swapList.setPrefHeight(100);
        swapSection.getChildren().add(swapList);

        leftPanel.getChildren().addAll(memorySection, createForm(), swapSection);
        return leftPanel;
    }

    private GridPane createForm() {
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.addRow(0, new Label("Nombre Proceso:"), processName);
        form.addRow(1, new Label("Tamaño (MB):"), processSize);

        Button addBtn = createActionButton("Agregar Proceso", "#28a745", this::addProcess);
        Button removeBtn = createActionButton("Liberar Proceso", "#dc3545", this::removeProcess);

        form.add(new HBox(10, addBtn, removeBtn), 0, 2, 2, 1);
        return form;
    }

    private Button createActionButton(String text, String color, Runnable action) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white;");
        btn.setOnAction(e -> action.run());
        return btn;
    }

    private VBox createRightPanel() {
        VBox rightPanel = new VBox(15);
        rightPanel.setPadding(new Insets(15));

        setupProcessTable();
        setupChart();

        rightPanel.getChildren().addAll(
                new Label("Procesos Activos"),
                processTable,
                new Label("Distribución de Memoria"),
                memoryChart
        );
        return rightPanel;
    }

    private void setupProcessTable() {
        TableColumn<ProcessInfo, String> nameCol = new TableColumn<>("Proceso");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<ProcessInfo, Number> sizeCol = new TableColumn<>("Tamaño");
        sizeCol.setCellValueFactory(new PropertyValueFactory<>("size"));

        processTable.getColumns().addAll(nameCol, sizeCol);
        processTable.setPrefHeight(200);
    }

    private void setupChart() {
        memoryChart.setLegendVisible(false);
        ((CategoryAxis) memoryChart.getXAxis()).setLabel("Procesos");
        ((NumberAxis) memoryChart.getYAxis()).setLabel("MB");
        memoryChart.setPrefHeight(250);
    }

    private HBox createStatusBar() {
        HBox statusBar = new HBox(statusLabel);
        statusBar.setStyle("-fx-background-color: #e9ecef;");
        statusBar.setPadding(new Insets(5, 15, 5, 15));
        return statusBar;
    }

    private void addProcess() {
        String name = processName.getText();
        String sizeText = processSize.getText();

        if (name.isEmpty() || sizeText.isEmpty()) {
            showStatus("Error: Campos vacíos", "error");
            return;
        }

        try {
            int size = Integer.parseInt(sizeText);
            if (!allocateMemory(name, size)) {
                showStatus("No hay espacio suficiente - Considere compactación/swapping", "warning");
            } else {
                showStatus("Proceso agregado: " + name, "success");
                updateVisuals();
            }
        } catch (NumberFormatException e) {
            showStatus("Error: Tamaño inválido", "error");
        }
    }

    private boolean allocateMemory(String name, int size) {
        for (int i = 0; i < memoryBlocks.size(); i++) {
            MemoryBlock block = memoryBlocks.get(i);
            if (block.isFree() && block.getSize() >= size) {
                if (block.getSize() > size) {
                    MemoryBlock newBlock = new MemoryBlock("Free", block.getSize() - size, "#28a745");
                    memoryBlocks.add(i + 1, newBlock);
                }
                block.setName(name);
                block.setColor("#dc3545");
                block.setSize(size);
                return true;
            }
        }
        return false;
    }

    private void removeProcess() {
        String name = processName.getText();
        boolean removed = memoryBlocks.removeIf(block ->
                !block.isFree() && block.getName().equals(name));

        if (removed) {
            mergeFreeBlocks();
            updateVisuals();
            showStatus("Proceso liberado: " + name, "success");
        } else {
            showStatus("Proceso no encontrado", "error");
        }
    }

    private void compactMemory() {
        int totalUsed = memoryBlocks.stream()
                .filter(b -> !b.isFree())
                .mapToInt(MemoryBlock::getSize)
                .sum();

        int freeSize = TOTAL_MEMORY - totalUsed;

        List<MemoryBlock> newBlocks = new ArrayList<>();
        newBlocks.addAll(memoryBlocks.stream()
                .filter(b -> !b.isFree())
                .toList());
        newBlocks.add(new MemoryBlock("Free", freeSize, "#28a745"));

        animateMemoryTransition(memoryBlocks, newBlocks);
        memoryBlocks = newBlocks;
        showStatus("Memoria compactada", "success");
        updateVisuals();
    }

    private void handleSwapping() {
        String selected = processTable.getSelectionModel().getSelectedItem().getName();
        if (selected != null) {
            swapProcess(selected);
        }
    }

    private void swapProcess(String processName) {
        Optional<MemoryBlock> processBlock = memoryBlocks.stream()
                .filter(b -> !b.isFree() && b.getName().equals(processName))
                .findFirst();

        if (processBlock.isPresent()) {
            swapList.getItems().add(processName + " (" + processBlock.get().getSize() + "MB)");
            memoryBlocks.remove(processBlock.get());
            mergeFreeBlocks();
            updateVisuals();
            showStatus("Proceso en swapping: " + processName, "warning");
        }
    }

    private void relocateProcess() {
        List<MemoryBlock> current = new ArrayList<>(memoryBlocks);
        List<MemoryBlock> optimized = optimizeMemoryLayout(current);

        if (!current.equals(optimized)) {
            animateMemoryTransition(current, optimized);
            memoryBlocks = optimized;
            showStatus("Procesos reubicados", "success");
            updateVisuals();
        }
    }

    private List<MemoryBlock> optimizeMemoryLayout(List<MemoryBlock> blocks) {
        List<MemoryBlock> optimized = new ArrayList<>();
        List<MemoryBlock> usedBlocks = blocks.stream()
                .filter(b -> !b.isFree())
                .toList();

        int totalUsed = usedBlocks.stream()
                .mapToInt(MemoryBlock::getSize)
                .sum();

        optimized.addAll(usedBlocks);
        optimized.add(new MemoryBlock("Free", TOTAL_MEMORY - totalUsed, "#28a745"));
        return optimized;
    }

    private void mergeFreeBlocks() {
        List<MemoryBlock> merged = new ArrayList<>();
        MemoryBlock currentFree = null;

        for (MemoryBlock block : memoryBlocks) {
            if (block.isFree()) {
                if (currentFree == null) {
                    currentFree = block;
                } else {
                    currentFree.setSize(currentFree.getSize() + block.getSize());
                }
            } else {
                if (currentFree != null) {
                    merged.add(currentFree);
                    currentFree = null;
                }
                merged.add(block);
            }
        }

        if (currentFree != null) {
            merged.add(currentFree);
        }

        memoryBlocks = merged;
    }

    private void animateMemoryTransition(List<MemoryBlock> oldBlocks, List<MemoryBlock> newBlocks) {
        ParallelTransition transition = new ParallelTransition();

        for (MemoryBlock block : oldBlocks) {
            FadeTransition ft = new FadeTransition(Duration.millis(300), block.getNode());
            ft.setToValue(0);
            transition.getChildren().add(ft);
        }

        transition.setOnFinished(e -> {
            memoryVisualization.getChildren().clear();
            newBlocks.forEach(b -> {
                Pane node = b.getNode();
                node.setOpacity(0);
                FadeTransition ft = new FadeTransition(Duration.millis(300), node);
                ft.setToValue(1);
                ft.play();
                memoryVisualization.getChildren().add(node);
            });
        });

        transition.play();
    }

    private void updateVisuals() {
        updateMemoryDisplay();
        updateProcessTable();
        updateChart();
        updateMemorySummary();
    }

    private void updateMemoryDisplay() {
        memoryVisualization.getChildren().clear();
        memoryBlocks.forEach(block -> {
            Pane node = block.getNode();
            node.setPrefWidth((double) block.getSize() / TOTAL_MEMORY * 800);
            Tooltip.install(node, new Tooltip(block.toString()));
            memoryVisualization.getChildren().add(node);
        });
    }

    private void updateProcessTable() {
        ObservableList<ProcessInfo> processes = FXCollections.observableArrayList();
        memoryBlocks.stream()
                .filter(b -> !b.isFree())
                .forEach(b -> processes.add(new ProcessInfo(b.getName(), b.getSize())));
        processTable.setItems(processes);
    }

    private void updateChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        memoryBlocks.stream()
                .filter(b -> !b.isFree())
                .forEach(b -> series.getData().add(new XYChart.Data<>(b.getName(), b.getSize())));
        memoryChart.getData().setAll(series);
    }

    private void updateMemorySummary() {
        int used = memoryBlocks.stream()
                .filter(b -> !b.isFree())
                .mapToInt(MemoryBlock::getSize)
                .sum();
        memorySummary.setText(String.format("Memoria Usada: %d MB (%.1f%%) | Libre: %d MB",
                used, (used * 100.0) / TOTAL_MEMORY, TOTAL_MEMORY - used));
    }

    private void showStatus(String message, String type) {
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

    static class MemoryBlock {
        private String name;
        private int size;
        private String color;
        private Pane node;

        public MemoryBlock(String name, int size, String color) {
            this.name = name;
            this.size = size;
            this.color = color;
            this.node = new Pane();
            updateNode();
        }

        public String getName() { return name; }
        public int getSize() { return size; }
        public boolean isFree() { return name.equals("Free"); }
        public Pane getNode() { return node; }

        public void setName(String name) { this.name = name; }
        public void setSize(int size) { this.size = size; }
        public void setColor(String color) {
            this.color = color;
            updateNode();
        }

        private void updateNode() {
            node.setStyle("-fx-background-color: " + color + ";");
            node.setMinHeight(40);
        }

        @Override
        public String toString() {
            return isFree() ? "Libre: " + size + "MB" : name + ": " + size + "MB";
        }
    }

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