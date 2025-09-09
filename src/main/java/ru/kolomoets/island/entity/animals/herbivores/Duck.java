package ru.kolomoets.island.entity.animals.herbivores;

import ru.kolomoets.island.entity.animals.AbstractAnimal;
import ru.kolomoets.island.entity.animals.AnimalConfig;

import java.util.concurrent.ThreadLocalRandom;

public class Duck extends AbstractAnimal {
    public Duck() {
        super(AnimalConfig.DUCK);
    }

    @Override
    protected void eat() {
        super.eat();
    }

    @Override
    protected void move() {
        super.move();
    }

    @Override
    protected void reproduce() {

        if (satiety < foodAmount * 0.9) return;

        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        if (rnd.nextDouble() > 0.26) return;

        var sameSpecies = cell.getObjectsByType(this.getClass());
        if (sameSpecies.size() < 2) return;

        int current = sameSpecies.size();
        int limit = getMaxInCell();
        int canAdd = Math.min(offspringCount, limit - current);

        for (int i = 0; i < canAdd; i++) {
            try {
                AbstractAnimal child = this.getClass().getDeclaredConstructor().newInstance();
                island.spawnAt(child, col, row);
            } catch (Exception e) {
                throw new RuntimeException("Ошибка при создании потомка: " + this.getClass(), e);
            }
        }
    }
}
