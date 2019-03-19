package com.bitallowance;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import static android.content.Context.MODE_PRIVATE;

public class Login extends AsyncTask<String, Integer, Void> {
    // the socket is the connection to the server
    private Socket _socket;
    // the reader and writer are both connected to the socket and used to read from/write to the
    // server
    //private DataOutputStream _out;
    //private DataInputStream _in;
    private PrivateKey _priv;
    private PublicKey _pub;
    String _pubKeyString;
    // the port is the door use to connect to the sever
    private static final int SERVER_PORT = 3490;
    // the address of the server
    private static final String SERVER_IP = "107.174.13.151";
    // if the login was a success
    boolean _loginSuccessful;

    private Context _context;

    public void SetContext(Context _context) {
        this._context = _context;
    }

    private static String decrypt(HashMap<String, byte[]> map, String password) {
        String decrypted = new String();

        try {
            byte salt[] = map.get("salt");
            byte iv[] = map.get("iv");
            byte encrypted[] = map.get("encrypted");

            char[] passwordChar = password.toCharArray();
            PBEKeySpec pbeKeySpec = new PBEKeySpec(passwordChar, salt, 1324, 256);
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] keyBytes = secretKeyFactory.generateSecret(pbeKeySpec).getEncoded();
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            decrypted = new String(cipher.doFinal(encrypted));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return decrypted;
    }

    private void getKeyPair(String password) {
        try {
            FileInputStream fisPub = _context.openFileInput("publicKey.dat");
            FileInputStream fisPriv = _context.openFileInput("privateKey.dat");
            ObjectInputStream oisPub = new ObjectInputStream(fisPub);
            ObjectInputStream oisPriv = new ObjectInputStream(fisPriv);

            HashMap<String, byte[]> pubMap = (HashMap<String, byte[]>) oisPub.readObject();
            HashMap<String, byte[]> privMap = (HashMap<String, byte[]>) oisPriv.readObject();

            oisPub.close();
            oisPriv.close();

            String publicKeyString = decrypt(pubMap, password);
            String privateKeyString = decrypt(privMap, password);

            Log.d("Login", publicKeyString);
            _pubKeyString = publicKeyString;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Void doInBackground(String... strings) {
        String password = strings[0];
        getKeyPair(password);
        try {
            // the address has to be in the correct format for the socket to use it
            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
            SocketAddress sockaddr = new InetSocketAddress(serverAddr, SERVER_PORT);
            _socket = new Socket();

            // if there's a problem with the server this makes sure the thread doesn't hang
            int timeout = 2000;
            // connects the socket to the remote server
            _socket.connect(sockaddr, timeout);
            // instantiates the reader/writer
            DataOutputStream _out = new DataOutputStream(_socket.getOutputStream());
            DataInputStream _in = new DataInputStream(_socket.getInputStream());

            // write to the buffered writer, then sends the packet with flush
            // we're telling the server we want to 'c'reate an account
            _out.write('l');
            _out.flush();

            // read the public key into a byte array
            Log.d("Login", "accepting new");
            ByteArrayOutputStream serverKeyByteStream = new ByteArrayOutputStream();
            byte[] serverKeyBytes = new byte[426];
            int read = _in.read(serverKeyBytes, 0, 426);
            String serverPEMKey = new String(serverKeyBytes);
            Log.d("Read", Integer.toString(read));
            Log.d("Login", serverPEMKey);

            SharedPreferences preferences = _context.getSharedPreferences("AccountID", MODE_PRIVATE);
            int id = preferences.getInt("AccountID", 0);
            byte[] idBytes = new byte[] {(byte) id,
                    (byte) (id >> 8),
                    (byte) (id >> 16),
                    (byte) (id >> 24)};
            _out.write(idBytes);
            Log.d("Login", String.valueOf(id));
            read = _in.read();

            _out.write(_pubKeyString.getBytes());
            Log.d("Login","wrote public key to server");

            byte[] worked = new byte[1];
            read = _in.read(worked, 0, 1);
            String workedString = new String(worked);
            Log.d("Login", workedString);
            switch(workedString) {
                case "n":
                    _loginSuccessful = false;
                    return null;
                case "y":
                    _loginSuccessful = true;
                    break;
            }
            Log.d("Login", "Login " + _loginSuccessful);
            if (!_loginSuccessful) {
                _out.close();
                _in.close();
                _socket.close();
                return null;
            }

            _out.write("_".getBytes());

            byte[] usernameBytes = new byte[100];
            read = _in.read(usernameBytes, 0, 100);
            String username = new String(usernameBytes);
            Log.d("Login", username);


            _out.close();
            _in.close();
            _socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... data) {

    }

    @Override
    protected void onPostExecute(Void result) {
        Log.d("Login", "login " + _loginSuccessful);
        if (_loginSuccessful){
            // get the data from the server and fill the lists

            // go to a new activity
            Intent intent = new Intent(_context, ReserveHome.class);
            _context.startActivity(intent);
        } else {
            // display "wrong password" or something

        }
    }
}
