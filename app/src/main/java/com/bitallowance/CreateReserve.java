package com.bitallowance;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.SyncStateContract;
import android.util.Base64;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;

public class CreateReserve extends AsyncTask<String, Integer, Void> {

    // the socket is the connection to the server
    private Socket _socket;
    // the reader and writer are both connected to the socket and used to read from/write to the
    // server
    //private DataOutputStream _out;
    //private DataInputStream _in;
    private PrivateKey _priv;
    private PublicKey _pub;
    // the port is the door use to connect to the sever
    private static final int SERVER_PORT = 3490;
    // the address of the server
    private static final String SERVER_IP = "107.174.13.151";

    private Context _context;

    public void SetContext(Context _context) {
        this._context = _context;
    }

    private static HashMap<String, byte[]> encrypt(String input, String password) {
        SecureRandom random = new SecureRandom();
        byte salt[] = new byte[256];
        random.nextBytes(salt);
        HashMap<String, byte[]> map = new HashMap<String, byte[]>();

        try {
            char[] passwordChar = password.toCharArray();
            PBEKeySpec pbeKeySpec = new PBEKeySpec(passwordChar, salt, 1324, 256);
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            byte[] keyBytes = secretKeyFactory.generateSecret(pbeKeySpec).getEncoded();
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            SecureRandom ivRandom = new SecureRandom();

            byte[] iv = new byte[16];
            ivRandom.nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            byte[] encrypted = cipher.doFinal(input.getBytes());
            map.put("salt", salt);
            map.put("iv", iv);
            map.put("encrypted", encrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    private void generateKeyPair(String password) {
        // generate the public and private key pair
        try {
            // prepare the key generator
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");

            // generate some secure randomness
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            keyGen.initialize(2048, random);

            // generate the key pair
            KeyPair pair = keyGen.generateKeyPair();
            _priv = pair.getPrivate();
            _pub = pair.getPublic();
            FileOutputStream fosPub = _context.openFileOutput("publicKey.dat", Context.MODE_PRIVATE);
            FileOutputStream fosPriv = _context.openFileOutput("privateKey.dat", Context.MODE_PRIVATE);

            HashMap<String, byte[]> pubMap = encrypt(_pub.toString(), password);
            HashMap<String, byte[]> privMap = encrypt(_pub.toString(), password);

            ObjectOutputStream oosPub = new ObjectOutputStream(fosPub);
            ObjectOutputStream oosPriv = new ObjectOutputStream(fosPriv);
            oosPub.writeObject(pubMap);
            oosPriv.writeObject(privMap);
            oosPub.close();
            oosPriv.close();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Void doInBackground(String... strings) {
        String username = strings[0];
        String displayName = strings[1];
        String email = strings[2];
        String password = strings[3]; // DO NOT SEND TO SERVER DO NOT WRITE TO DISK
        generateKeyPair(password);

        // The server connection section
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
            _out.write('c');
            _out.flush();

            // read the public key into a byte array
            Log.d("Create Reserve", "accepting new");
            ByteArrayOutputStream serverKeyByteStream = new ByteArrayOutputStream();
            byte[] serverKeyBytes = new byte[426];
            int read = _in.read(serverKeyBytes, 0, 426);
            String serverPEMKey = new String(serverKeyBytes);
            Log.d("Read", Integer.toString(read));
            Log.d("Create Reserve", serverPEMKey);

            // encode the data into a usable format
            //X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.decode(serverPEMKey, Base64.DEFAULT));
            // rev up the RSA algorithm for generating the public key
            //KeyFactory fact = KeyFactory.getInstance("RSA");
            // generate the public key from the encoded data
            //RSAPublicKey serverPublic = (RSAPublicKey) fact.generatePublic(spec);
            //Log.d("Create Reserve", serverPublic.toString());

            // rev up the cipher
            //Cipher encryptCipher = Cipher.getInstance("RSA");
            //encryptCipher.init(Cipher.ENCRYPT_MODE, serverPublic);

            // Send the packets to the server, reading so it knows when the server says it's ready
            // for the next packet
            _out.write(_pub.toString().getBytes());
            _out.flush();
            read = _in.read();
            Log.d("Create Reserve", _pub.toString());
            _out.write((username + "\n").getBytes());
            _out.flush();
            read = _in.read();
            Log.d("Create Reserve", username);
            _out.write((displayName + "\n").getBytes());
            _out.flush();
            read = _in.read();
            Log.d("Create Reserve", displayName);
            _out.write((email + "\n").getBytes());
            _out.flush();
            read = _in.read();
            Log.d("Create Reserve", email);
            //_out.flush();
            Log.d("Create Reserve", "Flushed");

            // rev up a different cipher
            //Cipher decryptCipher = Cipher.getInstance("RSA");
            //decryptCipher.init(Cipher.DECRYPT_MODE, serverPublic);

            byte[] idBytes = new byte[4];
            Log.d("Create Reserve", "receiving id");
            _in.read(idBytes);
            // convert byte array to integer
            int id = ((idBytes[0] & 0xFF) << 24 |
                    (idBytes[1] & 0xFF) << 16 |
                    (idBytes[2] & 0xFF) << 8 |
                    (idBytes[3] & 0xFF));
            Log.d("Create Reserve ID", String.valueOf(id));

            Log.d("Create Reserve", "start closing things");
            _out.close();
            _in.close();
            _socket.close();
            Log.d("Create Reserve", "closed everything");
            SharedPreferences preferences = _context.getSharedPreferences("com.bitallowance", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("AccountID", id);
            editor.apply();
            Log.d("Create Reserve", "Saved ID");
            _context = null;
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

    }
}
