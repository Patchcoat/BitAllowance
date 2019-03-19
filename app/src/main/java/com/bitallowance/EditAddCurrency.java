package com.bitallowance;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class EditAddCurrency extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_add_currency);
        if (Reserve.getCurrencyName() != ""){
            EditText currency = (EditText)findViewById(R.id.editCurrency_txtName);
            Spinner symbol = (Spinner)findViewById(R.id.editCurrency_spinSymbol);
            currency.setText(Reserve.getCurrencyName());
            symbol.setSelection(getIndex(symbol, Reserve.get_currencySymbol()));
        }
    }


    //Added to populate Spinner
    private int getIndex(Spinner spinner, String spinnerVal){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(spinnerVal)){
                return i;
            }
        }
        return 0;
    }



    public void updateCurrency(View view){
        EditText currency = (EditText)findViewById(R.id.editCurrency_txtName);
        Spinner symbol = (Spinner)findViewById(R.id.editCurrency_spinSymbol);

        //Stop if currency name invalid
        if (!Reserve.setCurrencyName(currency.getText().toString())) {
            Toast toast = Toast.makeText(getApplicationContext(), "Invalid Currency Name", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }else {
            Reserve.set_currencySymbol(symbol.getSelectedItem().toString());
            finish(view);
        }
    }

    public void finish(View view){
        finish();
    }

}
