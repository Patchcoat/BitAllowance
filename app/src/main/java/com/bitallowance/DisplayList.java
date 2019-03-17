package com.bitallowance;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DisplayList extends AppCompatActivity {

    private RecyclerViewAdapter _recycleViewAdapter;
    private List<ListItem> _listItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_list);

        //Load list with current transactions
        _listItems.addAll(Reserve.get_transactionList());
        Collections.sort(_listItems, new SortByValue());

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.displayList_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        _recycleViewAdapter = new RecyclerViewAdapter(this, _listItems, RecyclerViewAdapter.CardType.Detailed);
        recyclerView.setAdapter(_recycleViewAdapter); // set the Adapter to RecyclerView
    }
}
