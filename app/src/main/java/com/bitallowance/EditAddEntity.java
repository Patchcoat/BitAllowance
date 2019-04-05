package com.bitallowance;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static android.widget.Toast.makeText;

/**
 * Allows users to add a new entity object or edit an existing one.
 * *NOTE* ENTITY_INDEX must be passed in to edit an existing entity
 */
public class EditAddEntity extends AppCompatActivity implements DatePickerFragment.DatePickerFragmentListener, ListItemClickListener,  ListItemSelectDialog.NestedListItemClickListener{

    private static final String TAG = "BADDS-EditAddEntity";
    private int _entityIndex;
    private Entity _currentEntity;
    private List<ListItem> _assignedTransactions = new ArrayList<>();
    private List<ListItem> _unassignedTransactions = new ArrayList<>();
    ListItemRecycleViewAdapter _recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_add_entity);

        //Get Entity Index
        _entityIndex = getIntent().getIntExtra("ENTITY_INDEX", -1);

        //If entity index is not valid, create new Entity and set index to size of Reserve.getEntityList()
        if(_entityIndex >= Reserve.get_entityList().size() || _entityIndex < 0) {
            _currentEntity = new Entity();
            _entityIndex = Reserve.get_entityList().size();
            _currentEntity.setId(0);
        } else {
            //If entity exists, get the existing entity
            _currentEntity = Reserve.get_entityList().get(_entityIndex);

            if (_currentEntity == null) {
                Log.e(TAG, "onCreate: _currentEntity is null.");
            } else {
                //populate _assignedTransactions for recyclerView
                _assignedTransactions.addAll(_currentEntity.getAssignedTransactions());
            }

            EditText name = (EditText) findViewById(R.id.editEntity_txtName);
            EditText email = (EditText) findViewById(R.id.editEntity_txtEmail);
            Button birthday = (Button) findViewById(R.id.editEntity_btnDatePicker);

            name.setText(_currentEntity.getDisplayName());
            if (!_currentEntity.getEmail().isEmpty()) {
                email.setText(_currentEntity.getEmail());
            }
            birthday.setText(Reserve.dateToString(_currentEntity.getBirthday()));
        }

        //Create a working list of unassigned transactions
        for (ListItem item: Reserve.getListItems(ListItemType.ALL)) {
            if (!_assignedTransactions.contains(item)){
                _unassignedTransactions.add(item);
            }
        }

        // get the reference of RecyclerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // set a LinearLayoutManager with default vertical orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        //  call the constructor of CustomAdapter to send the reference and data to Adapter
        _recyclerViewAdapter = new ListItemRecycleViewAdapter(this, this, _assignedTransactions, ListItemRecycleViewAdapter.CardType.Simple);
        recyclerView.setAdapter(_recyclerViewAdapter); // set the Adapter to RecyclerView

    }

    public void showDatePickerDialog(View v) {
        DatePickerFragment datePicker = DatePickerFragment.newInstance(this);
        datePicker.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSet(Date date) {
        if (date == null)
            return;
        Button button = (Button)findViewById(R.id.editEntity_btnDatePicker);
        button.setText(Reserve.dateToString(date));
        _currentEntity.setBirthday(date);
    }

    public void saveEntity(View view) {
        EditText name = (EditText)findViewById(R.id.editEntity_txtName);
        EditText email = (EditText)findViewById(R.id.editEntity_txtEmail);
        Button birthday = (Button)findViewById(R.id.editEntity_btnDatePicker);
        if (birthday.getText().toString() == getResources().getString(R.string.editEntityBirthdayHint)){
            Toast toast = Toast.makeText(getApplicationContext(),"A birthdate is required", Toast.LENGTH_SHORT);
            toast.show();
            return;
        } else if (name.getText().toString().isEmpty()){
            Toast toast = Toast.makeText(getApplicationContext(),"A name is required", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        _currentEntity.setDisplayName(name.getText().toString());
        _currentEntity.setEmail(email.getText().toString());


        //Update the local entity list
        if (_entityIndex >= Reserve.get_entityList().size()) {
            Reserve.addEntity(_currentEntity);
        } else {
            Reserve.updateEntity(_currentEntity, _entityIndex);
        }

        //Save the transaction assignments
        for (ListItem listItem : _assignedTransactions) {
            Transaction transaction = (Transaction) listItem;
            transaction.updateAssignment(_currentEntity, true);
        }
        for (ListItem listItem : _unassignedTransactions) {
            Transaction transaction = (Transaction) listItem;
            transaction.updateAssignment(_currentEntity, false);
        }

        //Save item to server
        if (Reserve.serverIsPHP) {
            //build the string to send to the server
            String data = "updateEntity&resPK=" + Reserve.get_id() + "&entPK=" + _currentEntity.getId() + "&display=" + _currentEntity.getDisplayName() +
                    "&email=" + _currentEntity.getEmail() + "&birthday=" + Reserve.dateStringSQL(_currentEntity.getBirthday()) + "&balance=" + _currentEntity.getCashBalance().toString();
            new ServerUpdateListItem(this, data, _currentEntity).execute();
        } else {
            _currentEntity.update();
        }

    }

    public void editNextEntity(View view) {
        Intent intent = new Intent(this, EditAddEntity.class);
        intent.putExtra("ENTITY_INDEX", _entityIndex + 1);
        startActivity(intent);
        finish();
    }


    public void editPrevEntity(View view) {
        Intent intent = new Intent(this, EditAddEntity.class);
        intent.putExtra("ENTITY_INDEX", _entityIndex - 1);
        startActivity(intent);
        finish();
    }

    public void finish(View view) {
        finish();
    }

    /**
     * Creates a dialogue view that prompts the user for the type of Transaction they would like to apply
     * @param view
     */
    public void addTransactions(View view) {
        //Declare new Fragment Manager & ListItemSelectDialog
        FragmentManager fragmentManager = getSupportFragmentManager();
        ListItemSelectDialog selectDialog = new ListItemSelectDialog();

        //Create a bundle to hold title & menu options
        Bundle bundle = new Bundle();

        //Get a dynamic list of items that can be applied
        ArrayList options = new ArrayList<>(Arrays.asList("Task", "Reward", "Fine", "Cancel"));

        if (_currentEntity.getName() != null && _currentEntity.getName() != "")
            bundle.putString("TITLE", "What transaction would you like to assign to " + _currentEntity.getName() + "?");
        else
            bundle.putString("TITLE", "What would you like to assign?");

        bundle.putStringArrayList("OPTIONS", options);

        //Initialize a Dialogue
        selectDialog.initialize(null,this);
        //Step 2 - SET ARGUMENTS - pass bundle to dialog with title and options to display
        selectDialog.setArguments(bundle);

        //Show the dialog
        selectDialog.show(fragmentManager, "");
    }

    /**
     * Handles onclick events for RecyclerView items
     * @param position  position in recyclerView/_assignedList
     * @param adapter the RecyclerView adapter making the call
     */
    @Override
    public void onRecyclerViewItemClick(int position, ListItemRecycleViewAdapter adapter) {

        //Declare new Fragment Manager & ListItemSelectDialog
        FragmentManager fragmentManager = getSupportFragmentManager();
        ListItemSelectDialog selectDialog = new ListItemSelectDialog();

        //Create a bundle to hold title & menu options
        Bundle bundle = new Bundle();

        //Get a dynamic list of items that can be applied
        ArrayList options = new ArrayList<>();

        String type;
        switch (_assignedTransactions.get(position).getType()){
            case TASK:
                type = "task";
                break;
            case REWARD:
                type = "reward";
                break;
            case FINE:
                type = "fine";
                break;
            default:
                type = "transaction";
        }
        options.addAll(Arrays.asList("Un-assign " + type, "View " + type, "Edit " + type, "Cancel"));

        bundle.putString("TITLE", "What would you like to do?");
        bundle.putStringArrayList("OPTIONS", options);

        //Initialize a Dialogue
        selectDialog.initialize(_assignedTransactions.get(position),this);
        //Step 2 - SET ARGUMENTS - pass bundle to dialog with title and options to display
        selectDialog.setArguments(bundle);

        //Show the dialog
        selectDialog.show(fragmentManager, "");
    }

    @Override
    public void onListItemDialogClick(int position, ListItem selectedItems) {

        //If selected item is null, we know that the user was responding to the AddTransaction dialog
        if(selectedItems == null){
            switch (position) {
                //If Task is selected
                case 0:
                    displayTransactions(ListItemType.TASK);
                    return;
                //If REWARD is selected
                case 1:
                    displayTransactions(ListItemType.REWARD);
                    return;
                //If FINE is selected
                case 2:
                    displayTransactions(ListItemType.FINE);
                    return;
                //If CANCEL is selected
                default:
                    return;
            }

        } else {
            switch (position) {
                //If Un-assign is selected
                case 0:
                    _assignedTransactions.remove(selectedItems);
                    _unassignedTransactions.add(selectedItems);
                    _recyclerViewAdapter.notifyDataSetChanged();
                    return;
                //If View is selected
                case 1:
                    displayDetails(selectedItems);
                    return;
                //If Edit is selected
                case 2:
                    editItem(selectedItems);
                    return;
                //If CANCEL is selected
                default:
                    return;
            }
        }
    }

    /**
     * Opens a new activity to allow the selected item to be edited
     * @param selectedItem the item to be edited
     */
    private void editItem(ListItem selectedItem) {
        Intent intent;
        intent = new Intent(this, EditAddTransaction.class);
        intent.putExtra("TRANSACTION_INDEX", Reserve.get_transactionList().indexOf(selectedItem));
        intent.putExtra("TRANSACTION_TYPE", selectedItem.getType());
        startActivityForResult(intent, 1);
    }

    /**
     * Opens a new activity that displays the details of the passed item.
     * @param selectedItem the item to be displayed
     */
    private void displayDetails(ListItem selectedItem){
        Intent intent = new Intent(this, DisplayDetails.class);
        intent.putExtra("INDEX",Reserve.getListItems(selectedItem.getType()).indexOf(selectedItem));
        intent.putExtra("TYPE", selectedItem.getType());
        startActivityForResult(intent,1);
    }

    /**
     * Runs after finish() is called by a child activity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        _assignedTransactions.clear();
        for (ListItem item: Reserve.getListItems(ListItemType.ALL)) {
            if (!_unassignedTransactions.contains(item))
                _assignedTransactions.add(item);
        }
        _recyclerViewAdapter.notifyDataSetChanged();
    }

    /**
     * Creates a Nested dialogue with a list of unassigned transaction of a specific type.
     * @param type The type of transactions to display
     */
    public void displayTransactions(ListItemType type){
        //Declare new Fragment Manager & ListItemSelectDialog
        FragmentManager fragmentManager = getSupportFragmentManager();
        ListItemSelectDialog selectDialog = new ListItemSelectDialog();

        //Create a bundle to hold title & menu options
        Bundle bundle = new Bundle();

        //Dynamically add display options to bundle
        //*Note* Options must be added as a String ArrayList
        switch (type){
            case TASK:
                bundle.putString("TITLE", "Which task would you like to like to assign?");
                break;
            case REWARD:
                bundle.putString("TITLE", "Which reward would you like to assign?");
                break;
            case FINE:
                bundle.putString("TITLE", "Which fine would you like to assign?");
                break;
        }

        //Get a dynamic list of items that can be applied
        ArrayList options = new ArrayList<>();

        //Populate the options and unassigned list with transactions of the specified type
        for (ListItem listItem : _unassignedTransactions) {
            if (listItem.getType() == type){
                options.add(listItem.getName());
            }
        }

        //Make sure that there are un_assigned transactions to add
        if (options.size() == 0){
            Toast toast = makeText(getApplicationContext(), "There are no more active transactions of that type.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        options.add("Add All");
        options.add("Cancel");
        bundle.putStringArrayList("OPTIONS", options);

        //Initialize a NestedDialogue
        selectDialog.initializeNested(null, this, type);
        //Step 2 - SET ARGUMENTS - pass bundle to dialog with title and options to display
        selectDialog.setArguments(bundle);

        //Show the dialog
        selectDialog.show(fragmentManager, "");
    }

    @Override
    public void onNestedListItemDialogClick(int position, ListItem selectedItem, ListItemType selectionType) {
        //Null value implies display transaction was called.
        if (selectedItem == null){
            //Recreate the list of options
             List<ListItem> options = new ArrayList<>();
            for (ListItem listItem : _unassignedTransactions) {
                if (listItem.getType() == selectionType){
                    options.add(listItem);
                }
            }
            if (position == options.size()) {
                //Add all was selected
                _assignedTransactions.addAll(options);
                _unassignedTransactions.removeAll(options);
            } else if (position == options.size() + 1) {
                //Cancel was selected
                return;
            } else {
                _assignedTransactions.add(options.get(position));
                _unassignedTransactions.remove(options.get(position));
            }

            _recyclerViewAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}

