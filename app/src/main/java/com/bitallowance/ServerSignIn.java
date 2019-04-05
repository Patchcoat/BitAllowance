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

/**
 * This class handles logic for both signing in and registering a new Reserve Account.
 */
public class ServerSignIn extends AsyncTask<String, Void, Void> {

    private Context context;
    private String data;
    private String _results;
    private static String TAG = "BADDS";
    private String host = "http://bitallowance.hybar.com";
    //assume user is logging in by default
    private boolean _isLogin = true;

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
                _isLogin = false;
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

        int resultCode = Integer.parseInt(_results);
        String toastMessage = "";

        //Do different things depending on whether we are logging in or registering.
        if (_isLogin) {
            if (resultCode > 0) {
                //Set Reserve ID = res_pk
                Reserve.set_id(resultCode);




                //Load Homepage
                Intent intent = new Intent(context, ReserveHome.class);
                intent.putExtra("RESERVE_ID", resultCode);
                context.startActivity(intent);
                toastMessage = "Login Successful";
            } else {
                toastMessage = "Incorrect username or password";
            }
        } else {
            // Handle code if we are registering a new reserve account.
            switch (resultCode) {
                case -1:
                    toastMessage = "An error has occurred";
                    break;
                case -2:
                    toastMessage = "That username has already been taken";
                    break;
                case -3:
                    toastMessage = "That email has already been registered";
                    break;
                case -4:
                    toastMessage = "Invalid Username";
                    break;
                case -5:
                    toastMessage = "Invalid email";
                    break;
                case -6:
                    toastMessage = "Invalid password";
                    break;
                case -7:
                    toastMessage = "Invalid display name";
                    break;
                default:
                    if (resultCode > 0) {
                        Reserve.set_id(resultCode);
                        //Defaults
                        Reserve.setCurrencyName("Bit-Bucks");
                        Reserve.set_currencySymbol("$");
                        Intent intent = new Intent(context, EditAddCurrency.class);
                        intent.putExtra("FIRST_TIME", true);
                        context.startActivity(intent);
                        toastMessage = "Account Created";
                    }
            }
        }
        Toast toast = Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT);
        toast.show();

    }
}
