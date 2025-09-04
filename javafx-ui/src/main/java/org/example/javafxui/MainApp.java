package org.example.javafxui;

import events.EventDispatcher;
import events.VillagerEventType;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import simulation.VillageSimulator;

public class MainApp extends Application {

    private ObservableList<VillagerData> villagerList = FXCollections.observableArrayList();
    private XYChart.Series<Number, Number> populationSeries = new XYChart.Series<>();
    private int dayCounter = 0;

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
        root.setTop(tableView);
        root.setCenter(lineChart);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Village Simulation");
        primaryStage.show();

        startsSimulation();
    }

    private void startsSimulation() {
        EventDispatcher dispatcher = new EventDispatcher();

        dispatcher.registerListener(event -> Platform.runLater(() -> {
            if (event.getType() == VillagerEventType.DAY_PASSED) {
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

            } else {
                VillagerData data = new VillagerData(
                        event.getVillager().getId(),
                        event.getVillager().getAge(),
                        event.getVillager().isAlive() ? "Alive" : "Dead"
                );

                villagerList.removeIf(v -> v.getId() == data.getId());
                villagerList.add(data);
            }

        }));

        VillageSimulator simulator = new VillageSimulator(dispatcher);
        new Thread(simulator::start).start();
    }

    public static void main(String[] args) {
        launch();
    }
}