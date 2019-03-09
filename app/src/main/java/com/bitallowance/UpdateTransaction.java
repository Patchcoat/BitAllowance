package com.bitallowance;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

public class UpdateTransaction extends AsyncTask<String, Integer, Void> {
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

    private void getKeyPair() {
        //  get the public and private key pair
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Void doInBackground(String... strings) {
        getKeyPair();

        String id = strings[0];

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
            _out.write('n');
            _out.flush();

            // read the public key into a byte array
            Log.d("Update Transaction", "accepting new");
            ByteArrayOutputStream serverKeyByteStream = new ByteArrayOutputStream();
            byte[] serverKeyBytes = new byte[426];
            int read = _in.read(serverKeyBytes, 0, 426);
            String serverPEMKey = new String(serverKeyBytes);
            Log.d("Read", Integer.toString(read));
            Log.d("Update Transaction", serverPEMKey);

            String nul = "\0";
            _out.write(_pub.toString().getBytes());
            Log.d("Update Transaction", _pub.toString());
            _out.write("ut\n".getBytes());// 'u'pdate 't'ransaction
            read = _in.read();
            int idNum = Integer.parseInt(id);
            _out.write(idNum);
            // if the transaction doesn't have a database id (-1) that means it was just created
            // and it needs to get an ID, and the rest of the information is pushed to the server
            // if it does have an ID then depending on the timestamp either the local transaction
            // is updated or the transaction on the server is updated.
            _out.write(id.getBytes()); // TODO convert id to uint32_t
            byte[] updateType = new byte[1];
            read = _in.read(updateType, 0, 1);
            // update types
            // l = local update of transaction
            // r = remote update of transaction, or create the transaction on the server
            switch ((char)updateType[0]) {
                case 'l':
                    byte[] buffer = new byte[100];
                    _out.write("_".getBytes()); // this line and the next one say "I'm ready"
                    _out.flush();
                    byte[] idBytes = new byte[4];
                    read = _in.read(buffer);// get ID
                    idNum = idBytes[3] & 0xFF |
                            (idBytes[2] & 0xFF) << 8 |
                            (idBytes[1] & 0xFF) << 16|
                            (idBytes[0] & 0xFF) << 24;
                    // TODO get the local copy of the transaction from the ID
                    _out.write("_".getBytes());
                    _out.flush();
                    read = _in.read(buffer);// get name
                    _out.write("_".getBytes());
                    _out.flush();
                    read = _in.read(buffer);// get value
                    _out.write("_".getBytes());
                    _out.flush();
                    read = _in.read(buffer);// get operator
                    _out.write("_".getBytes());
                    _out.flush();
                    read = _in.read(buffer);// get timestamp
                    _out.write("_".getBytes());
                    _out.flush();
                    read = _in.read(buffer);// get memo
                    _out.write("_".getBytes());
                    _out.flush();
                    read = _in.read(buffer);// get linked
                    _out.write("_".getBytes());
                    _out.flush();
                    read = _in.read(buffer);// get executed
                    _out.write("_".getBytes());
                    _out.flush();
                    read = _in.read(buffer);// get expiration date
                    _out.write("_".getBytes());
                    _out.flush();
                    read = _in.read(buffer);// get cool down
                    _out.write("_".getBytes());
                    _out.flush();
                    byte[] affecCntBytes = new byte[4];
                    read = _in.read(buffer);// get affected count
                    _in.read(affecCntBytes, 0, 4);
                    // convert byte array to integer
                    int affectedCount = affecCntBytes[3] & 0xFF |
                            (affecCntBytes[2] & 0xFF) << 8 |
                            (affecCntBytes[1] & 0xFF) << 16|
                            (affecCntBytes[0] & 0xFF) << 24;
                    for (int i = 0; i < affectedCount; i++) {
                        _out.write("_".getBytes());
                        _out.flush();
                        read = _in.read(buffer);
                        /* TODO separate from the buffer into the two values, affected/and assignments */
                    }
                    break;
                case 'r':
                    // write
                    _out.write("name\n".getBytes());// name
                    _out.flush();
                    read = _in.read();
                    _out.write("value\n".getBytes());// value
                    _out.flush();
                    read = _in.read();
                    _out.write("operator\n".getBytes());// operator
                    _out.flush();
                    read = _in.read();
                    _out.write("timestamp\n".getBytes());// timestamp
                    _out.flush();
                    read = _in.read();
                    _out.write("memo\n".getBytes());// memo
                    _out.flush();
                    read = _in.read();
                    _out.write("linked\n".getBytes());// linked
                    _out.flush();
                    read = _in.read();
                    _out.write("executed\n".getBytes());// executed
                    _out.flush();
                    read = _in.read();
                    _out.write("expiration\n".getBytes());// expiration date
                    _out.flush();
                    read = _in.read();
                    _out.write("cool down\n".getBytes());// cool down
                    _out.flush();
                    read = _in.read();
                    _out.write("affected count\n".getBytes());// affected count
                    _out.flush();
                    read = _in.read();
                    for (int i = 0; i < 0; i++) {// loop through each affected entity
                        _out.write("affected\n".getBytes());// affected
                        _out.write("assignment\n".getBytes());// assignments
                        _out.flush();
                        read = _in.read();
                    }
                    break;
            }
            _out.flush();
            Log.d("Update Transaction", "Flushed");

            // confirm everything went well
            byte[] confirmBytes = new byte[100];
            Log.d("Update Transaction", "receiving confirmation");
            _in.read(confirmBytes, 0, 100);
            String confirm = new String(confirmBytes);
            Log.d("Update Transaction", confirm);

            Log.d("Update Transaction", "start closing things");
            _out.close();
            _in.close();
            _socket.close();
            Log.d("Update Transaction", "closed everything");
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
