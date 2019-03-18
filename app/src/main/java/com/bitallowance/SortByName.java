package com.bitallowance;

import java.util.Comparator;

public class SortByName implements Comparator<ListItem> {
    @Override
    public int compare(ListItem o1, ListItem o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
