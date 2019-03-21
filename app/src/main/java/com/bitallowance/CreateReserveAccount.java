package com.bitallowance;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

public class CreateReserveAccount extends AppCompatActivity {

    ProgressBar _passwordBar;
    static final String weakColor = "#EF271B";
    static final String midColor = "#F2AF29";
    static final String strongColor = "#62C370";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reserve_account);
        EditText password = (EditText)findViewById(R.id.createPassword);
        _passwordBar = (ProgressBar)findViewById(R.id.passwordStrengthBar);
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updatePasswordStrength(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    void updatePasswordStrength(CharSequence s) {
        String password = s.toString();
        int strength = password.getBytes().length * 8;
        strength = (int)(strength * 0.7);
        if (strength > 100) {
            strength = 100;
        } else if (strength > 80) {
            _passwordBar.getProgressDrawable().setColorFilter(Color.parseColor(strongColor), PorterDuff.Mode.SRC_IN);
        } else if (strength > 45) {
            _passwordBar.getProgressDrawable().setColorFilter(Color.parseColor(midColor), PorterDuff.Mode.SRC_IN);
        } else {
            _passwordBar.getProgressDrawable().setColorFilter(Color.parseColor(weakColor), PorterDuff.Mode.SRC_IN);
        }
        Log.d("Create Reserve", String.valueOf(strength));
        _passwordBar.setProgress(strength);
    }

    boolean checkPassword(String password){
        return password.matches("^(?=.*[0-9].*[0-9]).{8}$");
    }

    public void createReserve(View view){
        CreateReserve createReserve = new CreateReserve();
        createReserve.SetContext(getApplicationContext());
        EditText editTextUN = (EditText)findViewById(R.id.createUsername);
        EditText editTextDN = (EditText)findViewById(R.id.createDisplayName);
        EditText editTextEM = (EditText)findViewById(R.id.createEmail);
        EditText editTextPW = (EditText)findViewById(R.id.createPassword);
        EditText editTextPWV = (EditText)findViewById(R.id.verifyPassword);
        String username = editTextUN.getText().toString();
        String displayName = editTextDN.getText().toString();
        String email = editTextEM.getText().toString();
        String password = editTextPW.getText().toString();
        String passwordVerify = editTextPWV.getText().toString();
        if (checkPassword(password) && password == passwordVerify) {
            createReserve.execute(username, displayName, email, password);
        }
    }
}
