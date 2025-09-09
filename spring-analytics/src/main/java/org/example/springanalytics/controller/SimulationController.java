package org.example.springanalytics.controller;

import dto.SimulationStatsDTO;
import org.example.springanalytics.mapper.SimulationStatsMapper;
import org.example.springanalytics.model.SimulationStats;
import org.example.springanalytics.service.SimulationStatsService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/simulations")
public class SimulationController {

    private final SimulationStatsService simulationStatsService;
    private final SimulationStatsMapper simulationStatsMapper;

    public SimulationController(SimulationStatsService simulationStatsService, SimulationStatsMapper simulationStatsMapper) {
        this.simulationStatsService = simulationStatsService;
        this.simulationStatsMapper = simulationStatsMapper;
    }

    @PostMapping
    public SimulationStats createSimulation(@RequestBody SimulationStatsDTO simulationStatsDTO) {
        SimulationStats entity = simulationStatsMapper.toEntity(simulationStatsDTO);
        return simulationStatsService.saveOrUpdate(entity);
    }

    @PostMapping("/{id}/update")
    public SimulationStats updateSimulation(@PathVariable Long id,
                                            @RequestBody SimulationStats simulationStats) {
        simulationStats.setSimulationId(id);
        return simulationStatsService.saveOrUpdate(simulationStats);
    }

    @GetMapping("/{id}")
    public SimulationStats getSimulation(@PathVariable Long id) {
        return simulationStatsService.getSimulationStatsById(id);
    }

    @GetMapping
    public List<SimulationStats> getAllSimulations() {
        return simulationStatsService.getAllSimulationStats();
    }
}
