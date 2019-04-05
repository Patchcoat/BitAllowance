package com.bitallowance;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import static com.bitallowance.ListItemType.FINE;
import static com.bitallowance.ListItemType.REWARD;
import static com.bitallowance.ListItemType.TASK;

public class ServerLoadAssignments  extends AsyncTask<String, Void, Void> {

    private static String TAG = "BADDS - LoadAssignments";
    private String data;
    private String _results;
    private String host = "http://bitallowance.hybar.com";
    //assume user is saving a Transaction by default
    private ListItem _item;

    public ServerLoadAssignments(String data, ListItem item) {
        this.data = data;
        this._item = item;
    }

    @Override
    protected Void doInBackground(String... params) {

        HttpURLConnection connection = null;
        URL url;
        try {

            url = new URL(this.host + "/loadAssignments.php");

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

        try {
            JSONArray jsonArray = new JSONArray(_results);
            Map<Entity, Boolean> newMap = new ArrayMap<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                Boolean bool;
                if (object.getInt("assigned") == 1) {
                    bool = true;
                } else {
                    bool = false;
                }

                if (Reserve.get_entityList().get(i).getId() == object.getInt("ent_fk")) {
                    newMap.put(Reserve.get_entityList().get(i), bool);
                }
            }
            Reserve.get_transactionList().get(Reserve.get_transactionList().indexOf(_item)).setAssignments(newMap);

        } catch (Exception e) {
            Log.e(TAG, "onPostExecute: " + e.getMessage());
        }


    }
}
