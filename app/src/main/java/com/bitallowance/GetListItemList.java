package com.bitallowance;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.security.PrivateKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import android.os.AsyncTask;

public class GetListItemList extends AsyncTask<String, Integer, Void> {
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

    private Reserve _reserve;

    @Override
    protected void onPreExecute() {

    }

    private void setReserve(Reserve reserve) {
        _reserve = reserve;
    }

    private Transaction getTransaction(int read) {
        byte[] buffer = new byte[100];
        Transaction transaction = new Transaction();
        try {
            _out.write("_".getBytes());
            read = _in.read(buffer);// get id
            int idInt = (buffer[0] << 24 |
                    (buffer[1] & 0xFF) << 16 |
                    (buffer[2] & 0xFF) << 8 |
                    (buffer[3] & 0xFF));
            transaction._id = Integer.toString(idInt);
            _out.write("_".getBytes());
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
        } catch(IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return transaction;
    }

    private void getTransactionList() {
        if (_reserve == null) {
            Log.e("GetListItemList", "Reserve cannot be null. Be sure to call \"SetReserve\"");
            return;
        }
        try {
            int idNum = _reserve.get_id();
            byte[] idByte = new byte[] {(byte) idNum,
                    (byte) (idNum >> 8),
                    (byte) (idNum >> 16),
                    (byte) (idNum >> 24)};
            _out.write(idByte); // ID
            byte[] countBytes = new byte[4];
            int read = _in.read(countBytes);
            int count = countBytes[3] & 0xFF |
                    (countBytes[2] & 0xFF) << 8 |
                    (countBytes[1] & 0xFF) << 16 |
                    (countBytes[0] & 0xFF) << 24;
            if (count == 0)
                return;
            byte[] buffer = new byte[1];
            _out.write("_".getBytes());
            read = _in.read(buffer);
            while (buffer[0] == "u".getBytes()[0]) {
                getTransaction(read);
                read = _in.read(buffer);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private Entity getEntity(int read) {
        byte[] buffer = new byte[100];
        Entity entity = new Entity();
        try {
            _out.write("_".getBytes());
            read = _in.read(buffer);// get id
            int idInt = (buffer[0] << 24 |
                    (buffer[1] & 0xFF) << 16 |
                    (buffer[2] & 0xFF) << 8 |
                    (buffer[3] & 0xFF));
            entity.setId(idInt);
            _out.write("_".getBytes());
            _out.flush();
            read = _in.read(buffer);// value
            entity.updateBalance(new BigDecimal(new String(buffer)));
            String usernameSrt = entity.getName();
            _out.write("_".getBytes());
            _out.flush();
            entity.setUserName(new String(buffer));
            _out.write("_".getBytes());
            _out.flush();
            read = _in.read(buffer);// displayName
            entity.setDisplayName(new String(buffer));
            _out.write("_".getBytes());
            _out.flush();
            read = _in.read(buffer);// birthday
            Date birthdayDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(new String(buffer));
            entity.setBirthday(birthdayDate);
            _out.write("_".getBytes());
            _out.flush();
            read = _in.read(buffer);// email
            entity.setEmail(new String(buffer));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return entity;
    }

    private void getEntityList() {
        if (_reserve == null) {
            Log.e("GetListItemList", "Reserve cannot be null. Be sure to call \"SetReserve\"");
            return;
        }
        try {
            int idNum = _reserve.get_id();
            byte[] idByte = new byte[] {(byte) idNum,
                    (byte) (idNum >> 8),
                    (byte) (idNum >> 16),
                    (byte) (idNum >> 24)};
            _out.write(idByte); // ID
            byte[] countBytes = new byte[4];
            int read = _in.read(countBytes);
            int count = countBytes[3] & 0xFF |
                    (countBytes[2] & 0xFF) << 8 |
                    (countBytes[1] & 0xFF) << 16 |
                    (countBytes[0] & 0xFF) << 24;
            if (count == 0)
                return;
            byte[] buffer = new byte[1];
            _out.write("_".getBytes());
            read = _in.read(buffer);
            while (buffer[0] == "u".getBytes()[0]) {
                getEntity(read);
                read = _in.read(buffer);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
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
            Log.d("Get List Item", "accepting new");
            ByteArrayOutputStream serverKeyByteStream = new ByteArrayOutputStream();
            byte[] serverKeyBytes = new byte[426];
            int read = _in.read(serverKeyBytes, 0, 426);
            String serverPEMKey = new String(serverKeyBytes);
            Log.d("Read", Integer.toString(read));
            Log.d("Get List Item", serverPEMKey);

            String nul = "\0";
            _out.write(_pubKeyString.getBytes());
            read = _in.read();

            if (_item instanceof Transaction) {
                Log.d("Get Transaction", _pubKeyString);
                _out.write("gt\0".getBytes());// 'g'et 't'ransaction
                read = _in.read();
                getTransactionList();
            } else if (_item instanceof Entity) {
                Log.d("Get Entity", _pubKeyString);
                _out.write("ge\0".getBytes());// 'g'et 'e'tity
                read = _in.read();
                getEntityList();
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
