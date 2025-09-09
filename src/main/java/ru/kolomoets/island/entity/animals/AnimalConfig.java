package ru.kolomoets.island.entity.animals;

public enum AnimalConfig {

    BOAR(400, 50, 2, 50, 2, 0.5, 0.1, "🐗"),
    BUFFALO(700, 10, 3, 100, 1, 0.5, 0.1, "🐃"),
    CATERPILLAR(0.01, 1000, 0, 0.01, 0, 1.0, 0.1, "🐛"),
    DEER(300, 20, 4, 50, 1, 0.5, 0.1, "🦌"),
    DUCK(1, 200, 4, 0.15, 4, 0.9, 0.2, "🦆"),
    GOAT(60, 140, 3, 10, 1, 0.5, 0.1, "🐐"),
    HORSE(400, 20, 4, 60, 1, 0.5, 0.1, "🐎"),
    MOUSE(0.05, 500, 1, 0.01, 3, 0.5, 0.2, "🐁"),
    RABBIT(2.0, 150, 2, 0.45, 4, 0.5, 0.1, "🐇"),
    SHEEP(70, 140, 3, 15, 1, 0.5, 0.2, "🐑"),
    BEAR(500, 5, 2, 80, 1, 0.5, 0.15, "🐻"),
    EAGLE(6, 20, 3, 1, 2, 0.55, 0.15, "🦅"),
    FOX(8, 30, 2, 2, 3, 0.5, 0.15, "🦊"),
    PYTHON(15, 30, 1, 3, 2, 0.5, 0.15, "🐍"),
    WOLF(50, 30, 3, 8, 3, 0.45, 0.15, "🐺");

    private final double weight;
    private final int maxInCell;
    private final int maxSpeed;
    private final double foodAmount;
    private final int offspringCount;
    private final double initialSatietyRate;
    private final double satietyDecayRate;
    private final String symbol;

    AnimalConfig(double weight,
                 int maxInCell,
                 int maxSpeed,
                 double foodAmount,
                 int offspringCount,
                 double initialSatietyRate,
                 double satietyDecayRate,
                 String symbol) {
        this.weight = weight;
        this.maxInCell = maxInCell;
        this.maxSpeed = maxSpeed;
        this.foodAmount = foodAmount;
        this.offspringCount = offspringCount;
        this.initialSatietyRate = initialSatietyRate;
        this.satietyDecayRate = satietyDecayRate;
        this.symbol = symbol;
    }

    public double getWeight() {
        return weight;
    }

    public int getMaxInCell() {
        return maxInCell;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public double getFoodAmount() {
        return foodAmount;
    }

    public int getOffspringCount() {
        return offspringCount;
    }

    public double getInitialSatietyRate() {
        return initialSatietyRate;
    }

    public double getSatietyDecayRate() {
        return satietyDecayRate;
    }

    public String getSymbol() {
        return symbol;
    }
}
