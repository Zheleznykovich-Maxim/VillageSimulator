import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class VillageSimulator {
    public static void main(String[] args) throws InterruptedException {
        List<Villager> villagers = new CopyOnWriteArrayList<>();
        ExecutorService executor = Executors.newCachedThreadPool();
        Villager starter = new Villager(executor);
        villagers.add(starter);
        executor.submit(starter);
//        Thread firstThread = new Thread(starter);
//        firstThread.start();

        for (int day = 1; day <= 100; day++) {
            System.out.println("--- День " + day + " ---");
            int villagersSize = villagers.stream()
                            .mapToInt(VillageSimulator::countAliveVillagers)
                                    .sum();
            System.out.println("Население деревни: " + villagersSize);

            if (villagersSize < 1) {
                System.out.println("Все жители вымерли. Симуляция окончена.");
                break;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Симуляция завершена \n");
        printVillagersStatistic(villagers);
        executor.shutdown();
//        firstThread.interrupt();
//        firstThread.join();

    }

    public static int countAliveVillagers(Villager villager) {
        int count = villager.isAlive() ? 1 : 0;

        if (villager.getPopulation() != null) {
            for (Villager child : villager.getPopulation()) {
                count += countAliveVillagers(child);
            }
        }

        return count;
    }

    public static int countTotal(Villager villager) {
        int count = 1;

        if (villager.getPopulation() != null) {
            for (Villager child : villager.getPopulation()) {
                count += countTotal(child);
            }
        }

        return count;
    }

    public static void printVillagersStatistic(List<Villager> villagers) {
        int countTotalVillagers = countTotal(villagers.get(0));
        System.out.println("Общее количество жителей: " + countTotalVillagers);
        System.out.println("Количество умерших жителей: " + (countTotalVillagers - countAliveVillagers(villagers.get(0))));
        System.out.println("--- Некролог ---");
        List<Villager> deadVillagers = new ArrayList<>();
        collectDead(villagers.get(0), deadVillagers);
        deadVillagers.sort(Comparator.comparing(Villager::getId));
        deadVillagers.forEach(deadVillager -> {
            System.out.println("Житель " + deadVillager.getId() + " умер в возрасте " + deadVillager.getAge());
            System.out.println("Количество детей: " + (deadVillager.getPopulationSize()));
            String children = deadVillager.getPopulation().stream()
                    .map(v -> "Житель " + v.getId())
                    .collect(Collectors.joining(", "));
            System.out.println("Дети: " + children);
            System.out.println("---------------");
        });
//        for (Villager villager : villagers.get(0).getPopulation()) {
//            System.out.println("Житель " + villager.getId() + " умер в возрасте " + villager.getAge());
//            System.out.println("Количество детей: " + (villager.getPopulationSize()));
//            String children = villager.getPopulation().stream()
//                            .map(v -> "Житель " + v.getId())
//                            .collect(Collectors.joining(", "));
//            System.out.println("Дети: " + children);
//        }
    }

    public static void collectDead(Villager villager, List<Villager> deadList) {
        if (!villager.isAlive()) {
            deadList.add(villager);
        }

        if (villager.getPopulation() != null) {
            for (Villager child : villager.getPopulation()) {
                collectDead(child, deadList);
            }
        }
    }
}