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
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Day");
        yAxis.setLabel("Population");

        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.getData().add(populationSeries);
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
            VillagerData data = new VillagerData(event.getVillager().getId(), event.getVillager().getAge(), event.getVillager().isAlive() ? "Alive" : "Dead");
            villagerList.removeIf(v -> v.getId() == data.getId());
            villagerList.add(data);

            dayCounter++;
            populationSeries.getData().add(new XYChart.Data<>(dayCounter, villagerList.size()));

        }));

        VillageSimulator simulator = new VillageSimulator(dispatcher);
        new Thread(simulator::start).start();
//        ExecutorService executor = Executors.newCachedThreadPool();
//        Villager starter = new Villager(executor, dispatcher);
//        executor.submit(starter);
    }

    public static void main(String[] args) {
        launch();
    }
}