package simulation;

import events.EventDispatcher;
import events.VillagerEvent;
import events.VillagerEventType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;

@Getter
public class Villager implements Runnable {
    private static int counter = 1;
    private final int id;
    private final Integer parentId;
    private int age;
    private int health;
    private int food;
    private boolean alive;
    private final Random random;
    private List<Villager> population;
    private final ExecutorService executor;
    private final EventDispatcher dispatcher;

    public Villager(Integer parentId, ExecutorService executor, EventDispatcher dispatcher) {
        this.id = counter++;
        this.parentId = parentId;
        this.age = 0;
        this.health = 100;
        this.food = 50;
        this.alive = true;
        this.random = new Random();
        this.population = Collections.synchronizedList(new ArrayList<>());
        this.executor = executor;
        this.dispatcher = dispatcher;
    }

    @Override
    public void run() {

        while (alive && !Thread.currentThread().isInterrupted()) {
            liveOneDay();
            try {
                for (int i = 0; i < 5; i++) {
                    Thread.sleep(100);
                    if (Thread.currentThread().isInterrupted()) {
                        return;
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void liveOneDay() {
        if (!alive) return;

        age++;


        if (food <= 0) {
            health -= 20;
        } else {
            food -= 10;
            health = Math.min(100, health + 5);
        }

        if (random.nextBoolean()) {
            int foundFood =random.nextInt(35);
            food += foundFood;
            System.out.println("Житель " + id + " нашёл " + foundFood + " еды");
            dispatcher.dispath(new VillagerEvent(this, VillagerEventType.FOOD_FOUND, foundFood));

            if (health <= 0 || age > 80) {
                alive = false;
                System.out.println("Житель " + id + " умер в возрасте " + age);
                dispatcher.dispath(new VillagerEvent(this, VillagerEventType.DEATH, 0));
            }
        }

        if (canReproduce()) {
            Villager child = new Villager(id, executor, dispatcher);
            population.add(child);
            executor.submit(child);
            System.out.println("У жителя " + id + " родился новый житель!");
            dispatcher.dispath(new VillagerEvent(this, VillagerEventType.BIRTH, 0));
        }
    }

    private boolean canReproduce() {
        return alive && age >= 18 && age <= 50 && random.nextInt(100) < 5;
    }
}
