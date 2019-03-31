package com.bitallowance;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class UpdateListItem extends AsyncTask<String, Integer, Void> {
    // the socket is the connection to the server
    private Socket _socket;
    // the reader and writer are both connected to the socket and used to read from/write to the
    // server
    //private DataOutputStream _out;
    //private DataInputStream _in;
    private PrivateKey _priv;
    private String _pubKeyString;
    // the port is the door use to connect to the sever
    private static final int SERVER_PORT = 3490;
    // the address of the server
    private static final String SERVER_IP = "107.174.13.151";
    // the item to update
    ListItem _item;
    DataOutputStream _out;
    DataInputStream _in;

    private Context _context;

    public void SetContext(Context _context) {
        this._context = _context;
    }

    private void getKeyPair() {
        //  get the public and private key pair
    }

    @Override
    protected void onPreExecute() {

    }

    protected void itemToUpdate(ListItem item) {
        _item = item;
    }

    protected void updateTransaction() {
        // write
        Transaction transaction = (Transaction)_item;
        try {
            int idNum = Integer.parseInt(transaction._id);
            byte[] idByte = new byte[] {(byte) idNum,
                    (byte) (idNum >> 8),
                    (byte) (idNum >> 16),
                    (byte) (idNum >> 24)};
            _out.write(idByte); // ID
            int read = _in.read();
            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss\0");
            df.setTimeZone(tz);
            String timestamp = df.format(transaction._timeStamp);
            Log.d("Timestamp", timestamp);
            _out.write((timestamp).getBytes());// timestamp
            _out.flush();
            Log.d("Update Transaction", String.valueOf(transaction._id));
            // if the transaction doesn't have a database id (0) that means it was just created
            // and it needs to get an ID, and the rest of the information is pushed to the server
            // if it does have an ID then depending on the timestamp either the local transaction
            // is updated or the transaction on the server is updated.
            byte[] updateType = new byte[1];
            read = _in.read(updateType, 0, 1);
            // update types
            // l = local update of transaction
            // r = remote update of transaction, or create the transaction on the server
            switch ((char) updateType[0]) {
                case 'l': //local update
                    byte[] buffer = new byte[100];
                    _out.write("_".getBytes()); // this line and the next one say "I'm ready"
                    _out.flush();
                    read = _in.read(buffer);// get value
                    String valueStr = buffer.toString();
                    transaction._value = new BigDecimal(valueStr);
                    _out.write("_".getBytes());
                    _out.flush();
                    read = _in.read(buffer);// get operator
                    char character = (char) buffer[0];
                    switch (character) {
                        case '+':
                            transaction._operator = Operator.ADD;
                            break;
                        case '/':
                            transaction._operator = Operator.DIVIDE;
                            break;
                        case '*':
                            transaction._operator = Operator.MULTIPLY;
                            break;
                        case '-':
                            transaction._operator = Operator.SUBTRACT;
                            break;
                    }
                    _out.write("_".getBytes());
                    _out.flush();
                    read = _in.read(buffer);// get type
                    char typeChar = (char) buffer[0];
                    switch (typeChar) {
                        case 'E':
                            transaction._transactionType = ListItemType.ENTITY;
                            break;
                        case 'F':
                            transaction._transactionType = ListItemType.FINE;
                            break;
                        case 'T':
                            transaction._transactionType = ListItemType.TASK;
                            break;
                        case 'R':
                            transaction._transactionType = ListItemType.REWARD;
                            break;
                        case 'A':
                            transaction._transactionType = ListItemType.ALL;
                            break;
                    }
                    _out.write("_".getBytes());
                    _out.flush();
                    read = _in.read(buffer);// get name
                    String nameStr = new String(buffer);
                    transaction._name = nameStr;
                    _out.write("_".getBytes());
                    _out.flush();
                    read = _in.read(buffer);// get memo
                    String memoStr = new String(buffer);
                    transaction._name = memoStr;
                    _out.write("_".getBytes());
                    _out.flush();
                    read = _in.read(buffer);// get linked
                    transaction._linked = (buffer[0] != 0);
                    _out.write("_".getBytes());
                    _out.flush();
                    read = _in.read(buffer);// get executed
                    transaction._executed = (buffer[0] != 0);
                    _out.write("_".getBytes());
                    _out.flush();
                    read = _in.read(buffer);// get expirable
                    transaction.setIsExpirable(buffer[0] != 0);
                    _out.write("_".getBytes());
                    _out.flush();
                    read = _in.read(buffer);// get expiration
                    String expirationDateStr = new String(buffer);
                    transaction.setExpirationDate(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(expirationDateStr));
                    _out.write("_".getBytes());
                    _out.flush();
                    read = _in.read(buffer);// get cool down
                    int coolDownInt = (buffer[0] << 24 |
                            (buffer[1] & 0xFF) << 16 |
                            (buffer[2] & 0xFF) << 8 |
                            (buffer[3] & 0xFF));
                    transaction.setCoolDown(coolDownInt);
                    _out.write("_".getBytes());
                    _out.flush();
                    read = _in.read(buffer);// get repeatable
                    transaction.setIsRepeatable(buffer[0] != 0);
                    _out.write("_".getBytes());
                    _out.flush();
                    byte[] affecCntBytes = new byte[4];
                    read = _in.read(buffer);// get affected count
                    _in.read(affecCntBytes, 0, 4);
                    // convert byte array to integer
                    int affectedCount = affecCntBytes[3] & 0xFF |
                            (affecCntBytes[2] & 0xFF) << 8 |
                            (affecCntBytes[1] & 0xFF) << 16 |
                            (affecCntBytes[0] & 0xFF) << 24;
                    for (int i = 0; i < affectedCount; i++) {
                        _out.write("_".getBytes());
                        _out.flush();
                        read = _in.read(buffer);
                        /* TODO separate from the buffer into the two values, affected/and assignments */
                    }
                    break;
                case 'r': // remote update
                    String value = transaction._value.toPlainString();
                    value += '\0';
                    _out.write(value.getBytes());// value
                    _out.flush();
                    read = _in.read();
                    char operator = '+';
                    switch (transaction._operator) {
                        case ADD:
                            operator = '+';
                            break;
                        case DIVIDE:
                            operator = '/';
                            break;
                        case MULTIPLY:
                            operator = '*';
                            break;
                        case SUBTRACT:
                            operator = '-';
                            break;
                    }
                    _out.write(operator);// operator
                    _out.flush();
                    read = _in.read();
                    char type = '|';
                    switch (transaction.getTransactionType()) {
                        case ENTITY:
                            type = 'E';
                            break;
                        case FINE:
                            type = 'F';
                            break;
                        case TASK:
                            type = 'T';
                            break;
                        case REWARD:
                            type = 'R';
                            break;
                        case ALL:
                            type = 'A';
                            break;
                    }
                    Log.d("Type", String.valueOf(type));
                    _out.write(type);// type
                    _out.flush();
                    read = _in.read();
                    _out.write(transaction._name.getBytes(Charset.defaultCharset()));// name
                    read = _in.read();
                    String memo = transaction._memo;
                    _out.write((memo).getBytes());// memo
                    _out.flush();
                    read = _in.read();
                    boolean linked = transaction._linked;
                    byte linkedByte = (byte) (linked ? 1 : 0);
                    Log.d("linked", String.valueOf(linkedByte));
                    _out.write(linkedByte);// linked
                    _out.flush();
                    read = _in.read();
                    boolean executed = transaction._executed;
                    byte executedByte = (byte) (linked ? 1 : 0);
                    _out.write(executedByte);// executed
                    _out.flush();
                    read = _in.read();
                    byte expirableByte = (byte) (transaction.isExpirable() ? 1 : 0);
                    _out.write(expirableByte);// expirable
                    _out.flush();
                    read = _in.read();
                    String expiration = df.format(transaction.getExpirationDate());
                    _out.write((expiration).getBytes());// expiration date
                    _out.flush();
                    read = _in.read();
                    int cool = transaction.getCoolDown();
                    byte[] coolByte = new byte[] {(byte) cool,
                            (byte) (cool >> 8),
                            (byte) (cool >> 16),
                            (byte) (cool >> 24)};
                    _out.write(coolByte);// cool down
                    _out.flush();
                    read = _in.read();
                    boolean repeat = transaction.isRepeatable();
                    byte repeatByte = (byte) (repeat ? 1 : 0);
                    _out.write(repeatByte);// repeat
                    _out.flush();
                    byte[] idBytes = new byte[4];
                    read = _in.read(idBytes);// get ID
                    idNum = idBytes[3] & 0xFF |
                            (idBytes[2] & 0xFF) << 8 |
                            (idBytes[1] & 0xFF) << 16 |
                            (idBytes[0] & 0xFF) << 24;
                    transaction._id = Integer.toString(idNum);
                    int count = transaction._affected.size();
                    _out.write(count);// affected count
                    _out.flush();
                    read = _in.read();
                    for (int i = 0; i < 0; i++) {// loop through each affected entity
                        int affected = transaction._affected.get(i).getId();
                        byte[] affectedidByte = new byte[] {(byte) affected,
                                (byte) (affected >> 8),
                                (byte) (affected >> 16),
                                (byte) (affected >> 24)};
                        _out.write(affectedidByte);// assignments
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
        } catch(IOException e) {
            e.printStackTrace();
        } catch(ParseException e) {
            e.printStackTrace();
        }
    }

    protected void updateEntity() {

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
        if (_context == null) {
            _pubKeyString = "sdf";
            return;
        }
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
    protected Void doInBackground(String... strings) {
        getKeyPair("password");

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
            _out = new DataOutputStream(_socket.getOutputStream());
            _in = new DataInputStream(_socket.getInputStream());

            // write to the buffered writer, then sends the packet with flush
            // we're telling the server we want to do some 'n'ormal communication
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
            _out.write(_pubKeyString.getBytes());
            read = _in.read();
            Log.d("Update Transaction", _pubKeyString);
            _out.write("ut\0".getBytes());// 'u'pdate 't'ransaction
            read = _in.read();

            if (_item instanceof Transaction) {
                updateTransaction();
            } else if (_item instanceof Entity) {
                updateEntity();
            }

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
