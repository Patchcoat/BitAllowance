package com.bitallowance;

import java.util.Comparator;

public class SortByDate implements Comparator<ListItem> {

    @Override
    public int compare(ListItem o1, ListItem o2) {
            return o1.getSortableDate().compareTo(o2.getSortableDate());
    }
}
