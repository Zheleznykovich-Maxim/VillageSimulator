package simulation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class VillageSimulator {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();
        Villager starter = new Villager(executor);
        executor.submit(starter);

        for (int day = 1; day <= 100; day++) {
            System.out.println("--- День " + day + " ---");
            int villagersSize = countAliveVillagers(starter);
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
        executor.shutdownNow();
        printVillagersStatistic(starter);
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

    public static void printVillagersStatistic(Villager villager) {

        int countTotalVillagers = countTotal(villager);
        int countAliveVillagers = countAliveVillagers(villager);
        int countDeadVillagers = countTotalVillagers - countAliveVillagers;

        System.out.println("Общее количество жителей: " + countTotalVillagers);
        System.out.println("Количество умерших жителей: " + countDeadVillagers);

        System.out.println("--- Некролог ---");
        List<Villager> deadVillagers = new ArrayList<>();
        collectDead(villager, deadVillagers);
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