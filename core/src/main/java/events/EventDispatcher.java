package events;

import java.util.ArrayList;
import java.util.List;

public class EventDispatcher {
    private final List<VillagerEventListener> listeners = new ArrayList<>();

    public synchronized void registerListener(VillagerEventListener listener) {
        listeners.add(listener);
    }

    public synchronized void unregisterListener(VillagerEventListener listener) {
        listeners.remove(listener);
    }

    public synchronized void dispath(VillagerEvent event) {
        for (VillagerEventListener listener : listeners) {
            listener.onVillagerEvent(event);
        }
    }
}
