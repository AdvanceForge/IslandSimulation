package ru.kolomoets.island.location;

import ru.kolomoets.island.entity.IslandObjects;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Cell {

    private final Map<Class<? extends IslandObjects>, List<IslandObjects>> residents = new ConcurrentHashMap<>();

    public boolean addObject(IslandObjects o) {
        List<IslandObjects> list = residents.get(o.getClass());
        if (list == null) {
            list = new CopyOnWriteArrayList<>();
            residents.put(o.getClass(), list);
        }
        if (list.size() >= o.getMaxInCell()) return false;
        list.add(o);
        return true;
    }

    public boolean removeObject(IslandObjects o) {
        List<IslandObjects> list = residents.get(o.getClass());
        if (list == null) return false;
        boolean removed = list.remove(o);
        if (list.isEmpty())
            residents.remove(o.getClass());
        return removed;
    }

    public <T extends IslandObjects> List<T> getObjectsByType(Class<T> type) {
        List<IslandObjects> list = residents.getOrDefault(type, Collections.emptyList());
        return (List<T>) list;
    }

    public Collection<IslandObjects> getAllObjects() {
        List<IslandObjects> all = new ArrayList<>();
        for (List<IslandObjects> sublist : residents.values()) {
            all.addAll(sublist);
        }
        return all;
    }
}
