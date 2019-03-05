package com.bitallowance;

import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

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

    List entityList;
    List taskList;
    List rewardList;

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
}
