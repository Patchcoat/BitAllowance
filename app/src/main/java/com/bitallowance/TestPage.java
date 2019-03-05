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

    public void EditEntity (View view){
        Intent intent = new Intent(this, EditAddEntity.class);
        intent.putExtra("ENTITY_INDEX", -1);
        startActivity(intent);
    }

    public void EditReward (View view){
        Intent intent = new Intent(this, EditAddTransaction.class);
        intent.putExtra("TRANSACTION_INDEX", -1);
        intent.putExtra("TRANSACTION_TYPE", TransactionType.REWARD);
        startActivity(intent);
    }

    public void EditTask (View view){
        Intent intent = new Intent(this, EditAddTransaction.class);
        intent.putExtra("TRANSACTION_INDEX", -1);
        intent.putExtra("TRANSACTION_TYPE", TransactionType.TASK);
        startActivity(intent);
    }

    public void EditFine (View view){
        Intent intent = new Intent(this, EditAddTransaction.class);
        intent.putExtra("TRANSACTION_INDEX", -1);
        intent.putExtra("TRANSACTION_TYPE", TransactionType.FINE);
        startActivity(intent);
    }

    public void Connect(View view) {
        CreateReserve reserve = new CreateReserve();

        reserve.execute("Username", "Display Name", "user@website.com", "password");
    }

    public void GoHome (View view) {
        Intent intent = new Intent(this,ReserveHome.class);
        startActivity(intent);
    }
}
