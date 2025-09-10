package dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimulationStatsDTO {
    private int daysElapsed;
    private int totalVillagers;
    private int aliveVillagers;
}