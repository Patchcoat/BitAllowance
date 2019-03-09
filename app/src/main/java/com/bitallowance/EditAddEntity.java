package com.bitallowance;

import android.app.AlertDialog;
import android.app.Application;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;

public class EditAddEntity extends AppCompatActivity implements DatePickerFragment.DatePickerFragmentListener {

    int _entityIndex;
    Entity _currentEntity;
    List<ListItem> test = (List)Reserve.get_entityList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_add_entity);
        _entityIndex = getIntent().getIntExtra("ENTITY_INDEX", -1);
        if(_entityIndex >= Reserve.get_entityList().size() || _entityIndex < 0) {
            _currentEntity = new Entity();
            _entityIndex = Reserve.get_entityList().size();
        } else {
          _currentEntity = Reserve.get_entityList().get(_entityIndex);
          EditText name = (EditText)findViewById(R.id.editEntity_txtName);
          EditText email = (EditText)findViewById(R.id.editEntity_txtEmail);
          Button birthday = (Button)findViewById(R.id.editEntity_btnDatePicker);

          name.setText(_currentEntity.getDisplayName());
          if (!_currentEntity.getEmail().isEmpty()){
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
        RecyclerViewAdapter customAdapter = new RecyclerViewAdapter(this, test);
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
}

