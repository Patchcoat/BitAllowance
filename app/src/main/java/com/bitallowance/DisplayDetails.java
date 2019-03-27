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
import android.widget.AdapterView;
import android.widget.TextView;
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

/**
 * This class displays the details of a task. Providing the name of the task, the value of the task,
 * the expiration, and to whom the task is assigned.
 * @version 1.0
 * @since 03/18/2019
 */
public class DisplayDetails extends AppCompatActivity implements ListItemClickListener,
        AdapterView.OnItemSelectedListener, ListItemSelectDialog.NestedListItemClickListener {


    private static final String TAG = "BADDS-DisplayDetails";
    private ListItem _currentItem;
    private ListItemType _itemType;
    private int _index;

    //For the recycler View
    private List<ListItem> _assignedItems = new ArrayList<>();
    private List<ListItem> _entityListUnassigned = new ArrayList<>();


    private ListItemRecycleViewAdapter _recycleViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_details);
        _index = getIntent().getIntExtra("INDEX", 0);
        _itemType = (ListItemType)getIntent().getSerializableExtra("TYPE");

        //Account for possible null values
        if (_itemType == null)
            _itemType = ListItemType.ENTITY;

        /* * * * * IF EXISTING TRANSACTION * * * * */
        if ((_index >= 0 && _index < Reserve.getListItems(_itemType).size())){

            //Load current transaction with existing Transaction
            _currentItem = Reserve.getListItems(_itemType).get(_index);

            //Display transaction values
            if (_itemType != ListItemType.ENTITY) {
                updateTextFieldsTransaction();
            }
            else {
                updateTextFieldsEntity();

            }

            _assignedItems.addAll(_currentItem.getAssignmentList());


        } else {
            Log.e(TAG, "onCreate: Non-Existent object received");
            Toast toast = makeText(this, "There was an error...", Toast.LENGTH_SHORT);
            toast.show();
            //If item doesn't exist, return.
            finish();
        }

        // setUpSpinners(isExisting);
        // updateLabels(isExisting);

        /* * * * * SET UP RECYCLER-VIEW * * * * */
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.displayDetails_Recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        _recycleViewAdapter = new ListItemRecycleViewAdapter
                (this, this, _assignedItems,
                        ListItemRecycleViewAdapter.CardType.Simple);

        recyclerView.setAdapter(_recycleViewAdapter);
    }

    /**
     * This class dynamically updates the labels for the activity allowing us to use the same
     * activity for TASKS, FINES and REWARDS
     * @param isEditing - Are we editing or adding a transaction
     */
    private void updateLabels(boolean isEditing){

        String strLblTitle = "Display Details ";
        String strLblName;
        String strLblValue;

        switch (_itemType){
            case FINE:
                strLblTitle += "Fine";
                strLblName = "Fine Name: ";
                strLblValue = "Fine Cost: ";
                break;
            case REWARD:
                strLblTitle += "Reward";
                strLblName = "Reward Name";
                strLblValue = "Reward Cost";
                break;
            default:
                strLblTitle += "Task";
                strLblName = "Task Name:";
                strLblValue = "Task Value: ";
                break;
        }
        /* * * * * GET ONSCREEN ELEMENTS * * * * */
        TextView lblTitle = (TextView) findViewById(R.id.displayDetails_lblTitle);
        TextView lblName  = (TextView) findViewById(R.id.displayDetails_lblName);
        TextView lblValue = (TextView) findViewById(R.id.displayDetails_lblValue);

        lblTitle.setText(strLblTitle);
        lblName.setText(strLblName);
        lblValue.setText(strLblValue);

    }
    /**
     * This class updates the labels for displaying Transaction details.
     */
    private void updateTextFieldsTransaction(){
        Transaction transaction = (Transaction) _currentItem;
        //SET VALUES OF TEXT FIELDS
        TextView txtName  = (TextView) findViewById(R.id.displayDetails_txtName);
        TextView txtValue = (TextView) findViewById(R.id.displayDetails_txtValue);
        TextView txtDesc  = (TextView) findViewById(R.id.displayDetails_txtDesc);

        // _textDisplayDetails = (TextView)  findViewById(R.id.displayDetails_textDisplayDetails);
        TextView _textCoolDown       = (TextView)  findViewById(R.id.displayDetails_textCoolDown);
        TextView _textRepeatable     = (TextView)  findViewById(R.id.displayDetails_textRepeat);
        TextView _textExpires        = (TextView)  findViewById(R.id.displayDetails_textExpires);

        txtName.setText(transaction.getName());
        txtDesc.setText(transaction.getMemo());
        txtValue.setText(transaction.getValue().toString());

        // Populates the coolDown data
        switch (transaction.getCoolDown()) {
            case 0:
                _textCoolDown.setText("Immediately");
                break;
            case 1:
                _textCoolDown.setText("Hourly");
                break;
            case 24:
                _textCoolDown.setText("Daily");
                break;
            case 168:
                _textCoolDown.setText("Weekly");
                break;
            default:
                _textCoolDown.setText("Not specified");
                break;
        }

        // Populates whether is repeatable
        if (transaction.isRepeatable()){
            _textRepeatable.setText("Not-Repeatable");
        } else {
            _textRepeatable.setText("Repeatable");
        }

        // Populates Expiration Data
        if (!transaction.isExpirable()){
            _textExpires.setText("Does Not Expire");
        } else {
            _textExpires.setText(transaction.getExpirationDate().toString());
        }
    }

    /**
     * This class updates the labels for displaying Entity details.
     */
    private void updateTextFieldsEntity(){
        Entity entity = (Entity) _currentItem;
        //SET VALUES OF TEXT FIELDS
        TextView txtName  = (TextView) findViewById(R.id.displayDetails_txtName);
        TextView txtValue = (TextView) findViewById(R.id.displayDetails_txtValue);
        TextView txtDesc  = (TextView) findViewById(R.id.displayDetails_txtDesc);

        // _textDisplayDetails = (TextView)  findViewById(R.id.displayDetails_textDisplayDetails);
        TextView _textCoolDown       = (TextView)  findViewById(R.id.displayDetails_textCoolDown);
        TextView _textRepeatable     = (TextView)  findViewById(R.id.displayDetails_textRepeat);
        TextView _textExpires        = (TextView)  findViewById(R.id.displayDetails_textExpires);

        txtName.setText(entity.getName());
        txtDesc.setText(entity.getEmail());
        txtValue.setText(entity.getCardPrimaryDetails());


        _textExpires.setText(entity.getBirthday().toString());

    }

    /**
     * This class provides setup for Activity Spinners.
     * Defines Spinners and Array Adapters for Spinner member variables
     * Sets the adapters and listeners for various spinners.
     * @author Doug Barlow
     * @param existingTransaction - indicates whether is Adding or Editing
     */
    private void setUpSpinners(boolean existingTransaction){

    }


    /**
     * This creates a DatePickerFragment and displays it.
     * @param v
     */
    public void showDatePickerDialog(View v) {

    }

    /**
     * Saves the current  Transaction based on the current inputs.
     * Basic Error checking for EditText items. Spinners are programmed
     * in such a way it should be impossible for them to be in an invalid state.
     * @param view
     */
    public void editTransaction(View view) {

    }

    /**
     * This class allows the user to move to the Next Transaction (of the same Transaction Type)
     * in the list.
     * @param view
     */
    public void editNextTransaction(View view) {
        Intent intent = new Intent(this, DisplayDetails.class);
        int newIndex;

        if (_index == (Reserve.getListItems(_itemType).size() - 1))
            newIndex = 0;
        else
            newIndex = _index + 1;

        //Pass the transaction type in case it's a new transaction
        intent.putExtra("TYPE", _itemType);
        //Pass the index (New transaction will be transactionList().size() + 1)
        intent.putExtra("INDEX", newIndex);

        startActivity(intent);

        //When they finally click "Done" it will finish() regardless of how many transactions they've edited
        finish();
    }

    /**
     * This class allows the user to move to the Previous Transaction (of the same Transaction Type)
     * in the list.
     * @param view
     */
    public void editPrevTransaction(View view) {
        Intent intent = new Intent(this, DisplayDetails.class);
        int newIndex;

        if (_index == 0)
            newIndex = Reserve.getListItems(_itemType).size() -1;
        else
            newIndex = _index - 1;

        //Pass the transaction type in case it's a new transaction
        intent.putExtra("TYPE", _itemType);
        //Pass the index (New transaction will be -1)
        intent.putExtra("INDEX", newIndex);

        startActivity(intent);

        //When they finally click "Done" it will finish() regardless of how many transactions they've edited
        finish();
    }

    /**
     * Send the user back to the calling Activity
     * @param view
     */
    public void finish(View view) {
        finish();
    }

    /**
     * Original Code before Onclick modifications
    @Override
    public void onRecyclerViewItemClick(int position, ListItemRecycleViewAdapter adapter) {
        Toast toast = makeText(getApplicationContext(),
                "Selected " + _assignedItems.get(position).getName(), Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onListItemDialogClick(int position, ListItem selectedItem) {

    }
    */

    /**
     * End of file before changes implemented 3.25.2019 for on item select
     */

    /**
     * Callback function for ListItemClickListener. Handles onclick events for recycler view items.
     * @param position The index of the selected item.
     * @param adapter The recycleView adapter that registered the onClick event. Useful for activities with
     *                multiple recycleViews.
     */
    @Override
    public void onRecyclerViewItemClick(int position, ListItemRecycleViewAdapter adapter) {

        List<ListItem> currentList;
        currentList = _assignedItems;

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
            intent.putExtra("ENTITY_INDEX", _assignedItems.indexOf(selectedItem));
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
                _assignedItems.remove(selectedItem);
                // updateAdapter(selectedItem.getType());
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
        //updateAdapter(ENTITY);
        //updateAdapter(TASK);
        //updateAdapter(REWARD);
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
                    //updateAdapter(ENTITY);
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

