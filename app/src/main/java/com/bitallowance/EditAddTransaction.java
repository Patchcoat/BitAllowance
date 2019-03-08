package com.bitallowance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class EditAddTransaction extends AppCompatActivity
        implements DatePickerFragment.DatePickerFragmentListener, AdapterView.OnItemSelectedListener {

    private Transaction _currentTransaction;
    private TransactionType _transType;
    private int _transIndex;
    //For the recycler View
    List<ListItem> test = (List)Reserve.get_entityList();

    //Declare Spinners to allow dynamic content
    private Spinner _spinExpires;
    private Spinner _spinRepeatable;
    private Spinner _spinCoolDown;
    private Spinner _spinAddEntity;
    List<String> spinExpiresOptions = new ArrayList<>(Arrays.asList("Does not expire.", "(Select Expiration Date)"));
    ArrayAdapter<String> spinAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_add_transaction);
        _transIndex = getIntent().getIntExtra("TRANSACTION_INDEX", -1);
        _transType = (TransactionType)getIntent().getSerializableExtra("TRANSACTION_TYPE");

        boolean isExisting;

        /* * * * * IF EXISTING TRANSACTION * * * * */
        if ((_transIndex >= 0 && _transIndex < Reserve.get_transactionList().size()) &&
                Reserve.get_transactionList().get(_transIndex).getTransactionType() == _transType){

            //Load current transaction with existing Transaction
            _currentTransaction = Reserve.get_transactionList().get(_transIndex);

            //Display transaction values
            updateTextFields();

            isExisting = true;
        } else {
            _transIndex = Reserve.get_transactionList().size();
            _currentTransaction = new Transaction();
            _currentTransaction.setTransactionType(_transType);
            isExisting = false;
        }

        setUpSpinners(isExisting);
        updateLabels(isExisting);

        /* * * * * SET UP RECYCLER-VIEW * * * * */
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.editTransaction_Recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        RecyclerViewAdapter customAdapter = new RecyclerViewAdapter(this, test);
        recyclerView.setAdapter(customAdapter);
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
        txtValue.setText(_currentTransaction.get_value().toString());
    }

    /**
     * This class provides setup for Activity Spinners.
     * @author Doug Barlow
     * @param existingTransaction - indicates whether is Adding or Editing
     */
    private void setUpSpinners(boolean existingTransaction){
        _spinAddEntity    = (Spinner)  findViewById(R.id.editTransaction_spinAddEntity);
        _spinCoolDown     = (Spinner)  findViewById(R.id.editTransaction_spinCoolDown);
        _spinRepeatable   = (Spinner)  findViewById(R.id.editTransaction_spinRepeat);
        _spinExpires      = (Spinner)  findViewById(R.id.editTransaction_spinExpires);

        /* * * * * BASIC SPINNER SETUP * * * * */
        spinAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinExpiresOptions);
        _spinExpires.setOnItemSelectedListener(this);
        _spinRepeatable.setOnItemSelectedListener(this);
        _spinExpires.setAdapter(spinAdapter);


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


    public void showDatePickerDialog(View v) {
        DatePickerFragment datePicker = DatePickerFragment.newInstance(this);
        datePicker.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSet(Date date) {
        if (date == null && spinExpiresOptions.size() == 2) {
            _spinExpires.setSelection(0);
            return;
        } else if (date != null){
            if (spinExpiresOptions.size() == 2)
                spinExpiresOptions.add(Reserve.dateToString(date));
            else
                spinExpiresOptions.set(2, Reserve.dateToString(date));
            spinAdapter.notifyDataSetChanged();
            _currentTransaction.setExpirationDate(date);
        }
        _spinExpires.setSelection(2);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (parent.getId()) {
            case R.id.editTransaction_spinExpires:
                if (position == 1) {
                    showDatePickerDialog(view);
                }
                break;
            case R.id.editTransaction_spinRepeat:
                ViewGroup.LayoutParams params = _spinRepeatable.getLayoutParams();
                _spinRepeatable.setMinimumWidth(170);
                int dpMultiplier = (int) getApplicationContext().getResources().getDisplayMetrics().density;
                if (position == 0) {
                    params.width = dpMultiplier * 160;
                    _spinCoolDown.setVisibility(View.VISIBLE);
                } else {
                    params.width = dpMultiplier * 200;
                    _spinCoolDown.setVisibility(View.INVISIBLE);
                }
                _spinRepeatable.setLayoutParams(params);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

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

        _currentTransaction.setName(txtName.getText().toString());
        _currentTransaction.setValue(txtValue.getText().toString());
        _currentTransaction.setMemo(txtDesc.getText().toString());

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

        if (_transIndex >= Reserve.get_transactionList().size()){
            Reserve.addTransaction(_currentTransaction);
        }
        else {
            Reserve.updateTransaction(_currentTransaction, _transIndex);
        }

        Toast toast = Toast.makeText(getApplicationContext(),"Record Saved.", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void editNextTransaction(View view) {
        Intent intent = new Intent(this, EditAddTransaction.class);

        int newIndex;
        for(newIndex = _transIndex + 1; newIndex < Reserve.get_transactionList().size(); newIndex++){
            if (Reserve.get_transactionList().get(newIndex).getTransactionType() == _transType){
                break;
            }
        }

        intent.putExtra("TRANSACTION_TYPE", _transType);
        intent.putExtra("TRANSACTION_INDEX", newIndex);

        startActivity(intent);
        finish();
    }


    public void editPrevTransaction(View view) {
        Intent intent = new Intent(this, EditAddTransaction.class);

        int newIndex;
        for(newIndex = _transIndex - 1; newIndex >= 0; newIndex--){
            if (Reserve.get_transactionList().get(newIndex).getTransactionType() == _transType){
                break;
            }
        }

        intent.putExtra("TRANSACTION_TYPE", _transType);
        intent.putExtra("TRANSACTION_INDEX", newIndex);

        startActivity(intent);
        finish();
    }

    public void finish(View view) {
        finish();
    }
}


