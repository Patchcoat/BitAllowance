package com.bitallowance;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

public class RemoteDatabaseTest extends AppCompatActivity {

    private String tag = "Network Socket";
    private Socket clientSocket = null;
    private BufferedReader input = null;
    private OutputStream out = null;
    private static final int SERVER_PORT = 3490;
    private static final String SERVER_IP = "107.174.13.151";

    public void Connect(View view) {
        new Thread(new ClientThread()).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_database_test);
    }

    class ClientThread implements Runnable {
        @Override
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                SocketAddress sockaddr = new InetSocketAddress(serverAddr, SERVER_PORT);
                clientSocket = new Socket();

                int timeout = 2000;
                clientSocket.connect(sockaddr, timeout);
            } catch (UnknownHostException el) {
                el.printStackTrace();
            } catch (IOException el) {
                el.printStackTrace();
            }

            try {
                String string = "Hello There!";
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                writer.write(string);
                writer.flush();
                Log.d(tag, "Sent \"Hello There!\"");
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String str;
                Log.d(tag, "Received,");
                while ((str = reader.readLine()) != null) {
                    Log.d(tag, str);
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
