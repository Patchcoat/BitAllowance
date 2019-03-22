package com.bitallowance;

import java.util.Date;

public interface ListItem {
    String getName();
    String getCardPrimaryDetails();
    String getCardSecondaryDetails();

    float getSortableValue();
    Date getSortableDate();

    ListItemType getType();
    boolean applyTransaction(ListItem item);
}
