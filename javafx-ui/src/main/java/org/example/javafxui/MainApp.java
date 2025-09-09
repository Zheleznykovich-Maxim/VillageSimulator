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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.javafxui.genealogy.GenealogyView;
import simulation.VillageSimulator;

import java.util.HashMap;
import java.util.Map;

public class MainApp extends Application {

    private ObservableList<VillagerData> villagerList = FXCollections.observableArrayList();
    private XYChart.Series<Number, Number> populationSeries = new XYChart.Series<>();
    private int dayCounter = 0;

    private GenealogyView genealogyView;
    private VillagerData rootVillager;
    private final Map<Integer, VillagerData> villagerMap = new HashMap<>(); // по id

    @Override
    public void start(Stage primaryStage) {

        // таблица жителей
        TableView<VillagerData> tableView = new TableView<>(villagerList);

        TableColumn<VillagerData, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<VillagerData, Integer> ageColumn = new TableColumn<>("Age");
        ageColumn.setCellValueFactory(cellData -> cellData.getValue().getAge().asObject());

        TableColumn<VillagerData, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().getStatus());

        tableView.getColumns().addAll(idColumn, ageColumn, statusColumn);
        tableView.setPrefWidth(350);

        // график
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Day");
        xAxis.setMinorTickCount(0);
        xAxis.setForceZeroInRange(false);
        xAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(xAxis) {
            @Override
            public String toString(Number object) {
                double v = object.doubleValue();
                return v == Math.floor(v) ? String.valueOf((int) v) : "";
            }
        });
        xAxis.setLabel("Day");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Alive Villagers");
        yAxis.setTickUnit(1);
        yAxis.setMinorTickCount(0);
        yAxis.setForceZeroInRange(false);
        yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis) {
            @Override
            public String toString(Number object) {
                double v = object.doubleValue();
                return v == Math.floor(v) ? String.valueOf((int) v) : "";
            }
        });

        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.getData().add(populationSeries);
        lineChart.lookup(".chart-series-line").setStyle("-fx-stroke-width: 2px;");
        lineChart.setPrefHeight(250);
        populationSeries.setName("Population");

        // родовое дерево
        genealogyView = new GenealogyView();

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(genealogyView);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(tableView, scrollPane);
        splitPane.setDividerPositions(0.3); // 30% слева, 70% справ

        BorderPane root = new BorderPane();
        root.setCenter(splitPane);
        root.setBottom(lineChart);


        Scene scene = new Scene(root, 900, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Village Simulation");
        primaryStage.show();

        startSimulation();
    }

    private void startSimulation() {
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
                                    "-fx-background-color: E66E1DFF;" +
                                            "-fx-background-radius: 2px;" +
                                            "-fx-padding: 2px;"
                            );
                        }
                    });

                    populationSeries.getData().add(point);

                    // обновляем дерево раз в день
                    if (rootVillager != null) {
                        genealogyView.update(rootVillager);
                    }
                }

                case BIRTH, FOOD_FOUND -> {
                    var villagerCore = event.getVillager();

                    VillagerData data = villagerMap.computeIfAbsent(
                            villagerCore.getId(),
                            id -> new VillagerData(
                                    id,
                                    villagerCore.getAge(),
                                    villagerCore.isAlive() ? "Alive" : "Dead"
                            )
                    );

                    data.setAge(villagerCore.getAge());
                    data.setStatus(villagerCore.isAlive() ? "Alive" : "Dead");

                    villagerList.removeIf(v -> v.getId() == data.getId());
                    villagerList.add(data);

                    if (villagerCore.getParentId() != null) {
                        VillagerData parent = villagerMap.get(villagerCore.getParentId());
                        if (parent != null && !parent.getChildren().contains(data)) {
                            parent.addChild(data);
                        }
                    } else if (rootVillager == null) {
                        rootVillager = data; // первый без родителя становится корнем
                    }

                    genealogyView.update(rootVillager);
                }

                case DEATH -> {
                    var villager = event.getVillager();
                    VillagerData data = villagerMap.get(villager.getId());
                    if (data != null) {
                        data.setStatus("Dead");
                    }
                    genealogyView.update(rootVillager);
                }
            }
        }));

        VillageSimulator simulator = new VillageSimulator(dispatcher);
        new Thread(simulator::start).start();
    }

    public static void main(String[] args) {
        launch();
    }
}