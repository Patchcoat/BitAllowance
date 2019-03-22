package com.bitallowance;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.widget.Toast.makeText;
import static com.bitallowance.ListItemType.ALL;
import static com.bitallowance.ListItemType.ENTITY;
import static com.bitallowance.ListItemType.FINE;
import static com.bitallowance.ListItemType.REWARD;
import static com.bitallowance.ListItemType.TASK;

public class ReserveHome extends AppCompatActivity implements ListItemClickListener, AdapterView.OnItemSelectedListener,
 ListItemSelectDialog.NestedListItemClickListener {

    private static final String TAG = "BADDS-ReserveHome";

    //RecyclerView variables
    private RecyclerView entityView;
    private RecyclerView taskView;
    private RecyclerView rewardView;

    private RecyclerView.Adapter mEntityList;
    private RecyclerView.Adapter mTaskList;
    private RecyclerView.Adapter mRewardList;

    private RecyclerView.LayoutManager entityLayoutManager;
    private RecyclerView.LayoutManager taskLayoutManager;
    private RecyclerView.LayoutManager rewardLayoutManager;

    private List<ListItem> entityList;
    private List<ListItem> taskList;
    private List<ListItem> rewardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_home);
        entityList = new ArrayList();
        taskList = new ArrayList();
        rewardList = new ArrayList();

        entityList.addAll(Reserve.getListItems(ListItemType.ENTITY));
        taskList.addAll(Reserve.getListItems(ListItemType.TASK));
        rewardList.addAll(Reserve.getListItems(ListItemType.REWARD));


        //The RecyclerView
        entityView = findViewById(R.id.entityView);
        taskView = findViewById(R.id.taskView);
        rewardView = findViewById(R.id.rewardView);

        //Layout Manager
        entityLayoutManager = new LinearLayoutManager(this);
        taskLayoutManager = new LinearLayoutManager(this);
        rewardLayoutManager = new LinearLayoutManager(this);

        entityView.setLayoutManager(entityLayoutManager);
        taskView.setLayoutManager(taskLayoutManager);
        rewardView.setLayoutManager(rewardLayoutManager);

        //Adapter
        mEntityList = new ListItemRecycleViewAdapter(this, this, entityList, ListItemRecycleViewAdapter.CardType.Normal);
        entityView.setAdapter(mEntityList);
        mTaskList = new ListItemRecycleViewAdapter(this, this, taskList, ListItemRecycleViewAdapter.CardType.Normal);
        taskView.setAdapter(mTaskList);
        mRewardList = new ListItemRecycleViewAdapter(this, this, rewardList, ListItemRecycleViewAdapter.CardType.Normal);
        rewardView.setAdapter(mRewardList);
    }

    public void openSettings(View view) {

    }

    public void openEntityList(View view) {

    }

    public void openTaskList(View view) {

    }

    public void openRewardList(View view) {

    }

    public void openGiveReward(View view) {

    }


    private void updateAdapter(ListItemType type) {
        switch (type) {
            case ENTITY:
                entityList.clear();
                entityList.addAll(Reserve.get_entityList());
                mEntityList.notifyDataSetChanged();
                return;
            case TASK:
                taskList.clear();
                taskList.addAll(Reserve.getListItems(TASK));
                mTaskList.notifyDataSetChanged();
                return;
            default:
                rewardList.clear();
                rewardList.addAll(Reserve.getListItems(REWARD));
                mRewardList.notifyDataSetChanged();
                return;
        }
    }

    /**
     * This function identifies the appropriate recycleView adapter to update based on the
     * selected item type.
     * @param item The selected item.
     * @return One of the memberVariable adapters.
     */
    private RecyclerView.Adapter getAdapter(ListItem item){
        switch (item.getType()){
            case ENTITY:
                return mEntityList;
            case TASK:
                return mTaskList;
            default:
                return mRewardList;
        }
    }

    /**
     * This function identifies the appropriate ListItems list to work with based on the selected
     * item.
     * @param item The selected item
     * @return One of the member ListItem lists.
     */
    private List<ListItem> getCurrentList(ListItem item) {
        switch (item.getType()){
            case ENTITY:
                return entityList;
            case TASK:
                return taskList;
            default:
                return rewardList;
        }
    }

    /**
     * Callback function for ListItemClickListener. Handles onclick events for recycler view items.
     * @param position The index of the selected item.
     * @param adapter The recycleView adapter that registered the onClick event. Useful for activities with
     *                multiple recycleViews.
     */
    @Override
    public void onRecyclerViewItemClick(int position, ListItemRecycleViewAdapter adapter) {

        List<ListItem> currentList;
        if(adapter == mEntityList){
            currentList = entityList;
        } else if (adapter == mTaskList) {
            currentList = taskList;
        } else {
            currentList = rewardList;
        }

        /* * * * * EXAMPLE OF HOW TO IMPLEMENT A LIST_ITEM_SELECT_DIALOG * * * * */
        //Declare new Fragment Manager & ListItemSelectDialog
        FragmentManager fragmentManager = getSupportFragmentManager();
        ListItemSelectDialog selectDialog = new ListItemSelectDialog();

        //Create a bundle to hold title & menu options
        Bundle bundle = new Bundle();
        bundle.putString("TITLE", currentList.get(position).getName());

        //get selected item type to display different options depending on type.
        ListItemType clickType = currentList.get(position).getType();

        //Dynamically add display options to bundle
        //*Note* Options must be added as a String ArrayList
        switch (clickType){
            case ENTITY:
                bundle.putStringArrayList("OPTIONS", new ArrayList<>(Arrays.asList("View Details", "Apply Payment",
                        "Apply Reward", "Apply Fine", "Edit " + currentList.get(position).getName(),
                        "Delete "+ currentList.get(position).getName(), "Cancel")));
                break;
            case TASK:
                bundle.putStringArrayList("OPTIONS", new ArrayList<>(Arrays.asList("View Details", "Apply Payment",
                        "Edit " + currentList.get(position).getName(), "Delete "+ currentList.get(position).getName(), "Cancel")));
                break;
            case REWARD:
                bundle.putStringArrayList("OPTIONS", new ArrayList<>(Arrays.asList("View Details", "Apply Reward",
                        "Edit " + currentList.get(position).getName(), "Delete "+ currentList.get(position).getName(), "Cancel")));
                break;

            case FINE:
                bundle.putStringArrayList("OPTIONS", new ArrayList<>(Arrays.asList("View Details", "Apply Fine",
                        "Edit " + currentList.get(position).getName(), "Delete "+ currentList.get(position).getName(), "Cancel")));
                break;

        }

        //This is required
        /* * * * INITIALIZE & SET DIALOG ARGUMENTS DIALOG (2 - STEPS) * * * */
        //Step 1 - INITIALIZE - pass selected item and a listener. - This is required for onClick event to work.
        selectDialog.initialize(currentList.get(position), this);
        //Step 2 - SET ARGUMENTS - pass bundle to dialog with title and options to display
        selectDialog.setArguments(bundle);

        //Show the dialog
        selectDialog.show(fragmentManager, "");
    }

    /**
     * Handles onClick events for ListItem Dialogs
     * @param position Designates option selected (See "OPTIONS" array passed to dialog)
     * @param selectedItem Item to be affected.
     */
    @Override
    public void onListItemDialogClick(int position, final ListItem selectedItem) {

        if (selectedItem != null){

            if (selectedItem.getType() == ListItemType.ENTITY){
                switch (position) {
                    case 0:
                        displayDetails(selectedItem);
                        break;
                    case 1:
                        getItemsToApply(selectedItem, TASK);
                        break;
                    case 2:
                        getItemsToApply(selectedItem, REWARD);
                        break;
                    case 3:
                        getItemsToApply(selectedItem, FINE);
                        break;
                    case 4:
                        editItem(selectedItem);
                        break;
                    case 5:
                        confirmDelete(selectedItem);
                        break;
                }
            }
            else{
                switch (position) {
                    case 0:
                        displayDetails(selectedItem);
                        break;
                    case 1:
                        getItemsToApply(selectedItem, ENTITY);
                        break;
                    case 2:
                        editItem(selectedItem);
                        break;
                    case 3:
                        confirmDelete(selectedItem);
                        break;
                }
            }
        }
    }

    /**
     * Opens a new activity to allow the selected item to be edited
     * @param selectedItem the item to be edited
     */
    private void editItem(ListItem selectedItem){
        Intent intent;
        if (selectedItem.getType() == ENTITY) {
            intent = new Intent(this, EditAddEntity.class);
            intent.putExtra("ENTITY_INDEX", getCurrentList(selectedItem).indexOf(selectedItem));
        } else {
            intent = new Intent(this, EditAddTransaction.class);
            intent.putExtra("TRANSACTION_INDEX", Reserve.get_transactionList().indexOf(selectedItem));
            intent.putExtra("TRANSACTION_TYPE", selectedItem.getType());
        }
        startActivityForResult(intent,1);
    }

    /**
     * Opens a new activity that displays the details of the passed item.
     * @param selectedItem the item to be displayed
     */
    private void displayDetails(ListItem selectedItem){
        Intent intent = new Intent(this, DisplayDetails.class);
        intent.putExtra("INDEX",Reserve.getListItems(selectedItem.getType()).indexOf(selectedItem));
        intent.putExtra("TYPE", selectedItem.getType());
        startActivityForResult(intent,1);
    }

    /**
     * Prompts the user to confirm they want to delete the selected item. If the user confirms,
     * it deletes the object and updates the recyclerView
     * @param selectedItem the object to be deleted.
     */
    private void  confirmDelete(final ListItem selectedItem){
        //Confirm the user wants to delete the record
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete " + selectedItem.getName() + "?").setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedItem.delete();
                getCurrentList(selectedItem).remove(selectedItem);
                updateAdapter(selectedItem.getType());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        updateAdapter(ENTITY);
        updateAdapter(TASK);
        updateAdapter(REWARD);
    }

    /**
     * Opens another dialogue fragment with a list of ListItems that can be applied to the
     * current selection.
     * @param item Currently selected item
     * @param typeToApply
     */
    private void getItemsToApply(ListItem item, ListItemType typeToApply){
        //Declare new Fragment Manager & ListItemSelectDialog
        FragmentManager fragmentManager = getSupportFragmentManager();
        ListItemSelectDialog selectDialog = new ListItemSelectDialog();

        //Create a bundle to hold title & menu options
        Bundle bundle = new Bundle();

        //Dynamically add display options to bundle
        //*Note* Options must be added as a String ArrayList

        switch (typeToApply){
            case ENTITY:
                bundle.putString("TITLE", "Who do you want to apply " + item.getName() + "to?");
                break;
            case TASK:
                bundle.putString("TITLE", "Which task would you like to apply to " + item.getName() + "?");
                break;
            case REWARD:
                bundle.putString("TITLE", "Which reward would you like to apply to " + item.getName() + "?");
                break;
            case FINE:
                bundle.putString("TITLE", "Which fine would you like to apply to " + item.getName() + "?");
                break;
        }

        //Get a dynamic list of items that can be applied
        ArrayList options = new ArrayList<>();

        if (typeToApply == ENTITY){
            Transaction tempTransaction = (Transaction) item;
            for (Entity entity : Reserve.get_entityList()){

                //Only add assigned entities to the options list.
                if (tempTransaction.isAssigned(entity)) {
                    options.add(entity.getName());
                }
                //Make sure the number of options is greater than 0
                if(options.size() == 0){
                    Toast toast = makeText(this, "Uh-oh. There is no-one assigned to that transaction.", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
            }
        } else {
            //Only add assigned transactions to the options list.
            Entity tempEntity = (Entity)item;
            for (Transaction transaction : tempEntity.getAssignedTransactions(typeToApply)) {
                options.add(transaction.getName());
            }
            //Make sure the number of options is greater than 0
            if(options.size() == 0){
                Toast toast = makeText(this, "Uh-oh. This person has no " + typeToApply + "s assigned to them.", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
        }


        options.add("Cancel");
        bundle.putStringArrayList("OPTIONS", options);

        //Initialize a NestedDialogue
        selectDialog.initializeNested(item, this, typeToApply);
        //Step 2 - SET ARGUMENTS - pass bundle to dialog with title and options to display
        selectDialog.setArguments(bundle);

        //Show the dialog
        selectDialog.show(fragmentManager, "");

    }

    /**
     * The callback function for nested ListItem dialogs. This handles onclick events for the nested
     * dialog fragments
     * @param position index of the option selected by the user.
     * @param selectedItem item to be affected
     * @param applyType type of item to be applied to selectedItem.
     */
    @Override
    public void onNestedListItemDialogClick(int position, ListItem selectedItem,ListItemType applyType) {
        Toast toast;
        List<ListItem> options = new ArrayList<>();
        //applyType must list a specific type.
        if (applyType == null || applyType == ALL) {
            toast = makeText(this, "An Error Occurred...", Toast.LENGTH_SHORT);
        }else {
            try {
                //Get the current options list
                if (applyType == ENTITY){
                    Transaction tempTransaction = (Transaction) selectedItem;
                    for (Entity entity : Reserve.get_entityList()){
                        if (tempTransaction.isAssigned(entity)) {
                            options.add(entity);
                        }
                    }
                } else {
                    Entity tempEntity = (Entity) selectedItem;
                    options.addAll(tempEntity.getAssignedTransactions(applyType));
                }
                //This indicates that CANCEL was selected
                if (position == options.size()){
                    return;
                    //An item was selected
                } else if (selectedItem.applyTransaction(options.get(position))) {
                    toast = makeText(this, "Transaction Applied", Toast.LENGTH_SHORT);
                    //Only Entities should visibly change after a transaction has been applied.
                    updateAdapter(ENTITY);
                } else {
                    if (applyType == ENTITY) {
                        toast = makeText(this, Reserve.getListItems(applyType).get(position).getName() +
                                " does not have enough " + Reserve.getCurrencyName() + " for that reward.", Toast.LENGTH_SHORT);
                    } else {
                        toast = makeText(this, selectedItem.getName() + " does not have enough " +
                                Reserve.getCurrencyName() + " for that reward.", Toast.LENGTH_SHORT);
                    }
                }
            } catch (IllegalArgumentException e)
            {
                toast = makeText(this, "Uh-Oh - An unexpected error occurred...", Toast.LENGTH_SHORT);
            }
        }
        toast.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
