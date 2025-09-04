package simulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;

public class Villager implements Runnable {
    private static int counter = 1;
    private final int id;
    private int age;
    private int health;
    private int food;
    private boolean alive;
    private final Random random;
    private List<Villager> population;
    private final ExecutorService executor;

    public Villager(ExecutorService executor) {
        this.id = counter++;
        this.age = 0;
        this.health = 100;
        this.food = 50;
        this.alive = true;

        this.random = new Random();
        this.population = Collections.synchronizedList(new ArrayList<>());
        this.executor = executor;
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
            int foundFood =random.nextInt(30);
            food += foundFood;
            System.out.println("Житель " + id + " нашёл " + foundFood + " еды");

            if (health <= 0 || age > 80) {
                alive = false;
                System.out.println("Житель " + id + " умер в возрасте " + age);
            }
        }

        if (canReproduce()) {
            Villager child = new Villager(executor);
            population.add(child);
            executor.submit(child);
            System.out.println("У жителя " + id + " родился новый житель!");
        }
    }

    private boolean canReproduce() {
        return alive && age >= 18 && age <= 50 && random.nextInt(100) < 5;
    }

    public boolean isAlive() {
        return alive;
    }

    public int getId() {
        return id;
    }

    public int getAge() {
        return age;
    }

    public int getPopulationSize() {
        return population.size();
    }

    public List<Villager> getPopulation() {
        return population;
    }
}
