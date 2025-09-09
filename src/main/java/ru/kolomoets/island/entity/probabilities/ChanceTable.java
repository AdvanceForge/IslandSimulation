package ru.kolomoets.island.entity.probabilities;

import ru.kolomoets.island.entity.IslandObjects;
import ru.kolomoets.island.entity.animals.AbstractAnimal;
import ru.kolomoets.island.entity.animals.herbivores.*;
import ru.kolomoets.island.entity.animals.predators.*;
import ru.kolomoets.island.entity.plants.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ChanceTable {

    private static final Set<Class<? extends AbstractPlant>> PLANTS = Set.of(Grass.class);

    private static final Set<Class<? extends AbstractAnimal>> EATS_PLANTS = Set.of(
            Horse.class, Deer.class, Rabbit.class, Mouse.class,
            Goat.class, Sheep.class, Boar.class, Buffalo.class,
            Duck.class, Caterpillar.class);

    private static final Map<Class<? extends AbstractAnimal>, Map<Class<? extends IslandObjects>, Integer>> PREDATOR_TO_PREY =
            predatorToPreyTable();

    private ChanceTable() {
    }

    public static int chance(Class<? extends AbstractAnimal> predatorClass,
                             Class<? extends IslandObjects> preyClass) {
        if (PLANTS.contains(preyClass)) {
            return EATS_PLANTS.contains(predatorClass) ? 100 : 0;
        }
        Map<Class<? extends IslandObjects>, Integer> preyChanceRow = PREDATOR_TO_PREY.get(predatorClass);
        return (preyChanceRow == null) ? 0 : preyChanceRow.getOrDefault(preyClass, 0);
    }


    private static Map<Class<? extends AbstractAnimal>, Map<Class<? extends IslandObjects>, Integer>> predatorToPreyTable() {

        Map<Class<? extends AbstractAnimal>, Map<Class<? extends IslandObjects>, Integer>> predatorToPrey = new HashMap<>();

        putPredatorRow(predatorToPrey, Wolf.class, Map.of(
                Horse.class, 10, Deer.class, 15, Rabbit.class, 60, Mouse.class, 80,
                Goat.class, 60, Sheep.class, 70, Boar.class, 15, Buffalo.class, 10, Duck.class, 40));

        putPredatorRow(predatorToPrey, Python.class, Map.of(
                Fox.class, 15, Rabbit.class, 20, Mouse.class, 40, Duck.class, 10));

        putPredatorRow(predatorToPrey, Fox.class, Map.of(
                Rabbit.class, 70, Mouse.class, 90, Duck.class, 60, Caterpillar.class, 40));

        putPredatorRow(predatorToPrey, Bear.class, Map.of(
                Python.class, 80, Horse.class, 40, Deer.class, 80, Rabbit.class, 80, Mouse.class, 90,
                Goat.class, 70, Sheep.class, 70, Boar.class, 50, Buffalo.class, 20, Duck.class, 10));

        putPredatorRow(predatorToPrey, Eagle.class, Map.of(
                Fox.class, 10, Rabbit.class, 90, Mouse.class, 90, Duck.class, 80));

        putPredatorRow(predatorToPrey, Mouse.class, Map.of(
                Caterpillar.class, 90));

        putPredatorRow(predatorToPrey, Boar.class, Map.of(
                Mouse.class, 50, Caterpillar.class, 90));

        putPredatorRow(predatorToPrey, Duck.class, Map.of(
                Caterpillar.class, 90));

        return Collections.unmodifiableMap(predatorToPrey);
    }

    private static void putPredatorRow(
            Map<Class<? extends AbstractAnimal>, Map<Class<? extends IslandObjects>, Integer>> predatorToPrey,
            Class<? extends AbstractAnimal> predatorType,
            Map<Class<? extends IslandObjects>, Integer> preyChanceRow
    ) {
        predatorToPrey.put(predatorType, Map.copyOf(preyChanceRow));
    }

}
