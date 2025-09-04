package events;

import lombok.Getter;
import simulation.Villager;

@Getter
public class VillagerEvent {
    private final Villager villager;
    private final VillagerEventType type;
    private final int value;

    public VillagerEvent(Villager villager, VillagerEventType type, int value) {
        this.villager = villager;
        this.type = type;
        this.value = value;
    }
}
