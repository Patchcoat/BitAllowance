package com.bitallowance;

import android.content.Context;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class ServerLoadListItems  extends AsyncTask<String, Void, Void> {

    private Context context;
    private String data;
    private String _results;
    private static String TAG = "BADDS";
    private String host = "http://bitallowance.hybar.com";
    //assume user is saving a Transaction by default
    private ListItemType _type;

    public ServerLoadListItems(Context context, String data, ListItemType type) {
        this.context = context;
        this.data = data;
        this._type = type;
    }

    @Override
    protected Void doInBackground(String... params) {

        HttpURLConnection connection = null;
        URL url;
        try{
            if(_type == ListItemType.ENTITY) {
                url = new URL(this.host + "/loadEntities.php");
            } else {
                url = new URL(this.host + "/loadTransactions.php");
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

        String toastMessage = _results;

        try {

            //Do different things depending on whether we are logging in or registering.
            if (_type == ListItemType.ENTITY) {
                JSONArray jsonArray = new JSONArray(_results);
                for (int i = 0; i < jsonArray.length(); i++){
                    Entity entity = new Entity();
                    JSONObject object = jsonArray.getJSONObject(i);
                    entity.setId(object.getInt("id"));
                    entity.setUserName("");
                    entity.setDisplayName(object.getString("displayName"));
                    entity.setBirthday(new Date());
                    entity.setEmail(object.getString("email"));
                    entity.setCashBalance(object.getDouble("cashBalance"));
                    Reserve.get_entityList().add(entity);
                }
            } else {
                JSONArray jsonArray = new JSONArray(_results);
                for (int i = 0; i < jsonArray.length(); i++){
                    Transaction transaction = new Transaction();
                    JSONObject object = jsonArray.getJSONObject(i);
                    transaction.set_id(object.getString("id"));
                    transaction.setName(object.getString("name"));
                    transaction.setType(object.getString("type"));
                    transaction.setValue(object.getString("value"));
                    transaction.setMemo(object.getString("memo"));
                    if (object.getInt("expirable") == 1){
                        transaction.setIsExpirable(true);
                    } else {
                        transaction.setIsExpirable(false);
                    }
                    transaction.setExpirationDate(new Date());
                    if (object.getInt("repeatable") == 1){
                        transaction.setIsRepeatable(true);
                    } else {
                        transaction.setIsExpirable(false);
                    }
                    transaction.setCoolDown(object.getInt("coolDown"));
                    Reserve.get_transactionList().add(transaction);
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "onPostExecute: " + e.getMessage());
            toastMessage = "An Error Occurred";
        }

        Toast toast = Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT);
        toast.show();
    }
}
