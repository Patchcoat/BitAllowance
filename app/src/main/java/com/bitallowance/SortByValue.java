package com.bitallowance;

import java.util.Comparator;

public class SortByValue implements Comparator<ListItem> {
    @Override
    public int compare(ListItem o1, ListItem o2) {
        if (o1.getSortableValue() > o2.getSortableValue())
            return 1;
        else
            return -1;
    }
}
