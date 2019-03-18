package com.bitallowance;

import android.content.Context;
import android.content.Intent;
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

public abstract class ReserveHome extends AppCompatActivity {

    // LOG debug characteristic
    private static final String TAG = "BADGS-ReserveHome";

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

    // ReserverHome Private variables
    private List entityList;
    private List taskList;
    private List rewardList;

    private Filter filter;
    private TransactionType _transType;
    private int _index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Debug LOG
        Log.d(TAG, "onCreate=" + savedInstanceState);

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

        Context context = this.getApplicationContext();
        MyTask myTask = new MyTask();
        myTask.execute(true);

    }

    public void openSettings(View view) {
        // Debug LOG
        Log.d(TAG, "openSettings=" + view);

    }

    public void openEntityList(View view) {
        // Debug LOG
        Log.d(TAG, "openEntityList=" + view);

    }

    public void openTaskList(View view) {
        // Debug LOG
        Log.d(TAG, "openTaskList=" + view);

    }

    public void openRewardList(View view) {
        // Debug LOG
        Log.d(TAG, "openRewardList=" + view);

    }

    public void openGiveReward(View view) {
        // Debug LOG
        Log.d(TAG, "openGiveReward=" + view);

    }

    // AsyncTask that gathers the information to display the information
    private class MyTask extends AsyncTask<Boolean, Integer, ArrayList> {

        @Override
        protected ArrayList doInBackground(Boolean... booleans) {
            // Debug LOG
            Log.d(TAG, "doInBackground=" + booleans);

            return null;
        }



        public void CreateReserveList(View view) {
            //Intent intent = new Intent(Reserve, ReserveHome.class);
            int newIndex;

            //Find the next transaction with a matching type.
            for(newIndex = _index - 1; newIndex >= 0; newIndex--){
                if (Reserve.get_transactionList().get(newIndex).getTransactionType() == _transType){
                    break;
                }
            }

            //Pass the transaction type in case it's a new transaction
            // intent.putExtra("TRANSACTION_TYPE", _transType);
            //Pass the index (New transaction will be -1)
            // intent.putExtra("TRANSACTION_INDEX", newIndex);

            // startActivity(intent);

            //When they finally click "Done" it will finish() regardless of how many transactions they've edited
            finish();
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            // Debug LOG
            Log.d(TAG, "onProgressUpdate=" + values);

            entityList.add(Reserve.get_entityList());
            //if (Reserve.get_transactionList() == )
            taskList.add(Reserve.get_transactionList());
            rewardList.add("Creating Key " + values[0] + ": x00" + values[1]);

            mEntityList.notifyDataSetChanged();
            mTaskList.notifyDataSetChanged();
            mRewardList.notifyDataSetChanged();

            entityView.smoothScrollToPosition(entityList.size()-1);
            taskView.smoothScrollToPosition(taskList.size()-1);
            rewardView.smoothScrollToPosition(rewardList.size()-1);
        }
    }
}
