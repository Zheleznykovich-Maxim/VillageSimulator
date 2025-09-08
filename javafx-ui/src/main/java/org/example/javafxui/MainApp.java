package org.example.javafxui;

import events.EventDispatcher;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import simulation.VillageSimulator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainApp extends Application {

    private ObservableList<VillagerData> villagerList = FXCollections.observableArrayList();
    private XYChart.Series<Number, Number> populationSeries = new XYChart.Series<>();
    private int dayCounter = 0;

    private Pane genealogyPane;
    private VillagerData rootVillager;
    private Map<Integer, VillagerData> villagerMap = new HashMap<>(); // по id

    @Override
    public void start(Stage primaryStage) {

        TableView<VillagerData> tableView = new TableView<>(villagerList);

        TableColumn<VillagerData, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<VillagerData, Integer> ageColumn = new TableColumn<>("Age");
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));

        TableColumn<VillagerData, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        tableView.getColumns().addAll(idColumn, ageColumn, statusColumn);

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Day");
        xAxis.setMinorTickCount(0);
        xAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(xAxis) {
            @Override
            public String toString(Number object) {
                double value = object.doubleValue();
                if (value == Math.floor(value)) {
                    return String.valueOf((int) value); // целое → выводим
                } else {
                    return ""; // дробное → скрываем
                }
            }
        });
        xAxis.setLabel("Day");
        xAxis.setForceZeroInRange(false);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Alive Villagers");
        yAxis.setTickUnit(1);
        yAxis.setMinorTickCount(0);
        yAxis.setForceZeroInRange(false);
        yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis) {
            @Override
            public String toString(Number object) {
                double value = object.doubleValue();
                if (value == Math.floor(value)) {
                    return String.valueOf((int) value); // целое → выводим
                } else {
                    return ""; // дробное → скрываем
                }
            }
        });

        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.getData().add(populationSeries);
        lineChart.lookup(".chart-series-line").setStyle("-fx-stroke-width: 2px;");
        populationSeries.setName("Population");

        BorderPane root = new BorderPane();

        SplitPane splitPane = new SplitPane();
        tableView.setPrefWidth(350);

        genealogyPane = new Pane();
        genealogyPane.setPrefWidth(550);

        // добавляем в SplitPane
        splitPane.getItems().addAll(tableView, genealogyPane);
        splitPane.setDividerPositions(0.3); // 30% слева, 70% справа

        root.setCenter(splitPane);
        root.setBottom(lineChart);
        lineChart.setPrefHeight(250);

        Scene scene = new Scene(root, 900, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Village Simulation");
        primaryStage.show();

        startsSimulation();
    }

    private void startsSimulation() {
        EventDispatcher dispatcher = new EventDispatcher();

        dispatcher.registerListener(event -> Platform.runLater(() -> {
            switch (event.getType()) {
                case DAY_PASSED -> {
                    dayCounter++;
                    int aliveCount = event.getValue();
                    XYChart.Data<Number, Number> point = new XYChart.Data<>(dayCounter, aliveCount);
                    point.nodeProperty().addListener((obs, oldNode, newNode) -> {
                        if (newNode != null) {
                            newNode.setStyle(
                                    "-fx-background-color: E66E1DFF; " + // заливка
                                            "-fx-background-radius: 2px; " +   // радиус кружка
                                            "-fx-padding: 2px;"             // размер (чем меньше padding, тем меньше точка)

                            );
                        }
                    });

                    populationSeries.getData().add(point);

                    // обновляем дерево раз в день
                    if (rootVillager != null) {
                        drawGenealogy(genealogyPane, rootVillager);
                    }
                }

                case BIRTH, FOOD_FOUND -> {
                    VillagerData data = new VillagerData(
                            event.getVillager().getId(),
                            event.getVillager().getAge(),
                            event.getVillager().isAlive() ? "Alive" : "Dead"
                    );
                    villagerMap.put(data.getId(), data);
                    villagerList.removeIf(v -> v.getId() == data.getId());
                    villagerList.add(data);

                    if (event.getVillager().getParentId() != null) {
                        VillagerData parent = villagerMap.get(event.getVillager().getParentId());
                        if (parent != null) {
                            parent.addChild(data);
                        }
                    } else {
                        rootVillager = data;
                    }
                    drawGenealogy(genealogyPane, rootVillager);
                }
                case DEATH -> {
                    var villager = event.getVillager();
                    VillagerData data = villagerMap.get(villager.getId());
                    if (data != null) {
                        data.setStatus("Dead");
                    }
                    drawGenealogy(genealogyPane, rootVillager);
                }
            }


        }));

        VillageSimulator simulator = new VillageSimulator(dispatcher);
        new Thread(simulator::start).start();
    }

    private void drawGenealogy(Pane pane, VillagerData root) {
        pane.getChildren().clear();
        drawVillager(pane, root, 250, 20, 200);
    }

    private void drawVillager(Pane pane, VillagerData villager, double x, double y, double spacing) {
        Label label = new Label("Житель " + villager.getId());
        label.setStyle(villager.getStatus().equals("Alive")
                ? "-fx-background-color: lightgreen; -fx-border-color: black; -fx-padding: 3;"
                : "-fx-background-color: lightgray; -fx-border-color: black; -fx-padding: 3;");
        label.setLayoutX(x);
        label.setLayoutY(y);

        pane.getChildren().add(label);

        List<VillagerData> children = villager.getChildren();
        if (children != null && !children.isEmpty()) {
            double childX = x - (children.size() - 1) * spacing / 2;
            double childY = y + 80;

            for (VillagerData child : children) {
                javafx.scene.shape.Line line = new javafx.scene.shape.Line(
                        x + 30, y + 20,
                        childX + 30, childY
                );
                pane.getChildren().add(line);

                drawVillager(pane, child, childX, childY, spacing / 1.5);
                childX += spacing;
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }
}