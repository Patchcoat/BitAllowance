package com.bitallowance;

import android.graphics.BitmapRegionDecoder;
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DisplayList extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    //Adapters and other Activity essential variables.
    private RecyclerViewAdapter _recycleViewAdapter;
    private List<ListItem> _listItems = new ArrayList<>();
    private ListItemType _type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_list);

        _type = (ListItemType)getIntent().getSerializableExtra("LIST_ITEM_TYPE");
        //Check to make sure type has successfully been assigned.
        if (_type == null){
            _type = ListItemType.ALL;
        }

        //Load list with current items
        _listItems.addAll(Reserve.getListItems(_type));

        /* * * * * RECYCLER-VIEW SETUP * * * * */
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.displayList_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        //Select a Detailed adapter for this recycler view.
        _recycleViewAdapter = new RecyclerViewAdapter(this, _listItems, RecyclerViewAdapter.CardType.Detailed);
        recyclerView.setAdapter(_recycleViewAdapter);

        //Set up sort spinner
        setupSpinner();

    }

    /**
     * setupSpinner() - Sets up the spinner and dynamically populates spinner options based on
     * list-type.
     * @author Doug Barlow
     */
    private void setupSpinner(){
        //Declaring these here because this spinner doesn't need to change after it's set up.
        Spinner spinSort = (Spinner)findViewById(R.id.displayList_spinSort);
        ArrayAdapter<String> spinSortAdapter;

        List spinnerOptions;
        switch (_type){
            case ENTITY:
                spinnerOptions = new ArrayList<>(Arrays.asList("None", "Name", "Balance", "Birthday"));
                break;
            case REWARD:
            case FINE:
                spinnerOptions = new ArrayList<>(Arrays.asList("None", "Name", "Cost", "Expiration"));
                break;
            default:
                spinnerOptions = new ArrayList<>(Arrays.asList("None", "Name", "Value", "Expiration"));
        }

        spinSortAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerOptions);

        //Set spinner listener and adapter
        spinSort.setOnItemSelectedListener(this);
        spinSort.setAdapter(spinSortAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                _listItems.clear();
                _listItems.addAll(Reserve.getListItems(_type));
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

        _recycleViewAdapter.notifyDataSetChanged();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
