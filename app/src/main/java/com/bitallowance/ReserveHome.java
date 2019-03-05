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

    private RecyclerView.LayoutManager layoutManager;
    private List entityList;
    private List taskList;
    private List rewardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        entityList = new ArrayList();
        taskList = new ArrayList();
        rewardList = new ArrayList();

        //The RecyclerView
        entityView = findViewById(R.id.entityView);
        taskView = findViewById(R.id.taskView);
        rewardView = findViewById(R.id.rewardView);
        //Layout Manager
        layoutManager = new LinearLayoutManager(this);

        entityView.setLayoutManager(layoutManager);
        taskView.setLayoutManager(layoutManager);
        rewardView.setLayoutManager(layoutManager);

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
