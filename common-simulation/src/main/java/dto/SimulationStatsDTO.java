package dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimulationStatsDTO {
    private int daysElapsed;
    private int totalVillagers;
    private int aliveVillagers;


}