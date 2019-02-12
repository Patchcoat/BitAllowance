package com.bitallowance;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class RemoteDatabaseTest extends AppCompatActivity {

    private String tag = "Network Socket";
    private Socket socket = null;
    private DataInputStream input = null;
    private DataOutputStream out = null;
    private static final int SERVERPORT = 3490;
    private static final String SERVER_IP = "107.174.13.151";

    public void Connect(View view) {
        try {
            input = new DataInputStream(System.in);
            out = new DataOutputStream(socket.getOutputStream());
        } catch (UnknownHostException u) {
            Log.d(tag, u.toString());
        } catch (IOException i) {
            Log.d(tag, i.toString());
        }

        String line = "";
        if (input == null) {
            TextView textView = findViewById(R.id.textView);
            textView.setText("Error connecting");
            return;
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
        TextView textView = findViewById(R.id.textView);
        textView.setText(line);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_database_test);

        new Thread(new ClientThread()).start();
    }

    class ClientThread implements Runnable {
        @Override
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                socket = new Socket(serverAddr, SERVERPORT);
            } catch (UnknownHostException el) {
                el.printStackTrace();
            } catch (IOException el) {
                el.printStackTrace();
            }
        }
    }
}
