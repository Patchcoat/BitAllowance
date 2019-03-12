package com.bitallowance;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * ReserveHome Class
 * @author Dustin Christensen
 * @version 1.0
 * This class will be used to populate the Recycler Views on the ReserveHome page.
 * @since 02/25/2019
 */

public class ReserveHome extends AppCompatActivity {

    //RecyclerView variables
    private RecyclerView entityView;
    private RecyclerView taskView;
    private RecyclerView rewardView;

    private RecyclerView.Adapter mEntityList;
    private RecyclerView.Adapter mTaskList;
    private RecyclerView.Adapter mRewardList;

    private RecyclerView.LayoutManager entityLayoutManager;
    private RecyclerView.LayoutManager taskLayoutManager;
    private RecyclerView.LayoutManager rewardLayoutManager;

    private List entityList;
    private List taskList;
    private List rewardList;

    private Filter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_home);
        entityList = new ArrayList();
        taskList = new ArrayList();
        rewardList = new ArrayList();

        //The RecyclerView
        entityView = findViewById(R.id.entityView);
        taskView = findViewById(R.id.taskView);
        rewardView = findViewById(R.id.rewardView);

        //Layout Manager
        entityLayoutManager = new LinearLayoutManager(this);
        taskLayoutManager = new LinearLayoutManager(this);
        rewardLayoutManager = new LinearLayoutManager(this);

        entityView.setLayoutManager(entityLayoutManager);
        taskView.setLayoutManager(taskLayoutManager);
        rewardView.setLayoutManager(rewardLayoutManager);

        //Adapter
        mEntityList = new ViewList(entityList);
        entityView.setAdapter(mEntityList);
        mTaskList = new ViewList(taskList);
        taskView.setAdapter(mTaskList);
        mRewardList = new ViewList(rewardList);
        rewardView.setAdapter(mRewardList);
    }

    public void openSettings(View view) {

    }

    public void openEntityList(View view) {

    }

    public void openTaskList(View view) {

    }

    public void openRewardList(View view) {

    }

    public void openGiveReward(View view) {

    }

    public void onClickCreate(View view) {
        Context context = this.getApplicationContext();
        MyTask myTask = new MyTask();
        myTask.execute(true);
    }

    private class MyTask extends AsyncTask<Boolean, Integer, ArrayList> {

        @Override
        protected ArrayList doInBackground(Boolean... booleans) {

            if (booleans[0])
                saveFile();
            else
                loadFile();

            return null;
        }

        private void saveFile()
        {
            FileOutputStream outStream;
            Random random = new Random();
            String fileContents = "";
            int value;
            try{
                outStream = openFileOutput("testFile.txt", Context.MODE_PRIVATE);
                for (int i = 1; i <= 10; i++)
                {
                    value = random.nextInt(1900000000);
                    fileContents += value + "\n";
                    publishProgress(i, value, 1);
                    Thread.sleep(250);
                }
                outStream.write(fileContents.getBytes());
                outStream.close();
            }
            catch (Exception e)
            {
                Log.d("DMB: saveFile()", "File save failed!");
            }

        }

        private void loadFile()
        {
            FileInputStream inStream;
            int value;
            try{
                inStream = openFileInput("testFile.txt");
                InputStreamReader reader = new InputStreamReader(inStream);
                BufferedReader buffer = new BufferedReader(reader);
                for (int i = 1; i <= 10; i++)
                {
                    value = Integer.parseInt(buffer.readLine());
                    publishProgress(i, value, 0);
                    Thread.sleep(250);
                }
                inStream.close();
            }
            catch (Exception e)
            {
                Log.d("DMB: loadFile()", "File load failed!");
            }
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            if (values[2] == 1) {
                entityList.add("Here is a list" + values[0]);
                taskList.add("Just for fun" + values[1]);
                rewardList.add("Creating Key " + values[0] + ": x00" + values[1]);
            }
            else {
                rewardList.add("Loading Key " + values[0] + ": x00" + values[1]);
            }

            mEntityList.notifyDataSetChanged();
            mTaskList.notifyDataSetChanged();
            mRewardList.notifyDataSetChanged();

            entityView.smoothScrollToPosition(entityList.size()-1);
            taskView.smoothScrollToPosition(taskList.size()-1);
            rewardView.smoothScrollToPosition(rewardList.size()-1);
        }
    }
}
