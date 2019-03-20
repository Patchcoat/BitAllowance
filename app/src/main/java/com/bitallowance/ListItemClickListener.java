package com.bitallowance;

import java.util.List;

/**
 * Interface for passing onClick event back to parent Activity.
 * Parent activity must implement this interface.
 */
public interface ListItemClickListener {
    void onRecyclerViewItemClick(int position, ListItemRecycleViewAdapter adapter);
    void onListItemDialogClick(int position, ListItem selectedItem);
}