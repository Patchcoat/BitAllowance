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

/**
 * This class handles logic for both signing in and registering a new Reserve Account.
 */
public class ServerSignIn extends AsyncTask<String, Void, Void> {

    private Context context;
    private String data;
    private boolean success = false;
    private String _results;
    private static String TAG = "BADDS";
    private String host = "http://bitallowance.hybar.com";
    //assume user is logging in by default
    private boolean isLogin = true;

    public ServerSignIn(Context context, String data) {
        this.context = context;
        this.data = data;
    }

    @Override
    protected Void doInBackground(String... params) {

        HttpURLConnection connection = null;
        URL url;
        try{
            if(params[0].equals("login")) {
                url = new URL(this.host + "/login.php");
            } else if(params[0].equals("register")) {
                url = new URL(this.host + "/register.php");
                //The user is not logging in
                isLogin = false;
            } else
            {
                Log.e(TAG, "doInBackground: Unexpected param value");
                url = new URL(this.host + "/login.php");
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

                if (response.equals("1"))
                    this.success = true;

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

        Toast toast = Toast.makeText(context, _results, Toast.LENGTH_SHORT);
        toast.show();
    }
}
