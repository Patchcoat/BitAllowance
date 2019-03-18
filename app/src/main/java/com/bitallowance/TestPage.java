package com.bitallowance;

import android.content.Intent;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Random;

public class TestPage extends AppCompatActivity {

    /**
     * This class is meant to link to Activities we need to test
     * @param
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_page);
    }

    public void EditCurrency (View view){
        Intent intent = new Intent(this, EditAddCurrency.class);
        startActivity(intent);
    }

    public void EditEntity (View view){
        Intent intent = new Intent(this, EditAddEntity.class);
        intent.putExtra("ENTITY_INDEX", -1);
        startActivity(intent);
    }

    public void EditReward (View view){
        Intent intent = new Intent(this, EditAddTransaction.class);
        intent.putExtra("TRANSACTION_INDEX", -1);
        intent.putExtra("TRANSACTION_TYPE", ListItemType.REWARD);
        startActivity(intent);
    }

    public void EditTask (View view){
        Intent intent = new Intent(this, EditAddTransaction.class);
        intent.putExtra("TRANSACTION_INDEX", -1);
        intent.putExtra("TRANSACTION_TYPE", ListItemType.TASK);
        startActivity(intent);
    }

    public void EditFine (View view){
        Intent intent = new Intent(this, EditAddTransaction.class);
        intent.putExtra("TRANSACTION_INDEX", -1);
        intent.putExtra("TRANSACTION_TYPE", ListItemType.FINE);
        startActivity(intent);
    }

    public void ShowAllList (View view){
        Intent intent = new Intent(this, DisplayList.class);
        intent.putExtra("LIST_ITEM_TYPE", ListItemType.ALL);
        startActivity(intent);
    }
    public void ShowTaskList (View view){
        Intent intent = new Intent(this, DisplayList.class);
        intent.putExtra("LIST_ITEM_TYPE", ListItemType.TASK);
        startActivity(intent);
    }
    public void ShowRewardList (View view){
        Intent intent = new Intent(this, DisplayList.class);
        intent.putExtra("LIST_ITEM_TYPE", ListItemType.REWARD);
        startActivity(intent);
    }
    public void ShowFineList (View view){
        Intent intent = new Intent(this, DisplayList.class);
        intent.putExtra("LIST_ITEM_TYPE", ListItemType.FINE);
        startActivity(intent);
    }
    public void ShowEntityList (View view){
        Intent intent = new Intent(this, DisplayList.class);
        intent.putExtra("LIST_ITEM_TYPE", ListItemType.ENTITY);
        startActivity(intent);
    }

    public void Connect(View view) {
        CreateReserve reserve = new CreateReserve();
        reserve.SetContext(getApplicationContext());
        reserve.execute("Username", "Display Name", "user@website.com", "password");
    }

    public void ReserveHome (View view) {
        Intent intent = new Intent(this,ReserveHome.class);
        startActivity(intent);
    }

    /**
     * Generates 10 of each Entity, Task, Reward, Fine
     * @param view
     */
    public void generateTestData(View view){


        Random random = new Random();
        Date date = Calendar.getInstance().getTime();
        for (int i = 1; i <= 10; i++){

            Entity entity = new Entity();

            entity.setDisplayName("Child " + i);
            entity.setBirthday(new Date(date.getTime() - ((long)random.nextInt(315000000) * (long)random.nextInt(3000))));
            switch (random.nextInt(4)){
                case 1:
                    entity.setEmail("child" + i + "@gmail.com");
                    break;
                case 2:
                    entity.setEmail("child" + i + "@yahoo.com");
                    break;
                case 3:
                    entity.setEmail("child" + i + "@byui.edu");
                    break;
                default:
                    entity.setEmail("");
            }
            Reserve.addEntity(entity);

            Transaction task = new Transaction();
            Transaction reward = new Transaction();
            Transaction fine = new Transaction();

            task.setName("Task " + i);
            reward.setName("Reward " + i);
            fine.setName("Fine " + i);

            task.setValue(Float.toString((float)(random.nextInt(50))));
            reward.setValue(Float.toString((float)(random.nextInt(50))));
            fine.setValue(Float.toString((float)(random.nextInt(50))));

            task.setIsRepeatable(random.nextBoolean());
            reward.setIsRepeatable(random.nextBoolean());
            fine.setIsRepeatable(random.nextBoolean());

            switch (random.nextInt(4)){
                case 1:
                    task.setCoolDown(1);
                    reward.setCoolDown(1);
                    break;
                case 2:
                    task.setCoolDown(24);
                    reward.setCoolDown(24);
                    break;
                case 3:
                    task.setCoolDown(168);
                    reward.setCoolDown(168);
                    break;
                default:
                    task.setCoolDown(0);
                    reward.setCoolDown(0);
            }
            fine.setCoolDown(0);

            boolean randBool = random.nextBoolean();
            task.setIsExpirable(randBool);
            reward.setIsExpirable(randBool);
            fine.setIsExpirable(randBool);
            if (randBool){
                task.setExpirationDate(new Date(date.getTime() + ((long)random.nextInt(315000000) * (long)random.nextInt(300))) );
                reward.setExpirationDate(new Date(date.getTime() + ((long)random.nextInt(315000000) * (long)random.nextInt(300))) );
                fine.setExpirationDate(new Date(date.getTime() + ((long)random.nextInt(315000000) * (long)random.nextInt(300))) );
            } else {
                task.setExpirationDate(null);
                reward.setExpirationDate(null);
                fine.setExpirationDate(null);
            }

            task.setMemo("This is a randomly generated Task. This is Transaction number: " + ((3 * i) + 1));
            reward.setMemo("This is a randomly generated Reward. This is Transaction number: " + ((3 * i) + 2));
            fine.setMemo("This is a randomly generated Fine. This is Transaction number: " + ((3 * i) + 3));

            task.setTransactionType(ListItemType.TASK);
            reward.setTransactionType(ListItemType.REWARD);
            fine.setTransactionType(ListItemType.FINE);

            Map<Entity, Boolean> taskMap = new ArrayMap<>();
            Map<Entity, Boolean> rewardMap = new ArrayMap<>();
            Map<Entity, Boolean> fineMap = new ArrayMap<>();

            int temp = 0;
            for (Entity listItem: Reserve.get_entityList()){
                temp ++;
                ((ArrayMap<com.bitallowance.Entity, Boolean>) taskMap).put(listItem, random.nextBoolean());
                ((ArrayMap<com.bitallowance.Entity, Boolean>) rewardMap).put(listItem, random.nextBoolean());
                ((ArrayMap<com.bitallowance.Entity, Boolean>) fineMap).put(listItem, random.nextBoolean());
            }
            Log.e("DB_TEST", "CREATING DATA - RECORDS SAVED TO MAP" + temp);//entity.getName());
            task.setAssignments(taskMap);
            reward.setAssignments(rewardMap);
            fine.setAssignments(fineMap);


            Reserve.addTransaction(task);
            Reserve.addTransaction(reward);
            Reserve.addTransaction(fine);

        }

    }
}
