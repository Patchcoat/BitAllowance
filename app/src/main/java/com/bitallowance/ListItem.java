package com.bitallowance;

import java.util.Date;
import java.util.List;

public interface ListItem {
    String getName();
    String getCardPrimaryDetails();
    String getCardSecondaryDetails();

    float getSortableValue();
    Date getSortableDate();

    ListItemType getType();
    boolean applyTransaction(ListItem item);
    List<ListItem> getAssignmentList();

    void delete();
}
