package com.bitallowance;

import android.os.AsyncTask;

public class SignIn extends AsyncTask<String, Integer, Void> {

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Void doInBackground(String... strings) {
        //TODO use the password to decrypt the local data

        //TODO call functions from the server to request an update to Entity List, Task List, Transaction List

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... data) {

    }

    @Override
    protected void onPostExecute(Void result){

    }
}
