package com.bitallowance;

import android.content.Intent;
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
import java.util.Date;
import java.util.List;

import static android.widget.Toast.makeText;

/**
 * Allows users to add a new entity object or edit an existing one.
 * *NOTE* ENTITY_INDEX must be passed in to edit an existing entity
 */
public class EditAddEntity extends AppCompatActivity implements DatePickerFragment.DatePickerFragmentListener, ListItemClickListener {

    private static final String TAG = "BADDS-EditAddEntity";
    private int _entityIndex;
    private Entity _currentEntity;
    private List<ListItem> _assignedTransactions = new ArrayList<>();

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

        // get the reference of RecyclerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // set a LinearLayoutManager with default vertical orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        //  call the constructor of CustomAdapter to send the reference and data to Adapter
        ListItemRecycleViewAdapter customAdapter = new ListItemRecycleViewAdapter(this, this, _assignedTransactions, ListItemRecycleViewAdapter.CardType.Simple);
        recyclerView.setAdapter(customAdapter); // set the Adapter to RecyclerView

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

        if (_entityIndex >= Reserve.get_entityList().size()) {
            Reserve.addEntity(_currentEntity);
        } else {
            Reserve.updateEntity(_currentEntity, _entityIndex);
        }
        _currentEntity.update();
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

    @Override
    public void onRecyclerViewItemClick(int position, ListItemRecycleViewAdapter adapter) {
        Toast toast = makeText(getApplicationContext(), "Selected " + _assignedTransactions.get(position).getName(), Toast.LENGTH_SHORT);
        toast.show();

    }

    @Override
    public void onListItemDialogClick(int position, ListItem selectedItems) {
        Toast toast = makeText(getApplicationContext(), "Selected option " + position, Toast.LENGTH_SHORT);
        toast.show();
    }
}

