package ru.kolomoets.island.location;

public class SimulationConfig {

    private final int height;
    private final int width;
    private final int tickMillis;
    private final int ticks;

    private SimulationConfig(int height, int width, int tickMillis, int ticks) {
        this.height = height;
        this.width = width;
        this.tickMillis = tickMillis;
        this.ticks = ticks;
    }

    public static SimulationConfig create() {
        return new SimulationConfig(10, 20, 1000, 10);
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getTickMillis() {
        return tickMillis;
    }

    public int getTicks() {
        return ticks;
    }
}
