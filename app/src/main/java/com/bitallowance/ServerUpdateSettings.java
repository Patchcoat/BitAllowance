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

public class ServerUpdateSettings extends AsyncTask<String, Void, Void> {

    private Context context;
    private String data;
    private String _results;
    private static String TAG = "BADDS";
    private String host = "http://bitallowance.hybar.com";
    //assume user is logging in by default

    public ServerUpdateSettings(Context context, String data) {
        this.context = context;
        this.data = data;
    }

    @Override
    protected Void doInBackground(String... params) {

        HttpURLConnection connection = null;
        URL url;
        try{
            url = new URL(this.host + "/updateSettings.php");

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

      //  int resultCode = Integer.parseInt(_results);
        String toastMessage = "";

        Toast toast = Toast.makeText(context, _results, Toast.LENGTH_SHORT);
        toast.show();

    }
}
