package com.bitallowance;

import java.util.Comparator;

public class SortByID implements Comparator<Entity> {
    @Override
    public int compare(Entity o1, Entity o2) {
        if (o1.getId() > o2.getId())
            return 1;
        else
            return -1;
    }
}
