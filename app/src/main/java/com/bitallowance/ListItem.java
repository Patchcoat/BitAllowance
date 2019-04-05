package com.bitallowance;

import android.content.Context;

import java.util.Date;
import java.util.List;

public interface ListItem {
    String getName();
    String getCardPrimaryDetails();
    String getCardSecondaryDetails();

    float getSortableValue();
    Date getSortableDate();

    ListItemType getType();
    boolean applyTransaction(ListItem item, Context context);
    List<ListItem> getAssignmentList();

    void update();

    void delete();
}
