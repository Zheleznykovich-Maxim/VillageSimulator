package org.example.springanalytics.service;

import jakarta.transaction.Transactional;
import org.example.springanalytics.model.SimulationStats;
import org.example.springanalytics.repository.SimulationStatsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SimulationStatsService {

    private final SimulationStatsRepository simulationStatsRepository;

    public SimulationStatsService(final SimulationStatsRepository simulationStatsRepository) {
        this.simulationStatsRepository = simulationStatsRepository;
    }

    @Transactional
    public SimulationStats saveOrUpdate(SimulationStats simulationStats) {
        return simulationStatsRepository.save(simulationStats);
    }

    public List<SimulationStats> getAllSimulationStats() {
        return simulationStatsRepository.findAll();
    }

    public SimulationStats getSimulationStatsById(Long id) {
        return simulationStatsRepository.findById(id).orElse(null);
    }
}
