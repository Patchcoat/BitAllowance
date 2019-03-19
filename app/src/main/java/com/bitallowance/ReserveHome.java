package com.bitallowance;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.makeText;

public class ReserveHome extends AppCompatActivity implements ListItemClickListener {

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

    private List<ListItem> entityList;
    private List<ListItem> taskList;
    private List<ListItem> rewardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_home);
        entityList = new ArrayList();
        taskList = new ArrayList();
        rewardList = new ArrayList();

        entityList.addAll(Reserve.getListItems(ListItemType.ENTITY));
        taskList.addAll(Reserve.getListItems(ListItemType.TASK));
        rewardList.addAll(Reserve.getListItems(ListItemType.REWARD));


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
        mEntityList = new ListItemRecycleViewAdapter(this, this, entityList, ListItemRecycleViewAdapter.CardType.Normal);
        entityView.setAdapter(mEntityList);
        mTaskList = new ListItemRecycleViewAdapter(this, this, taskList, ListItemRecycleViewAdapter.CardType.Normal);
        taskView.setAdapter(mTaskList);
        mRewardList = new ListItemRecycleViewAdapter(this, this, rewardList, ListItemRecycleViewAdapter.CardType.Normal);
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

    @Override
    public void onRecyclerViewItemClick(int position, ListItemRecycleViewAdapter adapter) {
        Toast toast;
        if (adapter == mEntityList)
            toast = makeText(getApplicationContext(), "Selected " + entityList.get(position).getName(), Toast.LENGTH_SHORT);
        else if (adapter == mRewardList)
            toast = makeText(getApplicationContext(), "Selected " + rewardList.get(position).getName(), Toast.LENGTH_SHORT);
        else
            toast = makeText(getApplicationContext(), "Selected " + taskList.get(position).getName(), Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onListItemDialogClick(int position, ListItem item) {
        Toast toast = makeText(getApplicationContext(), "Selected option " + position, Toast.LENGTH_SHORT);
        toast.show();
    }

}
