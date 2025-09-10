package org.example.springanalytics.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "simulation_stats")
public class SimulationStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long simulationId;

    private int daysElapsed;

    private int totalVillagers;

    private int aliveVillagers;

    public double getSurvivalRate() {
        return totalVillagers == 0 ? 0.0 : (aliveVillagers * 100.0) / totalVillagers;
    }
}
