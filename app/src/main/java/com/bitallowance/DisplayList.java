package com.bitallowance;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static android.widget.Toast.makeText;

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
      //  String[] colors = {"red", "green", "blue", "black"};

        FragmentManager fragmentManager = getSupportFragmentManager();
        ListItemSelectDialog selectDialog = new ListItemSelectDialog();

        Bundle bundle = new Bundle();
        bundle.putString("TITLE", "What would you like to do?");
        bundle.putStringArrayList("OPTIONS", new ArrayList<>(Arrays.asList("Edit...", "Pay...", "Fine...", "Apply Reward", "Delete")));

        //THIS MUST BE CALLED
        selectDialog.initialize(_listItems.get(position), this);

// set MyFragment Arguments
        selectDialog.setArguments(bundle);

        selectDialog.show(fragmentManager, "test");


           /*
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setTitle("Pick a color");
        LayoutInflater inflater = getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.recyclerview_select_dialog, null));


        builder.setMultiChoiceItems(colors, new boolean[]{true,false,true,false}, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

            }
        });
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on colors[which]
            }
        }); //*/
        //builder.show();

/*
        // Initializing a new alert dialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the alert dialog title
        builder.setTitle("What would you like to do?");

        // Initialize a new list of options
        final List<String> options = new ArrayList<String>(Arrays.asList("Edit...", "Pay...", "Fine...", "Apply Reward", "Delete"));

        // Initialize a new array adapter instance
        final ArrayAdapter arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_selectable_list_item, options){
            @Override
            public View getView( int position,View convertView, ViewGroup parent){
                // Cast list view each item as text view
                TextView text_view = (TextView) super.getView(position,convertView,parent);

                text_view.getLayoutParams().height = (int) getApplicationContext().getResources().getDisplayMetrics().density * 30;


                GradientDrawable gd = new GradientDrawable();

                //How to add a border
                gd.setStroke(1, 0xFF000000);

                // How to set the background color
                // gd.setColor(0xFF00FF00); // Changes this drawable to use a single color instead of a gradient
                //How to apply rounded corners
                //gd.setCornerRadius(5);


                text_view.setBackground(gd);

                //How to set font
                //text_view.setTypeface(_typeFace);

                //How to set text size
                //text_view.setTextSize(TypedValue.COMPLEX_UNIT_DIP,25);

                //How to set text color
                //text_view.setTextColor(Color.parseColor("#FF831952"));
                return text_view;
            }
        };

        // Set a single choice items list for alert dialog
        builder.setSingleChoiceItems(
                arrayAdapter, // Items list
                -1, // Index of checked item (-1 = no selection)
                new DialogInterface.OnClickListener() // Item click listener
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Get the alert dialog selected item's text
                        String selectedItem = options.get(i);

                        // Display the selected item's text on toast
                        Toast.makeText(getApplicationContext(),"Checked : " + selectedItem,Toast.LENGTH_LONG).show();
                        dialogInterface.dismiss();
                    }
                });

        // Set the a;ert dialog positive button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Just dismiss the alert dialog after selection
                // Or do something now
            }
        });

        // Create the alert dialog
        AlertDialog dialog = builder.create();

        // Finally, display the alert dialog
        dialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

        //Customize the layout of the dialog
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = 800;
        dialog.getWindow().setAttributes(lp);

        //*/
        Toast toast = makeText(getApplicationContext(), "Selected " + _listItems.get(position).getName(), Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onListItemDialogClick(int position, ListItem item) {
        Toast toast = makeText(getApplicationContext(), "Selected option " + position, Toast.LENGTH_SHORT);
        toast.show();
    }
}
