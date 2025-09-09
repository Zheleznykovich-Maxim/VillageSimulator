package org.example.springanalytics.controller;

import org.example.springanalytics.service.SimulationStatsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    private final SimulationStatsService simulationStatsService;

    public ViewController(SimulationStatsService simulationStatsService) {
        this.simulationStatsService = simulationStatsService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("simulations", simulationStatsService.getAllSimulationStats());
        return "dashboard";
    }
}
