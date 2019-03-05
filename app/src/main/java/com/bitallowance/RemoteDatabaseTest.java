package com.bitallowance;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.net.Socket;

public class RemoteDatabaseTest extends AppCompatActivity {

    private String tag = "Network Socket";
    private Socket clientSocket = null;
    private BufferedReader input = null;
    private OutputStream out = null;
    private static final int SERVER_PORT = 3490;
    private static final String SERVER_IP = "107.174.13.151";

    public void Connect(View view) {
        CreateReserve reserve = new CreateReserve();

        reserve.execute("Username", "Display Name", "user@website.com", "password");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_database_test);
    }
}
