package com.bitallowance;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerDropItem extends AsyncTask<String, Void, Void> {

    private String data;
    private String _results;
    private static String TAG = "BADDS";
    private String host = "http://bitallowance.hybar.com";
    //assume user is saving a Transaction by default
    private ListItem _currentItem;

    public ServerDropItem(ListItem item) {
        if (item.getType() == ListItemType.ENTITY){
            data = "dropEntity&entPK=" + item.getItemID();
        } else {
            data = "dropTransaction&txnPK=" +item.getItemID();
        }
        Log.d(TAG, "ServerDropItem: data = " + data);
        this.data = data;
        this._currentItem = item;
    }

    @Override
    protected Void doInBackground(String... params) {

        HttpURLConnection connection = null;
        URL url;
        try {
            if (_currentItem.getType() == ListItemType.ENTITY) {
                url = new URL(this.host + "/dropEntity.php");
            } else {
                url = new URL(this.host + "/dropTransaction.php");
            }

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            OutputStream output = connection.getOutputStream();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
            writer.write(this.data);
            writer.flush();
            writer.close();
            output.close();


            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                InputStream input = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                String nextLine;
                String response = "";

                while ((nextLine = reader.readLine()) != null) {
                    response += nextLine;
                    Log.d("BADDS", "doInBackground: responseLine = " + nextLine);
                }

                reader.close();
                _results = response;

            }

        } catch (Exception e) {
            Log.d("BADDS", e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

    }
}
