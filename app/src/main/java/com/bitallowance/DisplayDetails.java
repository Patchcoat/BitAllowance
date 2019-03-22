package com.bitallowance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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


    private Transaction _currentTransaction;
    private ListItemType _transType;
    private int _transIndex;

    //For the recycler View
    private List<ListItem> _entityListAssigned = new ArrayList<>();
    private List<ListItem> _entityListUnassigned = new ArrayList<>();

    private ListItemRecycleViewAdapter _recycleViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_details);
        _transIndex = getIntent().getIntExtra("TRANSACTION_INDEX", -1);
        _transType = (ListItemType)getIntent().getSerializableExtra("TRANSACTION_TYPE");

        boolean isExisting;

        /* * * * * IF EXISTING TRANSACTION * * * * */
        if ((_transIndex >= 0 && _transIndex < Reserve.get_transactionList().size()) &&
                Reserve.get_transactionList().get(_transIndex).getTransactionType() == _transType){

            //Load current transaction with existing Transaction
            _currentTransaction = Reserve.get_transactionList().get(_transIndex);

            //Display transaction values
            updateTextFields();

            isExisting = true;
            for (Entity entity : Reserve.get_entityList()){

                if ((_currentTransaction.getAssignments().get(entity) != null) &&
                        _currentTransaction.getAssignments().get(entity)){
                    _entityListAssigned.add(entity);
                }
                else{
                    _entityListUnassigned.add(entity);
                }
            }

        } else {
            _transIndex = Reserve.get_transactionList().size();
            _currentTransaction = new Transaction();
            _currentTransaction.setTransactionType(_transType);
            isExisting = false;
            for (Entity entity : Reserve.get_entityList()){
                _entityListUnassigned.add(entity);
            }
        }

        // setUpSpinners(isExisting);
        // updateLabels(isExisting);

        /* * * * * SET UP RECYCLER-VIEW * * * * */
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.displayDetails_Recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        _recycleViewAdapter = new ListItemRecycleViewAdapter
                (this, this, _entityListAssigned,
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

        switch (_transType){
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
     * This class updates text values for EditText items when the Activity is Editing a transaction
     */
    private void updateTextFields(){
        //SET VALUES OF TEXT FIELDS
        TextView txtName  = (TextView) findViewById(R.id.displayDetails_txtName);
        TextView txtValue = (TextView) findViewById(R.id.displayDetails_txtValue);
        TextView txtDesc  = (TextView) findViewById(R.id.displayDetails_txtDesc);

        // _textDisplayDetails = (TextView)  findViewById(R.id.displayDetails_textDisplayDetails);
        TextView _textCoolDown       = (TextView)  findViewById(R.id.displayDetails_textCoolDown);
        TextView _textRepeatable     = (TextView)  findViewById(R.id.displayDetails_textRepeat);
        TextView _textExpires        = (TextView)  findViewById(R.id.displayDetails_textExpires);

        txtName.setText(_currentTransaction.getName());
        txtDesc.setText(_currentTransaction.getMemo());
        txtValue.setText(_currentTransaction.getValue().toString());

        // Populates the coolDown data
        switch (_currentTransaction.getCoolDown()) {
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
        if (_currentTransaction.isRepeatable()){
            _textRepeatable.setText("Not-Repeatable");
        } else {
            _textRepeatable.setText("Repeatable");
        }

        // Populates Expiration Data
        if (!_currentTransaction.isExpirable()){
            _textExpires.setText("Does Not Expire");
        } else {
            _textExpires.setText(_currentTransaction.getExpirationDate().toString());
        }
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

        //Find the next transaction with a matching type.
        for(newIndex = _transIndex + 1; newIndex < Reserve.get_transactionList().size(); newIndex++){
            if (Reserve.get_transactionList().get(newIndex).getTransactionType() == _transType){
                break;
            }
        }

        //Pass the transaction type in case it's a new transaction
        intent.putExtra("TRANSACTION_TYPE", _transType);
        //Pass the index (New transaction will be transactionList().size() + 1)
        intent.putExtra("TRANSACTION_INDEX", newIndex);

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

        //Find the next transaction with a matching type.
        for(newIndex = _transIndex - 1; newIndex >= 0; newIndex--){
            if (Reserve.get_transactionList().get(newIndex).getTransactionType() == _transType){
                break;
            }
        }

        //Pass the transaction type in case it's a new transaction
        intent.putExtra("TRANSACTION_TYPE", _transType);
        //Pass the index (New transaction will be -1)
        intent.putExtra("TRANSACTION_INDEX", newIndex);

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
                "Selected " + _entityListAssigned.get(position).getName(), Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onListItemDialogClick(int position, ListItem selectedItem) {

    }
}

