package com.bitallowance;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DisplayHistory extends AppCompatActivity implements  AdapterView.OnItemSelectedListener, ListItemClickListener{

    //Adapters and other Activity essential variables.
    private ListItemRecycleViewAdapter _recycleViewAdapter;
    private List<ListItem> _listItems = new ArrayList<>();
    private ListItemType _type;
    private Entity _entity;
    private int _index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_history);

        _index = getIntent().getIntExtra("INDEX" , 0);

        _entity = (Entity) Reserve.getListItems(ListItemType.ENTITY).get(_index);
        //Check to make sure type has successfully been assigned.
        if (_type == null){
            _type = ListItemType.ALL;
        }

        ListItem item = _entity;
        _listItems.addAll(_entity.getTransactionHistory());

        /* * * * * RECYCLER-VIEW SETUP * * * * */
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.displayHistory_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        //Select a Detailed adapter for this recycler view.
        _recycleViewAdapter = new ListItemRecycleViewAdapter(this, this, _listItems, ListItemRecycleViewAdapter.CardType.Detailed);
        recyclerView.setAdapter(_recycleViewAdapter);

        //Set up sort spinner
        setupSpinner();
    }

    /**
     * Sets up the spinner and dynamically populates spinner options based on
     * list-type.
     * @author Doug Barlow
     */
    private void setupSpinner(){
        //Declaring these here because this spinner doesn't need to change after it's set up.
        Spinner spinSort = (Spinner)findViewById(R.id.displayHistory_spinSort);
        ArrayAdapter<String> spinSortAdapter;

        List spinnerOptions = new ArrayList<>(Arrays.asList("None", "Name", "Value", "Date"));

        spinSortAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerOptions);

        //Set spinner listener and adapter
        spinSort.setOnItemSelectedListener(this);
        spinSort.setAdapter(spinSortAdapter);
    }

    /**
     * This class handles the onItemSelected event for the Sort Spinner. It contains basic sorting logic.
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                _listItems.clear();
                _listItems.addAll(_entity.getTransactionHistory());
                break;
            case 1:
                Collections.sort(_listItems, new SortByName());
                break;
            case 2:
                Collections.sort(_listItems, new SortByValue());
                break;
            case 3:
                Collections.sort(_listItems, new SortByDate());
                break;
        }

        //Need to update recycleViewAdapter.
        _recycleViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onRecyclerViewItemClick(int position, ListItemRecycleViewAdapter adapter) {

    }

    @Override
    public void onListItemDialogClick(int position, ListItem selectedItem) {

    }
}
