package com.bitallowance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.makeText;

/**
 * This class displays the details of a task. Providing the name of the task, the value of the task,
 * the expiration, and to whom the task is assigned.
 * @version 1.0
 * @since 03/18/2019
 */
public class DisplayDetails extends AppCompatActivity
        implements ListItemClickListener {


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

    @Override
    public void onRecyclerViewItemClick(int position, ListItemRecycleViewAdapter adapter) {
        Toast toast = makeText(getApplicationContext(),
                "Selected " + _assignedItems.get(position).getName(), Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onListItemDialogClick(int position, ListItem selectedItem) {

    }
}

