package com.bitallowance;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DisplayList extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    //Adapters and other Activity essential variables.
    private RecyclerViewAdapter _recycleViewAdapter;
    private List<ListItem> _listItems = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_list);

        //Load list with current transactions
        _listItems.addAll(Reserve.get_transactionList());
        Collections.sort(_listItems, new SortByValue());

        /* * * * * RECYCLER-VIEW SETUP * * * * */
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.displayList_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        //Select a Detailed adapter for this recycler view.
        _recycleViewAdapter = new RecyclerViewAdapter(this, _listItems, RecyclerViewAdapter.CardType.Detailed);
        recyclerView.setAdapter(_recycleViewAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
