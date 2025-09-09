package org.example.springanalytics.repository;

import org.example.springanalytics.model.SimulationStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SimulationStatsRepository extends JpaRepository<SimulationStats, Long> {
}
