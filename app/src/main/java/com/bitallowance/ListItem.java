package com.bitallowance;

public interface ListItem {
    String getName();
    String getCardPrimaryDetails();
    String getCardSecondaryDetails();

    float getSortableValue();
}
