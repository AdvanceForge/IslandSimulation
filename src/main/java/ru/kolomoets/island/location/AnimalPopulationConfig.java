package ru.kolomoets.island.location;

import ru.kolomoets.island.entity.animals.AbstractAnimal;
import ru.kolomoets.island.entity.animals.herbivores.*;
import ru.kolomoets.island.entity.animals.predators.*;

import java.util.Map;

public class AnimalPopulationConfig {

    public static final int ratioWolf = 1;
    public static final int ratioPython = 3;
    public static final int ratioFox = 3;
    public static final int ratioBear = 1;
    public static final int ratioEagle = 2;

    public static final int ratioHorse = 2;
    public static final int ratioDeer = 3;
    public static final int ratioRabbit = 6;
    public static final int ratioMouse = 8;
    public static final int ratioGoat = 3;
    public static final int ratioSheep = 3;
    public static final int ratioBoar = 3;
    public static final int ratioBuffalo = 3;
    public static final int ratioDuck = 4;
    public static final int ratioCaterpillar = 35;

    public static Map<Class<? extends AbstractAnimal>, Integer> createInitialPopulation(int width, int height) {
        int area = width * height;
        return Map.ofEntries(
                Map.entry(Horse.class, area * ratioHorse),
                Map.entry(Deer.class, area * ratioDeer),
                Map.entry(Rabbit.class, area * ratioRabbit),
                Map.entry(Mouse.class, area * ratioMouse),
                Map.entry(Goat.class,area * ratioGoat),
                Map.entry(Sheep.class, area * ratioSheep),
                Map.entry(Boar.class, area * ratioBoar),
                Map.entry(Buffalo.class, area * ratioBuffalo),
                Map.entry(Duck.class, area * ratioDuck),
                Map.entry(Caterpillar.class, area * ratioCaterpillar),

                Map.entry(Wolf.class, area * ratioWolf),
                Map.entry(Python.class, area * ratioPython),
                Map.entry(Fox.class, area * ratioFox),
                Map.entry(Bear.class, area * ratioBear),
                Map.entry(Eagle.class, area * ratioEagle)
        );
    }
}
