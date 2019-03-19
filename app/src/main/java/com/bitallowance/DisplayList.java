package com.bitallowance;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static android.widget.Toast.makeText;
import static com.bitallowance.ListItemType.FINE;
import static com.bitallowance.ListItemType.REWARD;

public class DisplayList extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener, ListItemClickListener {

    //Adapters and other Activity essential variables.
    private ListItemRecycleViewAdapter _recycleViewAdapter;
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


    @Override
    public void onRecyclerViewItemClick(int position, ListItemRecycleViewAdapter adapter) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        ListItemSelectDialog selectDialog = new ListItemSelectDialog();

        //Create a bundle to hold title & menu options
        Bundle bundle = new Bundle();
        bundle.putString("TITLE", _listItems.get(position).getName());

        ListItemType clickType = _listItems.get(position).getType();

        //Dynamically add display options to bundle
        switch (clickType){
            case ENTITY:
                bundle.putStringArrayList("OPTIONS", new ArrayList<>(Arrays.asList("Apply Payment",
                        "Apply Reward", "Apply Fine", "Edit " + _listItems.get(position).getName(),
                        "Delete "+ _listItems.get(position).getName(), "Cancel")));
                break;
            case TASK:
                bundle.putStringArrayList("OPTIONS", new ArrayList<>(Arrays.asList("Apply Payment",
                        "Edit " + _listItems.get(position).getName(), "Delete "+ _listItems.get(position).getName(), "Cancel")));
                break;
            case REWARD:
                bundle.putStringArrayList("OPTIONS", new ArrayList<>(Arrays.asList("Apply Reward",
                        "Edit " + _listItems.get(position).getName(), "Delete "+ _listItems.get(position).getName(), "Cancel")));
                break;

            case FINE:
                bundle.putStringArrayList("OPTIONS", new ArrayList<>(Arrays.asList("Apply Fine",
                        "Edit " + _listItems.get(position).getName(), "Delete "+ _listItems.get(position).getName(), "Cancel")));
                break;

        }

        //THIS MUST BE CALLED
        selectDialog.initialize(_listItems.get(position), this);

        // set MyFragment Arguments
        selectDialog.setArguments(bundle);

        selectDialog.show(fragmentManager, "Display Options");

    }

    @Override
    public void onListItemDialogClick(int position, ListItem item) {

        if (item != null){

            if (item.getType() == ListItemType.ENTITY){
                switch (position){
                    case 0:
                    case 1:
                    case 2:
                        getTransactionToApply(item);
                        break;
                    case 3:
                        Intent intent = new Intent(this, EditAddEntity.class);
                        intent.putExtra("ENTITY_INDEX", _listItems.indexOf(item));
                        startActivity(intent);
                        break;
                }
            }
            else{
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        Intent intent = new Intent(this, EditAddTransaction.class);
                        intent.putExtra("TRANSACTION_INDEX", Reserve.get_transactionList().indexOf(item));
                        intent.putExtra("TRANSACTION_TYPE", item.getType());
                        startActivity(intent);
                }
            }
        }
        Toast toast = makeText(getApplicationContext(), "Selected option " + position, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void getTransactionToApply(ListItem item){

    }
}
