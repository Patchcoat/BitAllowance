package com.bitallowance;

import java.util.Comparator;

public class SortByDate implements Comparator<ListItem> {

    @Override
    public int compare(ListItem o1, ListItem o2) {
        if (o1.getSortableDate() == null)
            return 1;
        else if(o2.getSortableDate() == null)
            return -1;
        else if (o1.getSortableDate().getTime() > o2.getSortableDate().getTime())
            return 1;
        else
            return -1;
    }
}
