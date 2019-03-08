package com.bitallowance;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class EditAddTransaction extends AppCompatActivity
        implements DatePickerFragment.DatePickerFragmentListener, AdapterView.OnItemSelectedListener {

    private Transaction _currentTransaction;
    private TransactionType _TransType;
    //For the recycler View
    List<ListItem> test = (List)Reserve.get_entityList();

    //Declare Spinners to allow dynamic content
    private Spinner spinExpires;
    private Spinner spinRepeatable;
    private Spinner spinCoolDown;
    List<String> spinExpiresOptions = new ArrayList<>(Arrays.asList("Does not expire.", "(Select Expiration Date)"));
    ArrayAdapter<String> spinAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_add_transaction);

        /* * * * * SET UP RECYCLER-VIEW * * * * */
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.editTransaction_Recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        RecyclerViewAdapter customAdapter = new RecyclerViewAdapter(this, test);
        recyclerView.setAdapter(customAdapter);


        spinAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinExpiresOptions);
        spinExpires = (Spinner) findViewById(R.id.editTransaction_spinExpires);
        spinRepeatable = (Spinner) findViewById(R.id.editTransaction_spinRepeat);
        spinCoolDown = (Spinner) findViewById(R.id.editTransaction_spinCoolDown);

        spinExpires.setOnItemSelectedListener(this);
        spinRepeatable.setOnItemSelectedListener(this);

        spinExpires.setAdapter(spinAdapter);
    }


    public void showDatePickerDialog(View v) {
        DatePickerFragment datePicker = DatePickerFragment.newInstance(this);
        datePicker.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSet(Date date) {
        if (spinExpiresOptions.size() == 2)
            spinExpiresOptions.add(Reserve.dateToString(date));
        else
            spinExpiresOptions.set(2, Reserve.dateToString(date));
        spinAdapter.notifyDataSetChanged();
        spinExpires.setSelection(2);
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
                ViewGroup.LayoutParams params = spinRepeatable.getLayoutParams();
                spinRepeatable.setMinimumWidth(170);
                int dpMultiplier = (int) getApplicationContext().getResources().getDisplayMetrics().density;
                if (position == 0) {
                    params.width = dpMultiplier * 160;
                    spinCoolDown.setVisibility(View.VISIBLE);
                } else {
                    params.width = dpMultiplier * 200;
                    spinCoolDown.setVisibility(View.INVISIBLE);
                }
                spinRepeatable.setLayoutParams(params);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
