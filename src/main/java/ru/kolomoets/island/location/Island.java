package ru.kolomoets.island.location;

import ru.kolomoets.island.entity.IslandObjects;
import ru.kolomoets.island.entity.animals.AbstractAnimal;
import ru.kolomoets.island.entity.animals.herbivores.*;
import ru.kolomoets.island.entity.animals.predators.*;
import ru.kolomoets.island.entity.plants.Grass;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

public class Island {

    private final AtomicInteger tickCounter = new AtomicInteger(0);

    private int height;
    private int width;
    private Cell[][] grid;

    public Island() {
    }

    public void init(int height, int width) {
        this.height = height;
        this.width = width;
        this.grid = new Cell[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = new Cell();
            }
        }
    }

    public void run() {
        SimulationConfig config = SimulationConfig.create();
        init(config.getHeight(), config.getWidth());

        fillRandomCellsWithGrassByChance();
        seedAllAnimals();
        printMapCompact(0);
        printAnimalStats(0);
//        printInitialStats();
//        printPlantStats(0);

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            int tick = tickCounter.incrementAndGet();

            phaseAnimalsLifeCycle();
            phaseGrowPlants();

            printMapCompact(tick);
            printAnimalStats(tick);
//            printMap(tick);
//            printPlantStats(tick);
//            printCellStats(tick);

            if (tick >= config.getTicks()) {
                scheduler.shutdown();
                System.out.println("Симуляция завершена после " + tick + " тактов.");
            }

        }, 0, config.getTickMillis(), TimeUnit.MILLISECONDS);
    }

    public void forEachCell(BiConsumer<Integer, Integer> action) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                action.accept(x, y);
            }
        }
    }

    private void phaseGrowPlants() {
        int limit = new Grass().getMaxInCell();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Cell cell = getCell(x, y);
                int have = cell.getObjectsByType(Grass.class).size();
                int need = limit - have;

                for (int i = 0; i < need; i++) {
                    cell.addObject(new Grass());
                }
            }
        }
    }

    private void fillRandomCellsWithGrassByChance() {
        final double FILL_PERCENT = 0.8;

        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                if (rnd.nextDouble() < FILL_PERCENT) {
                    Cell cell = getCell(x, y);
                    int have = cell.getObjectsByType(Grass.class).size();
                    int limit = new Grass().getMaxInCell();
                    int add = limit - have;

                    for (int i = 0; i < add; i++) {
                        cell.addObject(new Grass());
                    }
                }
            }
        }
    }

    private void seedAnimals(Class<? extends AbstractAnimal> type, int total) {
        if (total <= 0) return;

        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        int placed = 0;

        while (placed < total) {
            int x = rnd.nextInt(width);
            int y = rnd.nextInt(height);
            Cell cell = getCell(x, y);

            try {
                AbstractAnimal sample = type.getDeclaredConstructor().newInstance();
                int have = cell.getObjectsByType(type).size();
                int limit = sample.getMaxInCell();

                if (have < limit) {
                    AbstractAnimal animal = type.getDeclaredConstructor().newInstance();
                    spawnAt(animal, x, y);
                    placed++;
                }
            } catch (Exception e) {
                throw new RuntimeException("Не удалось создать животное: " + type, e);
            }
        }
    }

    private void seedAllAnimals() {

        Map<Class<? extends AbstractAnimal>, Integer> initialPopulation =
                AnimalPopulationConfig.createInitialPopulation(getWidth(), getHeight());
        for (var entry : initialPopulation.entrySet()) {
            seedAnimals(entry.getKey(), entry.getValue());
        }
    }

    private void printPlantStats(int tick) {
        int grass = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grass += getCell(x, y).getObjectsByType(Grass.class).size();
            }
        }
        System.out.printf("[tick %d] grass=%d%n", tick, grass);
    }

    private void printAnimalStats(int tick) {
        System.out.printf("=== Статистика (tick %d) ===%n", tick);

        System.out.println("Травоядные:");
        printStatsForGroup(List.of(Rabbit.class, Deer.class, Horse.class, Goat.class, Sheep.class,
                Boar.class, Mouse.class, Buffalo.class, Duck.class, Caterpillar.class));

        System.out.println("Хищники:");
        printStatsForGroup(List.of(Wolf.class, Fox.class, Bear.class, Eagle.class, Python.class));

        System.out.println("Растения:");
        int grassCount = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grassCount += getCell(x, y).getObjectsByType(Grass.class).size();
            }
        }
        System.out.printf("  🌿 Grass: %d%n", grassCount);
    }

    private void printStatsForGroup(List<Class<? extends AbstractAnimal>> group) {
        for (Class<? extends AbstractAnimal> type : group) {
            int count = 0;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    count += getCell(x, y).getObjectsByType(type).size();
                }
            }
            try {
                AbstractAnimal sample = type.getDeclaredConstructor().newInstance();
                System.out.printf("  %s %-12s %d%n", sample.getSymbol(), type.getSimpleName() + ":", count);
            } catch (Exception ignored) {
            }
        }
    }

    private void printInitialStats() {
        System.out.println("=== Начальное заселение острова ===");

        for (var entry : AnimalPopulationConfig.createInitialPopulation(getWidth(), getHeight()).entrySet()) {
            Class<? extends AbstractAnimal> type = entry.getKey();
            int count = 0;

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    count += getCell(x, y).getObjectsByType(type).size();
                }
            }

            System.out.printf("%s = %d%n", type.getSimpleName(), count);
        }
        System.out.println("===================================");
    }

    public boolean isOutside(int x, int y) {
        return x < 0 || x >= width || y < 0 || y >= height;
    }

    public Cell getCell(int x, int y) {
        if (isOutside(x, y)) return null;
        return grid[y][x];
    }

    public void spawnAt(IslandObjects obj, int x, int y) {
        Cell cell = getCell(x, y);
        if (cell != null && cell.addObject(obj)) {
            if (obj instanceof AbstractAnimal animal) {
                animal.onSpawn(this, x, y);
            }
        }
    }

    public boolean removeAt(IslandObjects obj, int x, int y) {
        Cell cell = getCell(x, y);
        if (cell == null) return false;
        return cell.removeObject(obj);
    }

    public void tryMove(IslandObjects obj, int fromX, int fromY, int toX, int toY) {
        if (fromX == toX && fromY == toY) return;
        if (isOutside(toX, toY)) return;

        Cell from = getCell(fromX, fromY);
        Cell to = getCell(toX, toY);
        if (from == null || to == null) return;

        int current = to.getObjectsByType(obj.getClass()).size();
        if (current >= obj.getMaxInCell()) return;

        boolean removed = from.removeObject(obj);
        if (!removed) return;

        to.addObject(obj);

        if (obj instanceof AbstractAnimal animal) {
            animal.onMovedTo(toX, toY, to);
        }
    }

    private void phaseAnimalsLifeCycle() {
        ExecutorService animalsPool = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors()
        );

        forEachCell((x, y) -> {
            Cell cell = getCell(x, y);
            animalsPool.submit(() -> {
                ArrayList<IslandObjects> snapshot = new ArrayList<>(cell.getAllObjects());
                for (IslandObjects obj : snapshot) {
                    if (obj instanceof AbstractAnimal animal) {
                        animal.lifeCycle();
                        if (!animal.isAlive()) {
                            cell.removeObject(animal);
                        }
                    }
                }
            });
        });

        animalsPool.shutdown();
        try {
            animalsPool.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void printCellStats(int tick) {
        System.out.printf("=== Состояние клеток (tick %d) ===%n", tick);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Cell cell = getCell(x, y);

                Map<String, Integer> counts = new ConcurrentHashMap<>();
                for (IslandObjects obj : cell.getAllObjects()) {
                    counts.merge(obj.getClass().getSimpleName(), 1, Integer::sum);
                }

                if (!counts.isEmpty()) {
                    System.out.printf("Клетка (%d,%d): %s%n", x, y, counts);
                }
            }
        }
        System.out.println("===================================");
    }

    public void printMap(int tick) {
        System.out.printf("=== Карта острова (tick %d) ===%n", tick);

        int cellWidth = 12;

        for (int x = 0; x < width; x++) {
            System.out.print("+");
            for (int i = 0; i < cellWidth; i++) System.out.print("-");
        }
        System.out.println("+");

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Cell cell = getCell(x, y);
                Collection<IslandObjects> objects = cell.getAllObjects();

                String predatorOut = "-";
                String herbivoreOut = "-";
                int plantsCount = 0;

                if (!objects.isEmpty()) {
                    Map<String, Integer> predators = new HashMap<>();
                    Map<String, Integer> herbivores = new HashMap<>();

                    for (IslandObjects obj : objects) {
                        if (obj instanceof AbstractAnimal animal) {
                            String type = animal.getClass().getSimpleName();
                            String symbol = animal.getSymbol();

                            if (isPredator(type)) {
                                predators.put(symbol, predators.getOrDefault(symbol, 0) + 1);
                            } else if (!type.equals("Caterpillar")) {
                                herbivores.put(symbol, herbivores.getOrDefault(symbol, 0) + 1);
                            }
                        } else {
                            plantsCount++;
                        }
                    }

                    int maxPred = 0;
                    for (Map.Entry<String, Integer> e : predators.entrySet()) {
                        if (e.getValue() > maxPred) {
                            predatorOut = e.getKey() + e.getValue();
                            maxPred = e.getValue();
                        }
                    }

                    int maxHerb = 0;
                    for (Map.Entry<String, Integer> e : herbivores.entrySet()) {
                        if (e.getValue() > maxHerb) {
                            herbivoreOut = e.getKey() + e.getValue();
                            maxHerb = e.getValue();
                        }
                    }
                }
                String content = String.format("%s/%s/🌿%d", predatorOut, herbivoreOut, plantsCount);

                System.out.printf("|%-12s", content);
            }
            System.out.println("|");

            for (int x = 0; x < width; x++) {
                System.out.print("+");
                for (int i = 0; i < cellWidth; i++) System.out.print("-");
            }
            System.out.println("+");
        }
    }

    private boolean isPredator(String typeName) {
        return typeName.equals("Wolf") ||
               typeName.equals("Python") ||
               typeName.equals("Fox") ||
               typeName.equals("Bear") ||
               typeName.equals("Eagle");
    }

    public void printMapCompact(int tick) {
        System.out.printf("=== Остров (tick %d) ===%n", tick);

        int cellWidth = 4;

        for (int x = 0; x < width; x++) {
            System.out.print("+");
            for (int i = 0; i < cellWidth; i++) System.out.print("-");
        }
        System.out.println("+");

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Cell cell = getCell(x, y);
                Collection<IslandObjects> objects = cell.getAllObjects();

                String symbol = ".";

                if (!objects.isEmpty()) {
                    List<AbstractAnimal> animals = new ArrayList<>();
                    for (IslandObjects obj : objects) {
                        if (obj instanceof AbstractAnimal animal &&
                            !animal.getClass().getSimpleName().equals("Caterpillar")) {
                            animals.add(animal);
                        }
                    }

                    if (!animals.isEmpty()) {
                        Map<String, Integer> counts = new HashMap<>();
                        for (AbstractAnimal a : animals) {
                            counts.merge(a.getClass().getSimpleName(), 1, Integer::sum);
                        }

                        String dominantType = null;
                        int maxCount = 0;
                        for (var entry : counts.entrySet()) {
                            if (entry.getValue() > maxCount) {
                                dominantType = entry.getKey();
                                maxCount = entry.getValue();
                            }
                        }

                        for (AbstractAnimal a : animals) {
                            if (a.getClass().getSimpleName().equals(dominantType)) {
                                symbol = a.getSymbol();
                                break;
                            }
                        }
                    } else {
                        symbol = "🌿";
                    }
                }

                System.out.printf("| %-3s", symbol);
            }
            System.out.println("|");

            for (int x = 0; x < width; x++) {
                System.out.print("+");
                for (int i = 0; i < cellWidth; i++)
                    System.out.print("-");
            }
            System.out.println("+");
        }
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
