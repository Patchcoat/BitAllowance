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
        EditText txtPassword = findViewById(R.id.passwordInputMain);
        EditText txtUsername = findViewById(R.id.usernameInputMain);
        String username = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();

        if (Reserve.serverIsPHP) {
            //build the string to send to the server
            String data = "login&username=" + username + "&password=" + password;
            new ServerSignIn(this, data).execute("login");
        } else {
            Login login = new Login();
            login.SetContext(this);
            login.execute(password);
        }
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
