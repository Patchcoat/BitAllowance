package com.bitallowance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static android.widget.Toast.makeText;

public class EditAddTransaction extends AppCompatActivity
        implements DatePickerFragment.DatePickerFragmentListener, AdapterView.OnItemSelectedListener, ListItemClickListener {

    private Transaction _currentTransaction;
    private ListItemType _transType;
    private int _transIndex;

    //For the recycler View
    private List<ListItem> _entityListAssigned = new ArrayList<>();
    private List<ListItem> _entityListUnassigned = new ArrayList<>();

    //Declare Spinners to allow dynamic content
    private Spinner _spinExpires;
    private Spinner _spinRepeatable;
    private Spinner _spinCoolDown;
    private Spinner _spinAddEntity;
    private List<String> _spinExpiresOptions = new ArrayList<>(Arrays.asList("Does not expire.", "(Select Expiration Date)"));
    private List<String> _spinAddEntityOptions = new ArrayList<>(Arrays.asList("Add Assignment", "Assign All"));
    private ArrayAdapter<String> _spinExpireAdapter;
    private ArrayAdapter<String> _spinAddEntityAdapter;

    private ListItemRecycleViewAdapter _recycleViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_add_transaction);
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

                if ((_currentTransaction.isAssigned(entity))) {
                    _entityListAssigned.add(entity);
                }
                else{
                    _entityListUnassigned.add(entity);
                    _spinAddEntityOptions.add(entity.getName());
                }
            }

        } else {
            _transIndex = Reserve.get_transactionList().size();
            _currentTransaction = new Transaction();
            _currentTransaction.setTransactionType(_transType);
            isExisting = false;
            for (Entity entity : Reserve.get_entityList()){
                _entityListUnassigned.add(entity);
                _spinAddEntityOptions.add(entity.getName());
            }
        }

        setUpSpinners(isExisting);
        updateLabels(isExisting);

        /* * * * * SET UP RECYCLER-VIEW * * * * */
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.editTransaction_Recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        _recycleViewAdapter = new ListItemRecycleViewAdapter(this, this, _entityListAssigned, ListItemRecycleViewAdapter.CardType.Simple);


        recyclerView.setAdapter(_recycleViewAdapter);
    }

    /**
     * This class dynamically updates the labels for the activity allowing us to use the same
     * activity for TASKS, FINES and REWARDS
     * @param isEditing - Are we editing or adding a transaction
     */
    private void updateLabels(boolean isEditing){

        String strLblTitle;
        String strLblName;
        String strLblValue;

        if (isEditing)
            strLblTitle = "Edit ";
        else
            strLblTitle = "Add New ";

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
        TextView lblTitle = (TextView) findViewById(R.id.editTransaction_lblTitle);
        TextView lblName  = (TextView) findViewById(R.id.editTransaction_lblName);
        TextView lblValue = (TextView) findViewById(R.id.editTransaction_lblValue);

        lblTitle.setText(strLblTitle);
        lblName.setText(strLblName);
        lblValue.setText(strLblValue);

    }
    /**
     * This class updates text values for EditText items when the Activity is Editing a transaction
     */
    private void updateTextFields(){
        //SET VALUES OF TEXT FIELDS
        EditText txtName  = (EditText) findViewById(R.id.editTransaction_txtName);
        EditText txtValue = (EditText) findViewById(R.id.editTransaction_txtValue);
        EditText txtDesc  = (EditText) findViewById(R.id.editTransaction_txtDesc);
        txtName.setText(_currentTransaction.getName());
        txtDesc.setText(_currentTransaction.getMemo());
        txtValue.setText(_currentTransaction.getValue().toString());
    }

    /**
     * This class provides setup for Activity Spinners.
     * Defines Spinners and Array Adapters for Spinner member variables
     * Sets the adapters and listeners for various spinners.
     * @author Doug Barlow
     * @param existingTransaction - indicates whether is Adding or Editing
     */
    private void setUpSpinners(boolean existingTransaction){
        _spinAddEntity    = (Spinner)  findViewById(R.id.editTransaction_spinAddEntity);
        _spinCoolDown     = (Spinner)  findViewById(R.id.editTransaction_spinCoolDown);
        _spinRepeatable   = (Spinner)  findViewById(R.id.editTransaction_spinRepeat);
        _spinExpires      = (Spinner)  findViewById(R.id.editTransaction_spinExpires);

        /* * * * * BASIC SPINNER SETUP * * * * */
        _spinExpireAdapter    = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, _spinExpiresOptions);
        _spinAddEntityAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, _spinAddEntityOptions);
        _spinExpires.setOnItemSelectedListener(this);
        _spinRepeatable.setOnItemSelectedListener(this);
        _spinAddEntity.setOnItemSelectedListener(this);
        _spinExpires.setAdapter(_spinExpireAdapter);
        _spinAddEntity.setAdapter(_spinAddEntityAdapter);


        //If loading an existing Transaction set spinners to their previous locations
        if (existingTransaction)
        {
            switch(_currentTransaction.getCoolDown()){
                case 0: // 0 hour cool down
                    _spinCoolDown.setSelection(0);
                    break;
                case 1: // 1 hour cool down
                    _spinCoolDown.setSelection(1);
                    break;
                case 24: // 24 hour or 1 day cool down
                    _spinCoolDown.setSelection(2);
                    break;
                case 168: // 168 hours or 1 week
                    _spinCoolDown.setSelection(3);
                    break;
            }

            if (_currentTransaction.isRepeatable()){
                _spinRepeatable.setSelection(0);
            } else {
                _spinRepeatable.setSelection(1);
            }

            if (!_currentTransaction.isExpirable()){
                _spinExpires.setSelection(0);
            } else {
                //Reuse onDateSet code
                onDateSet(_currentTransaction.getExpirationDate());
            }
        }
    }


    /**
     * This creates a DatePickerFragment and displays it.
     * @param v
     */
    public void showDatePickerDialog(View v) {
        DatePickerFragment datePicker = DatePickerFragment.newInstance(this);
        datePicker.show(getSupportFragmentManager(), "datePicker");
    }

    /**
     * This is the listener for the DatePicker.
     * I also use it manage the state of the spinExpires spinner when loading
     * and existing Transaction because the functionality is already there.
     * @param date
     */
    @Override
    public void onDateSet(Date date) {
        if (date == null && _spinExpiresOptions.size() == 2) {
            _spinExpires.setSelection(0);
            return;
        } else if (date != null){
            if (_spinExpiresOptions.size() == 2)
                _spinExpiresOptions.add(Reserve.dateToString(date));
            else
                _spinExpiresOptions.set(2, Reserve.dateToString(date));
            _spinExpireAdapter.notifyDataSetChanged();
            _currentTransaction.setExpirationDate(date);
        }
        _spinExpires.setSelection(2);
    }

    /**
     * This is the listener for the Spinner objects.
     * Uses a switch to differentiate which Spinner call came from.
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (parent.getId()) {
            //Expiration Spinner
            case R.id.editTransaction_spinExpires:
                if (position == 1) {
                    showDatePickerDialog(view);
                }
                break;
            //Repeat Spinner
            case R.id.editTransaction_spinRepeat:
                //Settings to make the spinner display better based on what is selected
                ViewGroup.LayoutParams params = _spinRepeatable.getLayoutParams();
                _spinRepeatable.setMinimumWidth(170);
                //pixel density multiplier for converting pixels to dp
                int dpMultiplier = (int) getApplicationContext().getResources().getDisplayMetrics().density;
                //If repeatable show cool-down spinner.
                if (position == 0) {
                    params.width = dpMultiplier * 160;
                    _spinCoolDown.setVisibility(View.VISIBLE);
                //else hide cool-down spinner and increase width of repeatable spinner. (for aesthetics)
                } else {
                    params.width = dpMultiplier * 200;
                    _spinCoolDown.setVisibility(View.INVISIBLE);
                }
                //activates the size changes of spinRepeatable
                _spinRepeatable.setLayoutParams(params);
                break;
            //Add Entity Spinner
            case R.id.editTransaction_spinAddEntity:
                //If first option is selected - Do nothing.
                if (position == 0) {
                    return;
                    //If position 1 selected assign transaction to ALL entities.
                } else if (position == 1) {
                    /* * * * FIRST CHECK IF UNASSIGNED LIST EMPTY * * * */
                    //Otherwise we risk crashing
                    if (_entityListUnassigned.size() == 0){
                        _spinAddEntity.setSelection(0);
                    }else {
                        //copy each entity to the Assigned list and remove corresponding spinner option
                        for (ListItem entity : _entityListUnassigned) {
                            _entityListAssigned.add(entity);
                            _spinAddEntityOptions.remove(2);
                        }
                        //clear unassigned list or effects won't be saved.
                        _entityListUnassigned.clear();
                        //Set selection back to 0 for ease of user.
                        _spinAddEntity.setSelection(0);
                    }
                } else {//ELSE -when user selects a single Entity
                    //Copy selected entity to Assigned list
                    _entityListAssigned.add(_entityListUnassigned.get(position - 2));
                    //Remove entity from Unassigned List
                    _entityListUnassigned.remove(position - 2);
                    //Remove option from option list.
                    _spinAddEntityOptions.remove(position);
                    //Set selection back to 0 for ease of user
                    _spinAddEntity.setSelection(0);
                }
                //Let the adapter know to redraw Add-Entity spinner with updated options.
                _spinAddEntityAdapter.notifyDataSetChanged();
                for (int i = 0; i < _spinAddEntityAdapter.getCount(); i++);

                //Let the adapter know to redraw the RecyclerView with the updated entity list.
                _recycleViewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * Saves the current  Transaction based on the current inputs.
     * Basic Error checking for EditText items. Spinners are programmed
     * in such a way it should be impossible for them to be in an invalid state.
     * @param view
     */
    public void saveTransaction(View view) {
        EditText txtName  = (EditText) findViewById(R.id.editTransaction_txtName);
        EditText txtValue = (EditText) findViewById(R.id.editTransaction_txtValue);
        EditText txtDesc  = (EditText) findViewById(R.id.editTransaction_txtDesc);

        /* * * * * CHECK FOR EMPTY FIELDS * * * * */
        if (txtName.getText().toString().isEmpty()) {
            Toast toast = Toast.makeText(getApplicationContext(),"A transaction name is required.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        } else if (txtValue.getText().toString().isEmpty()){
            Toast toast = Toast.makeText(getApplicationContext(),"You must specify a value for this transaction!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        } else if (txtDesc.getText().toString().isEmpty()) {
            Toast toast = Toast.makeText(getApplicationContext(),"You must add a description.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        //Save Text Fields
        _currentTransaction.setName(txtName.getText().toString());
        _currentTransaction.setValue(txtValue.getText().toString());
        _currentTransaction.setMemo(txtDesc.getText().toString());

        //Save ExpireSpinner State.
        //Expiration Date tentatively saved in onDateSet()
        //Expiration Date save locked when this object is saved in Reserve's master transaction list
        if (_spinExpires.getSelectedItemPosition() == 0) {
            _currentTransaction.setIsExpirable(false);
        }
        else {
            _currentTransaction.setIsExpirable(true);
        }

        if (_spinRepeatable.getSelectedItemPosition() == 0) {
            _currentTransaction.setIsRepeatable(true);
        } else {
            _currentTransaction.setIsRepeatable(false);
        }

        //Save cooldown selection in hours
        switch (_spinCoolDown.getSelectedItemPosition()){
            case 1:
                _currentTransaction.setCoolDown(1); // 1 hour
                break;
            case 2:
                _currentTransaction.setCoolDown(24); // 1 day
                break;
            case 3:
                _currentTransaction.setCoolDown(168); // 1 Week
                break;
            default:
                _currentTransaction.setCoolDown(0); // No cooldown
        }


        /* * * * * UPDATE ASSIGNMENT MAP * * * * */
        for (ListItem entity: _entityListAssigned) {
            _currentTransaction.updateAssignment((Entity) entity, Boolean.TRUE);
        }
        for (ListItem entity: _entityListUnassigned) {
            _currentTransaction.updateAssignment((Entity) entity, Boolean.FALSE);
        }

        /* * * * * FINALLY UPDATE TRANSACTION IN THE MASTER LIST * * * * */
        if (_transIndex >= Reserve.get_transactionList().size()){
            Reserve.addTransaction(_currentTransaction);
        }
        else {
            Reserve.updateTransaction(_currentTransaction, _transIndex);
        }
        _currentTransaction.update();

        Toast toast = Toast.makeText(getApplicationContext(),"Record Saved.", Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * This class allows the user to move to the Next Transaction (of the same Transaction Type)
     * in the list.
     * @param view
     */
    public void editNextTransaction(View view) {
        Intent intent = new Intent(this, EditAddTransaction.class);
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
        Intent intent = new Intent(this, EditAddTransaction.class);
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
        Toast toast = makeText(getApplicationContext(), "Selected " + _entityListAssigned.get(position).getName(), Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onListItemDialogClick(int position, ListItem selectedItem ) {
        Toast toast = makeText(getApplicationContext(), "Selected option " + position, Toast.LENGTH_SHORT);
        toast.show();
    }
}


