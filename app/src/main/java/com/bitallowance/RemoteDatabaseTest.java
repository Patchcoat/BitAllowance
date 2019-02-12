package com.bitallowance;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class RemoteDatabaseTest extends AppCompatActivity {

    private String tag = "Network Socket";
    private Socket socket = null;
    private DataInputStream input = null;
    private DataOutputStream out = null;

    String connect(String address, int port) {
        try {
            socket = new Socket(address, port);
            Log.d(tag, "Connected");

            input = new DataInputStream(System.in);
            out = new DataOutputStream(socket.getOutputStream());
        } catch (UnknownHostException u) {
            Log.d(tag, u.toString());
        } catch (IOException i) {
            Log.d(tag, i.toString());
        }

        String line = "";
        if (input == null) {
            return "ERROR could not connect";
        }
        while (!line.equals("Over")) {
            try {
                line = input.readLine();
                out.writeUTF(line);
            } catch (IOException i) {
                Log.d(tag, i.toString());
            }
        }

        try {
            input.close();
            out.close();
            socket.close();
        } catch (IOException i) {
            Log.d(tag, i.toString());
        }
        return line;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_database_test);

        String line = connect( "107.174.13.151", 3490);
        TextView textView = findViewById(R.id.textView);
        textView.setText(line);
    }
}
