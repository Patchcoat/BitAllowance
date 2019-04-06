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
public class DisplayDetails extends AppCompatActivity implements ListItemClickListener {


    private static final String TAG = "BADDS-DisplayDetails";
    private ListItem _currentItem;
    private ListItemType _itemType;
    private int _index;

    //For the recycler View
    private List<ListItem> _assignedItems = new ArrayList<>();


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

        /* * * * * SET UP RECYCLER-VIEW * * * * */
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.displayDetails_Recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        _recycleViewAdapter = new ListItemRecycleViewAdapter
                (this, this, _assignedItems,
                        ListItemRecycleViewAdapter.CardType.Simple);

        recyclerView.setAdapter(_recycleViewAdapter);

        //Hide history button unless item is an entity
        if (_itemType == ENTITY){
            findViewById(R.id.displayDetails_btnHistory).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.displayDetails_btnHistory).setVisibility(View.GONE);
        }
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
        TextView lblValue = (TextView) findViewById(R.id.displayDetails_lblValue);

        // _textDisplayDetails = (TextView)  findViewById(R.id.displayDetails_textDisplayDetails);
        TextView _textCoolDown       = (TextView)  findViewById(R.id.displayDetails_textCoolDown);
        TextView _textRepeatable     = (TextView)  findViewById(R.id.displayDetails_textRepeat);
        TextView _textExpires        = (TextView)  findViewById(R.id.displayDetails_textExpires);

        txtName.setText(transaction.getName());
        txtDesc.setText(transaction.getMemo());
        txtValue.setText(transaction.getValue().toString());

        if (_itemType != TASK){
            lblValue.setText("Cost:");
        }
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
            _textRepeatable.setText("Repeatable");
        } else {
            _textRepeatable.setText("Not-Repeatable");
        }

        // Populates Expiration Data
        if (!transaction.isExpirable()){
            _textExpires.setText("Does Not Expire");
        } else {
            _textExpires.setText(Reserve.dateToString(transaction.getExpirationDate()));
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
        TextView lblValue = (TextView) findViewById(R.id.displayDetails_lblValue);
        TextView lblBDay  = (TextView) findViewById(R.id.displayDetails_lblExpires);

        // _textDisplayDetails = (TextView)  findViewById(R.id.displayDetails_textDisplayDetails);
        TextView _textCoolDown       = (TextView)  findViewById(R.id.displayDetails_textCoolDown);
        TextView _textRepeatable     = (TextView)  findViewById(R.id.displayDetails_textRepeat);
        TextView _textExpires        = (TextView)  findViewById(R.id.displayDetails_textExpires);

        txtName.setText(entity.getName());
        txtDesc.setText(entity.getEmail());
        txtValue.setText(entity.getCardPrimaryDetails());
        lblValue.setText("Balance:");
        lblBDay.setText("Birthday:");



        _textExpires.setText(Reserve.dateToString(((Entity) _currentItem).getBirthday()));

    }

    /**
     * Allows the user to edit the current item
     * @param view
     */
    public void editListItem(View view) {
        Intent intent;
        if (_currentItem.getType() == ENTITY) {
            intent = new Intent(this, EditAddEntity.class);
            intent.putExtra("ENTITY_INDEX", Reserve.get_entityList().indexOf(_currentItem));
        } else {
            intent = new Intent(this, EditAddTransaction.class);
            intent.putExtra("TRANSACTION_INDEX", Reserve.get_transactionList().indexOf(_currentItem));
            intent.putExtra("TRANSACTION_TYPE", _currentItem.getType());
        }
        startActivityForResult(intent,1);
    }

    /**
     * This class allows the user to move to the Next Transaction (of the same Transaction Type)
     * in the list.
     * @param view
     */
    public void viewNextDetails(View view) {
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
    public void viewPreviousDetails(View view) {
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
                bundle.putStringArrayList("OPTIONS", new ArrayList<>(Arrays.asList("View Details",
                        "Apply Transaction", "Cancel")));
                break;
            case TASK:
                bundle.putStringArrayList("OPTIONS", new ArrayList<>(Arrays.asList("View Details",
                        "Apply Payment", "Cancel")));
                break;
            case REWARD:
                bundle.putStringArrayList("OPTIONS", new ArrayList<>(Arrays.asList("View Details",
                        "Apply Reward", "Cancel")));
                break;

            case FINE:
                bundle.putStringArrayList("OPTIONS", new ArrayList<>(Arrays.asList("View Details",
                        "Apply Fine", "Cancel")));
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

        if (selectedItem != null) {
            switch (position) {
                case 0:
                    displayDetails(selectedItem);
                    break;
                case 1:
                    selectedItem.applyTransaction(_currentItem, this);
                    if (_itemType == ENTITY){
                        updateTextFieldsEntity();
                    } else {
                        updateTextFieldsTransaction();
                    }
                    break;
                default:
                    Toast toast = makeText(this,"Cancelled", Toast.LENGTH_SHORT);
                    toast.show();
            }
        }
    }

    /**
     * Launches DisplayHistory Activity
     * @param view
     */
    void displayHistory(View view){
        Intent intent = new Intent(this, DisplayHistory.class);
        intent.putExtra("INDEX",Reserve.getListItems(_currentItem.getType()).indexOf(_currentItem));
        startActivity(intent);
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
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (_itemType == ENTITY){
            updateTextFieldsEntity();
        } else {
            updateTextFieldsTransaction();
        }
        _assignedItems.clear();
        _assignedItems.addAll(_currentItem.getAssignmentList());
        _recycleViewAdapter.notifyDataSetChanged();
    }

}

