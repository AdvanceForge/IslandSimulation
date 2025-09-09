package ru.kolomoets.island.entity.plants;

public enum PlantConfig {

    GRASS(1, 200, "🌿");

    private final double weight;
    private final int maxInCell;
    private final String symbol;

    PlantConfig(double weight, int maxInCell, String symbol) {
        this.weight = weight;
        this.maxInCell = maxInCell;
        this.symbol = symbol;
    }

    public double getWeight() {
        return weight;
    }

    public int getMaxInCell() {
        return maxInCell;
    }

    public String getSymbol() {
        return symbol;
    }
}
