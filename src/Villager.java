import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class Villager implements Runnable {
    private static int counter = 1;
    private int id;
    private int age;
    private int health;
    private int food;
    private boolean alive;
    private Random random;
    private List<Villager> population;

    public Villager() {
        this.id = counter++;
        this.age = 0;
        this.health = 100;
        this.food = 50;
        this.alive = true;

        this.random = new Random();
        this.population = new CopyOnWriteArrayList<>();
    }

    public Villager(List<Villager> population) {
        this.id = counter++;
        this.age = 0;
        this.health = 100;
        this.food = 50;
        this.alive = true;
        this.random = new Random();
        this.population = population;
    }

    @Override
    public void run() {
        while (alive) {
            liveOneDay();
            try {
                Thread.sleep(500);
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
            Villager child = new Villager();
            population.add(child);
            new Thread(child).start();
            System.out.println("У жителя " + id + " родился новый житель!");
        }
    }

    private boolean canReproduce() {
        return alive && age > 18 && age < 50 && random.nextInt(100) < 5;
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
