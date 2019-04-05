package com.bitallowance;

import android.content.Context;
import android.content.Intent;
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

public class ServerUpdateListItem extends AsyncTask<String, Void, Void> {

    private Context context;
    private String data;
    private String _results;
    private static String TAG = "BADDS";
    private String host = "http://bitallowance.hybar.com";
    //assume user is saving a Transaction by default
    private ListItem _currentItem;

    public ServerUpdateListItem(Context context, String data, ListItem item) {
        this.context = context;
        this.data = data;
        this._currentItem = item;
    }

    @Override
    protected Void doInBackground(String... params) {

        HttpURLConnection connection = null;
        URL url;
        try{
            if(_currentItem.getType() == ListItemType.ENTITY) {
                url = new URL(this.host + "/updateEntity.php");
            } else {
                url = new URL(this.host + "/updateTransaction.php");
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


            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){

                InputStream input = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                String nextLine;
                String response = "";

                while ((nextLine = reader.readLine()) != null){
                    response += nextLine;
                    Log.d("BADDS", "doInBackground: responseLine = " + nextLine );
                }

                reader.close();
                _results = response;

            }

        } catch (Exception e) {
            Log.d("BADDS", e.getMessage() );
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        String toastMessage = "";

        try {

            //Do different things depending on whether we are logging in or registering.
            if (_currentItem.getType() == ListItemType.ENTITY) {
                int resultCode = Integer.parseInt(_results);
                if (resultCode > 0) {
                    int index = Reserve.get_entityList().indexOf(_currentItem);

                    if (index >= 0)
                        Reserve.get_entityList().get(index).setId(resultCode);
                    toastMessage = "Record Created";
                } else if (resultCode == 0) {
                    toastMessage = "Record Updated";
                } else if (resultCode == -1) {
                    toastMessage = "Error updating record";
                } else {
                    toastMessage = "Error creating record";
                }
            } else {
                int resultCode = Integer.parseInt(_results);
                if (resultCode > 0) {
                    int index = Reserve.get_transactionList().indexOf(_currentItem);

                    if (index >= 0)
                        Reserve.get_transactionList().get(index).set_id(String.valueOf(resultCode));
                    toastMessage = "Record Created";
                } else if (resultCode == 0) {
                    toastMessage = "Record Updated";
                } else if (resultCode == -1) {
                    toastMessage = "Error updating record";
                } else {
                    toastMessage = "Error creating record";
                }
            }
        } catch (Exception e) {
            toastMessage = "An Error Occurred";
        }

        Toast toast = Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT);
        toast.show();
    }
}
