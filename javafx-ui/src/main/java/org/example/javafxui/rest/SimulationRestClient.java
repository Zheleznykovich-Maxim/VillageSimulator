package org.example.javafxui.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import dto.SimulationStatsDTO;

public class SimulationRestClient {

    private final String baseUrl;
    private final RestTemplate restTemplate;

    public SimulationRestClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();

    }

    public void sendUpdate(int daysElapsed, int totalVillagers, int aliveVillagers) {
        SimulationStatsDTO simulationStatsDTO = new SimulationStatsDTO();
        simulationStatsDTO.setDaysElapsed(daysElapsed);
        simulationStatsDTO.setTotalVillagers(totalVillagers);
        simulationStatsDTO.setAliveVillagers(aliveVillagers);

        String url = baseUrl + "/api/simulations";
        try {

            ResponseEntity<SimulationStatsDTO> response = restTemplate.postForEntity(url, simulationStatsDTO, SimulationStatsDTO.class);
            System.out.println("Updated simulation : " + response.getBody());
        } catch (Exception e) {
            System.err.println("Failed to send update: " + e.getMessage());
        }
    }
}
