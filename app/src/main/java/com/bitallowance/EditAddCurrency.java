package com.bitallowance;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class EditAddCurrency extends AppCompatActivity {

    private Boolean isFirstLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_add_currency);
        isFirstLogin = getIntent().getBooleanExtra("FIRST_TIME", false);

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
        EditText txtCurrency = (EditText)findViewById(R.id.editCurrency_txtName);
        Spinner spinSymbol = (Spinner)findViewById(R.id.editCurrency_spinSymbol);

        String currency = txtCurrency.getText().toString();
        String symbol = spinSymbol.getSelectedItem().toString();

        //Stop if currency name invalid
        if (!Reserve.setCurrencyName(currency)){
            Toast toast = Toast.makeText(getApplicationContext(), "Invalid Currency Name", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }else {
            Reserve.set_currencySymbol(symbol);

            if (Reserve.serverIsPHP) {
                //build the string to send to the server
                String data = "updateSettings&resPK=" + Reserve.get_id() + "&currency=" + currency + "&symbol=" + symbol;
                new ServerUpdateSettings(this, data).execute();
            }

            finish(view);
        }
    }

    public void finish(View view){

        if (isFirstLogin){
            Intent intent = new Intent(this, ReserveHome.class);
            intent.putExtra("RESERVE_ID", Reserve.get_id());
            startActivity(intent);

        } else {
            finish();
        }
    }

}
