package com.bitallowance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class TestPage extends AppCompatActivity {

    /**
     * This class is meant to link to Activities we need to test
     * @param
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_page);
    }

    public void EditCurrency (View view){
        Intent intent = new Intent(this, EditAddCurrency.class);
        startActivity(intent);
    }
}
