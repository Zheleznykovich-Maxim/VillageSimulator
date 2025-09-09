package org.example.springanalytics.mapper;

import dto.SimulationStatsDTO;
import org.example.springanalytics.model.SimulationStats;
import org.springframework.stereotype.Component;

@Component
public class SimulationStatsMapper {

    public SimulationStats toEntity(SimulationStatsDTO dto) {
        if (dto == null) return null;
        SimulationStats entity = new SimulationStats();
        entity.setDaysElapsed(dto.getDaysElapsed());
        entity.setTotalVillagers(dto.getTotalVillagers());
        entity.setAliveVillagers(dto.getAliveVillagers());
        return entity;
    }

    public SimulationStatsDTO toDTO(SimulationStats entity) {
        if (entity == null) return null;
        return new SimulationStatsDTO(
                entity.getDaysElapsed(),
                entity.getTotalVillagers(),
                entity.getAliveVillagers()
        );
    }
}
