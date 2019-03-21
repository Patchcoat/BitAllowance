package com.bitallowance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickLogin(View view) {
        Login login = new Login();
        login.SetContext(getApplicationContext());
        EditText editText = (EditText)findViewById(R.id.passwordInputMain);
        String password = editText.getText().toString();
        login.execute(password);
    }

    public void onClickCreateAccount(View view) {
        Intent intent = new Intent(this, CreateReserveAccount.class);
        startActivity(intent);
    }

    public void goToTestPage(View view){
        Intent intent = new Intent(this, TestPage.class);
        startActivity(intent);
    }
}
