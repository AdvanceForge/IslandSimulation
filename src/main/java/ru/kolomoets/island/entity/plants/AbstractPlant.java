package ru.kolomoets.island.entity.plants;

import ru.kolomoets.island.entity.IslandObjects;

public abstract class AbstractPlant implements IslandObjects {

    protected final double weight;
    protected final int maxInCell;
    protected final String symbol;

    public AbstractPlant(PlantConfig config) {
        this.weight = config.getWeight();
        this.maxInCell = config.getMaxInCell();
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
}
