package ru.kolomoets.island.entity.animals;

import ru.kolomoets.island.entity.IslandObjects;
import ru.kolomoets.island.entity.plants.Grass;
import ru.kolomoets.island.entity.probabilities.ChanceTable;
import ru.kolomoets.island.location.Cell;
import ru.kolomoets.island.location.Island;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public abstract class AbstractAnimal implements IslandObjects {

    protected final double weight;
    protected final int maxInCell;
    protected final int maxSpeed;
    protected final double foodAmount;
    protected final int offspringCount;
    protected double satiety;
    protected final double satietyDecayPerTick;
    protected final String symbol;

    protected volatile boolean alive = true;

    protected Cell cell;
    protected Island island;
    protected volatile int row;
    protected volatile int col;

    public AbstractAnimal(AnimalConfig config) {
        this.weight = config.getWeight();
        this.maxInCell = config.getMaxInCell();
        this.maxSpeed = config.getMaxSpeed();
        this.foodAmount = config.getFoodAmount();
        this.offspringCount = config.getOffspringCount();
        this.satiety = this.foodAmount * config.getInitialSatietyRate();
        this.satietyDecayPerTick = this.foodAmount * config.getSatietyDecayRate();
        this.symbol = config.getSymbol();
    }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public int getMaxInCell() {
        return maxInCell;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    protected void eat() {
//        System.out.println(getClass().getSimpleName() + " eat");

        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        var objects = new ArrayList<>(cell.getAllObjects());

        for (IslandObjects target : objects) {
            if (target == this) continue;

            int chance = ChanceTable.chance(this.getClass(), target.getClass());
            if (chance > 0 && rnd.nextInt(100) < chance) {

                if (target instanceof AbstractAnimal prey) {
                    cell.removeObject(prey);
                    gainFood(prey.getWeight());
                    prey.setDead();
                    break;
                } else if (target instanceof Grass) {
                    gainFood(target.getWeight());
                    cell.removeObject(target);
                    if (satiety >= foodAmount) break;
                }
            }
        }
    }

    protected void move() {
//        System.out.println(getClass().getSimpleName() + " move");

        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        int dx = rnd.nextInt(-maxSpeed, maxSpeed + 1);
        int dy = rnd.nextInt(-maxSpeed, maxSpeed + 1);

        if (dx == 0 && dy == 0) return;

        int newX = col + dx;
        int newY = row + dy;

        island.tryMove(this, col, row, newX, newY);
    }

    protected abstract void reproduce();
//        System.out.println(getClass().getSimpleName() + " repr");
//
//        if (satiety < foodAmount * 0.8) return;
//
//        ThreadLocalRandom rnd = ThreadLocalRandom.current();
//        if (rnd.nextDouble() > 0.3) return;
//
//        var sameSpecies = cell.getObjectsByType(this.getClass());
//        if (sameSpecies.size() < 2) return;
//
//        int current = sameSpecies.size();
//        int limit = getMaxInCell();
//        int canAdd = Math.min(offspringCount, limit - current);
//
//        for (int i = 0; i < canAdd; i++) {
//            try {
//                AbstractAnimal child = this.getClass().getDeclaredConstructor().newInstance();
//                island.spawnAt(child, col, row);
//            } catch (Exception e) {
//                throw new RuntimeException("Ошибка при создании потомка: " + this.getClass(), e);
//            }
//        }


    public void gainFood(double amount) {
        satiety = Math.min(foodAmount, satiety + amount);
    }

    public void decreaseSatietyAndCheckAlive() {
        satiety -= satietyDecayPerTick;
        if (satiety <= 0) {
            alive = false;
        }
    }

    public void lifeCycle() {
//        System.out.println("CYCLE START");
        if (!alive) return;
        eat();
        reproduce();
        move();
        decreaseSatietyAndCheckAlive();
    }

    public boolean isAlive() {
        return alive;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public double getFoodAmount() {
        return foodAmount;
    }

    public double getSatiety() {
        return satiety;
    }

    public void onSpawn(Island island, int x, int y) {
        this.island = island;
        this.col = x;
        this.row = y;
        this.cell = island.getCell(x, y);
    }

    public void onMovedTo(int newX, int newY, Cell newCell) {
        this.col = newX;
        this.row = newY;
        this.cell = newCell;
    }

    public void setDead() {
        this.alive = false;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
