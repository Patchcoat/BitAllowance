package com.bitallowance;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static android.widget.Toast.makeText;
import static com.bitallowance.ListItemType.ALL;
import static com.bitallowance.ListItemType.ENTITY;
import static com.bitallowance.ListItemType.FINE;
import static com.bitallowance.ListItemType.REWARD;
import static com.bitallowance.ListItemType.TASK;

public class ReserveHome extends AppCompatActivity implements ListItemClickListener, AdapterView.OnItemSelectedListener,
 ListItemSelectDialog.NestedListItemClickListener {

    private static final String TAG = "BADDS-ReserveHome";

    //RecyclerView variables
    private RecyclerView entityView;
    private RecyclerView taskView;
    private RecyclerView rewardView;
    private RecyclerView fineView;

    private RecyclerView.Adapter mEntityList;
    private RecyclerView.Adapter mTaskList;
    private RecyclerView.Adapter mRewardList;
    private RecyclerView.Adapter mFineList;

    private RecyclerView.LayoutManager entityLayoutManager;
    private RecyclerView.LayoutManager taskLayoutManager;
    private RecyclerView.LayoutManager rewardLayoutManager;
    private RecyclerView.LayoutManager fineLayoutManager;

    private List<ListItem> entityList;
    private List<ListItem> taskList;
    private List<ListItem> rewardList;
    private List<ListItem> fineList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_home);

        entityList = new ArrayList();
        taskList = new ArrayList();
        rewardList = new ArrayList();
        fineList = new ArrayList();


        entityList.addAll(Reserve.getListItems(ListItemType.ENTITY));
        taskList.addAll(Reserve.getListItems(ListItemType.TASK));
        rewardList.addAll(Reserve.getListItems(ListItemType.REWARD));
        fineList.addAll(Reserve.getListItems(ListItemType.FINE));


        //The RecyclerView
        entityView = findViewById(R.id.entityView);
        taskView = findViewById(R.id.taskView);
        rewardView = findViewById(R.id.rewardView);
        fineView = findViewById(R.id.fineView);

        //Layout Manager
        entityLayoutManager = new LinearLayoutManager(this);
        taskLayoutManager = new LinearLayoutManager(this);
        rewardLayoutManager = new LinearLayoutManager(this);
        fineLayoutManager = new LinearLayoutManager(this);

        entityView.setLayoutManager(entityLayoutManager);
        taskView.setLayoutManager(taskLayoutManager);
        rewardView.setLayoutManager(rewardLayoutManager);
        fineView.setLayoutManager(fineLayoutManager);

        //Adapter
        mEntityList = new ListItemRecycleViewAdapter(this, this, entityList, ListItemRecycleViewAdapter.CardType.Normal);
        entityView.setAdapter(mEntityList);
        mTaskList = new ListItemRecycleViewAdapter(this, this, taskList, ListItemRecycleViewAdapter.CardType.Normal);
        taskView.setAdapter(mTaskList);
        mRewardList = new ListItemRecycleViewAdapter(this, this, rewardList, ListItemRecycleViewAdapter.CardType.Normal);
        rewardView.setAdapter(mRewardList);
        mFineList = new ListItemRecycleViewAdapter(this, this, fineList, ListItemRecycleViewAdapter.CardType.Normal);
        fineView.setAdapter(mFineList);

        if (Reserve.serverIsPHP) {
            //Load Entities
            if (Reserve.get_entityList().size() == 0) {
                String data = "loadEntities&reserveID=" + Reserve.get_id();
                new ServerLoadListItems(this, data, ListItemType.ENTITY).execute();
            }
            //Load TransactionsEntities
            if (Reserve.get_transactionList().size() == 0) {
                String data = "loadTransactions&reserveID=" + Reserve.get_id();
                new ServerLoadListItems(this, data, ListItemType.ALL).execute();
            }
        }

    }

    public void openSettings(View view) {

    }

    /**
     * Starts DisplayList activity for Task Items
     * @param view
     */
    public void ShowTaskList (View view){
        Intent intent = new Intent(this, DisplayList.class);
        intent.putExtra("LIST_ITEM_TYPE", ListItemType.TASK);
        startActivity(intent);
    }

    /**
     * Starts DisplayList activity for Reward Items
     * @param view
     */
    public void ShowRewardList (View view){
        Intent intent = new Intent(this, DisplayList.class);
        intent.putExtra("LIST_ITEM_TYPE", ListItemType.REWARD);
        startActivity(intent);
    }

    /**
     * Starts DisplayList activity for Fine Items
     * @param view
     */
    public void ShowFineList (View view){
        Intent intent = new Intent(this, DisplayList.class);
        intent.putExtra("LIST_ITEM_TYPE", ListItemType.FINE);
        startActivity(intent);
    }

    /**
     * Starts DisplayList activity for Entity Items
     * @param view
     */
    public void ShowEntityList (View view){
        Intent intent = new Intent(this, DisplayList.class);
        intent.putExtra("LIST_ITEM_TYPE", ListItemType.ENTITY);
        startActivity(intent);
    }


    /**
     * Starts EditAddEntity Activity
     * @param view
     */
    public void AddEntity (View view){
        Intent intent = new Intent(this, EditAddEntity.class);
        intent.putExtra("ENTITY_INDEX", -1);
        startActivityForResult(intent, 1);
    }

    /**
     * Starts EditAddTransaction Activity to add a reward
     * @param view
     */
    public void AddReward (View view){
        Intent intent = new Intent(this, EditAddTransaction.class);
        intent.putExtra("TRANSACTION_INDEX", -1);
        intent.putExtra("TRANSACTION_TYPE", ListItemType.REWARD);
        startActivityForResult(intent, 1);
    }

    /**
     * Starts EditAddTransaction Activity to add a task
     * @param view
     */
    public void AddTask (View view){
        Intent intent = new Intent(this, EditAddTransaction.class);
        intent.putExtra("TRANSACTION_INDEX", -1);
        intent.putExtra("TRANSACTION_TYPE", ListItemType.TASK);
        startActivityForResult(intent, 1);
    }

    /**
     * Starts EditAddTransaction Activity to add a fine
     * @param view
     */
    public void AddFine (View view) {
        Intent intent = new Intent(this, EditAddTransaction.class);
        intent.putExtra("TRANSACTION_INDEX", -1);
        intent.putExtra("TRANSACTION_TYPE", ListItemType.FINE);
        startActivityForResult(intent, 1);
    }


    /**
     * Reloads the recyclerView adapter for the specified listItem type
     * @param type
     */
    private void updateAdapter(ListItemType type) {
        switch (type) {
            case ENTITY:
                entityList.clear();
                entityList.addAll(Reserve.get_entityList());
                mEntityList.notifyDataSetChanged();
                return;
            case TASK:
                taskList.clear();
                taskList.addAll(Reserve.getListItems(TASK));
                mTaskList.notifyDataSetChanged();
                return;
            case FINE:
                fineList.clear();
                fineList.addAll(Reserve.getListItems(FINE));
                mFineList.notifyDataSetChanged();
                return;
            default:
                rewardList.clear();
                rewardList.addAll(Reserve.getListItems(REWARD));
                mRewardList.notifyDataSetChanged();
                return;
        }
    }

    /**
     * This function identifies the appropriate recycleView adapter to update based on the
     * selected item type.
     * @param item The selected item.
     * @return One of the memberVariable adapters.
     */
    private RecyclerView.Adapter getAdapter(ListItem item){
        switch (item.getType()){
            case ENTITY:
                return mEntityList;
            case TASK:
                return mTaskList;
            case FINE:
                return mFineList;
            default:
                return mRewardList;
        }
    }

    /**
     * This function identifies the appropriate ListItems list to work with based on the selected
     * item.
     * @param item The selected item
     * @return One of the member ListItem lists.
     */
    private List<ListItem> getCurrentList(ListItem item) {
        switch (item.getType()){
            case ENTITY:
                return entityList;
            case TASK:
                return taskList;
            case FINE:
                return fineList;
            default:
                return rewardList;
        }
    }

    /**
     * Callback function for ListItemClickListener. Handles onclick events for recycler view items.
     * @param position The index of the selected item.
     * @param adapter The recycleView adapter that registered the onClick event. Useful for activities with
     *                multiple recycleViews.
     */
    @Override
    public void onRecyclerViewItemClick(int position, ListItemRecycleViewAdapter adapter) {

        List<ListItem> currentList;
        if(adapter == mEntityList){
            currentList = entityList;
        } else if (adapter == mTaskList) {
            currentList = taskList;
        } else if (adapter == mRewardList){
            currentList = rewardList;
        } else {
            currentList = fineList;
        }

        /* * * * * EXAMPLE OF HOW TO IMPLEMENT A LIST_ITEM_SELECT_DIALOG * * * * */
        //Declare new Fragment Manager & ListItemSelectDialog
        FragmentManager fragmentManager = getSupportFragmentManager();
        ListItemSelectDialog selectDialog = new ListItemSelectDialog();

        //Create a bundle to hold title & menu options
        Bundle bundle = new Bundle();
        bundle.putString("TITLE", currentList.get(position).getName());

        //get selected item type to display different options depending on type.
        ListItemType clickType = currentList.get(position).getType();

        //Dynamically add display options to bundle
        //*Note* Options must be added as a String ArrayList
        switch (clickType){
            case ENTITY:
                bundle.putStringArrayList("OPTIONS", new ArrayList<>(Arrays.asList("View Details", "Apply Payment",
                        "Apply Reward", "Apply Fine", "Edit " + currentList.get(position).getName(),
                        "Delete "+ currentList.get(position).getName(), "Cancel")));
                break;
            case TASK:
                bundle.putStringArrayList("OPTIONS", new ArrayList<>(Arrays.asList("View Details", "Apply Payment",
                        "Edit " + currentList.get(position).getName(), "Delete "+ currentList.get(position).getName(), "Cancel")));
                break;
            case REWARD:
                bundle.putStringArrayList("OPTIONS", new ArrayList<>(Arrays.asList("View Details", "Apply Reward",
                        "Edit " + currentList.get(position).getName(), "Delete "+ currentList.get(position).getName(), "Cancel")));
                break;

            case FINE:
                bundle.putStringArrayList("OPTIONS", new ArrayList<>(Arrays.asList("View Details", "Apply Fine",
                        "Edit " + currentList.get(position).getName(), "Delete "+ currentList.get(position).getName(), "Cancel")));
                break;

        }

        //This is required
        /* * * * INITIALIZE & SET DIALOG ARGUMENTS DIALOG (2 - STEPS) * * * */
        //Step 1 - INITIALIZE - pass selected item and a listener. - This is required for onClick event to work.
        selectDialog.initialize(currentList.get(position), this);
        //Step 2 - SET ARGUMENTS - pass bundle to dialog with title and options to display
        selectDialog.setArguments(bundle);

        //Show the dialog
        selectDialog.show(fragmentManager, "");
    }

    /**
     * Handles onClick events for ListItem Dialogs
     * @param position Designates option selected (See "OPTIONS" array passed to dialog)
     * @param selectedItem Item to be affected.
     */
    @Override
    public void onListItemDialogClick(int position, final ListItem selectedItem) {

        if (selectedItem != null){

            if (selectedItem.getType() == ListItemType.ENTITY){
                switch (position) {
                    case 0:
                        displayDetails(selectedItem);
                        break;
                    case 1:
                        getItemsToApply(selectedItem, TASK);
                        break;
                    case 2:
                        getItemsToApply(selectedItem, REWARD);
                        break;
                    case 3:
                        getItemsToApply(selectedItem, FINE);
                        break;
                    case 4:
                        editItem(selectedItem);
                        break;
                    case 5:
                        confirmDelete(selectedItem);
                        break;
                }
            }
            else{
                switch (position) {
                    case 0:
                        displayDetails(selectedItem);
                        break;
                    case 1:
                        getItemsToApply(selectedItem, ENTITY);
                        break;
                    case 2:
                        editItem(selectedItem);
                        break;
                    case 3:
                        confirmDelete(selectedItem);
                        break;
                }
            }
        }
    }

    /**
     * Opens a new activity to allow the selected item to be edited
     * @param selectedItem the item to be edited
     */
    private void editItem(ListItem selectedItem){
        Intent intent;
        if (selectedItem.getType() == ENTITY) {
            intent = new Intent(this, EditAddEntity.class);
            intent.putExtra("ENTITY_INDEX", getCurrentList(selectedItem).indexOf(selectedItem));
        } else {
            intent = new Intent(this, EditAddTransaction.class);
            intent.putExtra("TRANSACTION_INDEX", Reserve.get_transactionList().indexOf(selectedItem));
            intent.putExtra("TRANSACTION_TYPE", selectedItem.getType());
        }
        startActivityForResult(intent,1);
    }

    /**
     * Opens a new activity that displays the details of the passed item.
     * @param selectedItem the item to be displayed
     */
    private void displayDetails(ListItem selectedItem){
        Intent intent = new Intent(this, DisplayDetails.class);
        intent.putExtra("INDEX",Reserve.getListItems(selectedItem.getType()).indexOf(selectedItem));
        intent.putExtra("TYPE", selectedItem.getType());
        startActivityForResult(intent,1);
    }

    /**
     * Prompts the user to confirm they want to delete the selected item. If the user confirms,
     * it deletes the object and updates the recyclerView
     * @param selectedItem the object to be deleted.
     */
    private void  confirmDelete(final ListItem selectedItem){
        //Confirm the user wants to delete the record
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete " + selectedItem.getName() + "?").setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedItem.delete();
                getCurrentList(selectedItem).remove(selectedItem);
                updateAdapter(selectedItem.getType());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        builder.show();
    }

    /**
     * Refreshes data in case of change
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        entityList.clear();
        entityList.addAll(Reserve.get_entityList());
        mEntityList.notifyDataSetChanged();

        taskList.clear();
        taskList.addAll(Reserve.getListItems(TASK));
        mTaskList.notifyDataSetChanged();

        fineList.clear();
        fineList.addAll(Reserve.getListItems(FINE));
        mFineList.notifyDataSetChanged();

        rewardList.clear();
        rewardList.addAll(Reserve.getListItems(REWARD));
        mRewardList.notifyDataSetChanged();
    }

    /**
     * Opens another dialogue fragment with a list of ListItems that can be applied to the
     * current selection.
     * @param item Currently selected item
     * @param typeToApply
     */
    private void getItemsToApply(ListItem item, ListItemType typeToApply){
        //Declare new Fragment Manager & ListItemSelectDialog
        FragmentManager fragmentManager = getSupportFragmentManager();
        ListItemSelectDialog selectDialog = new ListItemSelectDialog();

        //Create a bundle to hold title & menu options
        Bundle bundle = new Bundle();

        //Dynamically add display options to bundle
        //*Note* Options must be added as a String ArrayList

        switch (typeToApply){
            case ENTITY:
                bundle.putString("TITLE", "Who do you want to apply " + item.getName() + "to?");
                break;
            case TASK:
                bundle.putString("TITLE", "Which task would you like to apply to " + item.getName() + "?");
                break;
            case REWARD:
                bundle.putString("TITLE", "Which reward would you like to apply to " + item.getName() + "?");
                break;
            case FINE:
                bundle.putString("TITLE", "Which fine would you like to apply to " + item.getName() + "?");
                break;
        }

        //Get a dynamic list of items that can be applied
        ArrayList options = new ArrayList<>();

        if (typeToApply == ENTITY){
            Transaction tempTransaction = (Transaction) item;
            for (Entity entity : Reserve.get_entityList()){

                //Only add assigned entities to the options list.
                if (tempTransaction.isAssigned(entity)) {
                    options.add(entity.getName());
                }
                //Make sure the number of options is greater than 0
                if(options.size() == 0){
                    Toast toast = makeText(this, "Uh-oh. There is no-one assigned to that transaction.", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
            }
        } else {
            //Only add assigned transactions to the options list.
            Entity tempEntity = (Entity)item;
            for (Transaction transaction : tempEntity.getAssignedTransactions(typeToApply)) {
                options.add(transaction.getName());
            }
            //Make sure the number of options is greater than 0
            if(options.size() == 0){
                Toast toast = makeText(this, "Uh-oh. This person has no " + typeToApply + "s assigned to them.", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
        }


        options.add("Cancel");
        bundle.putStringArrayList("OPTIONS", options);

        //Initialize a NestedDialogue
        selectDialog.initializeNested(item, this, typeToApply);
        //Step 2 - SET ARGUMENTS - pass bundle to dialog with title and options to display
        selectDialog.setArguments(bundle);

        //Show the dialog
        selectDialog.show(fragmentManager, "");

    }

    /**
     * The callback function for nested ListItem dialogs. This handles onclick events for the nested
     * dialog fragments
     * @param position index of the option selected by the user.
     * @param selectedItem item to be affected
     * @param applyType type of item to be applied to selectedItem.
     */
    @Override
    public void onNestedListItemDialogClick(int position, ListItem selectedItem,ListItemType applyType) {
        String toastMessage;
        List<ListItem> options = new ArrayList<>();
        //applyType must list a specific type.
        if (applyType == null || applyType == ALL) {
            toastMessage = "An Error Occurred...";
        }else {
            try {
                //Get the current options list
                if (applyType == ENTITY){
                    Transaction tempTransaction = (Transaction) selectedItem;
                    for (Entity entity : Reserve.get_entityList()){
                        if (tempTransaction.isAssigned(entity)) {
                            options.add(entity);
                        }
                    }
                } else {
                    Entity tempEntity = (Entity) selectedItem;
                    options.addAll(tempEntity.getAssignedTransactions(applyType));
                }
                //This indicates that CANCEL was selected
                if (position == options.size()){
                    return;
                    //An item was selected
                } else if (selectedItem.applyTransaction(options.get(position), this )) {
                    toastMessage ="Transaction Applied";
                    //Only Entities should visibly change after a transaction has been applied.
                    updateAdapter(ENTITY);
                } else {
                    if (applyType == ENTITY) {
                        toastMessage = Reserve.getListItems(applyType).get(position).getName() +
                                " does not have enough " + Reserve.getCurrencyName() + " for that reward.";
                    } else {
                        toastMessage = selectedItem.getName() + " does not have enough " +
                                Reserve.getCurrencyName() + " for that reward.";
                    }
                }
            } catch (IllegalArgumentException e)
            {
                toastMessage = "Uh-Oh - An unexpected error occurred...";
            }
        }
        Toast toast = makeText(this, toastMessage, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class ServerLoadListItems  extends AsyncTask<String, Void, Void> {

        private Context context;
        private String data;
        private String _results;
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
            try {
                if (_type == ListItemType.ENTITY) {
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


                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    InputStream input = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                    String nextLine;
                    String response = "";

                    while ((nextLine = reader.readLine()) != null) {
                        response += nextLine;
                        Log.d("BADDS", "doInBackground: responseLine = " + nextLine);
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

                //Do different things depending on whether we are logging in or registering.
                if (_type == ListItemType.ENTITY) {
                    JSONArray jsonArray = new JSONArray(_results);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Entity entity = new Entity();
                        JSONObject object = jsonArray.getJSONObject(i);
                        entity.setId(object.getInt("id"));
                        entity.setUserName("");
                        entity.setDisplayName(object.getString("displayName"));
                        entity.setBirthday(Reserve.stringToDate(object.getString("birthday")));
                        entity.setEmail(object.getString("email"));
                        entity.setCashBalance(object.getDouble("cashBalance"));
                        Reserve.get_entityList().add(entity);

                        entityList.clear();
                        entityList.addAll(Reserve.get_entityList());
                        mEntityList.notifyDataSetChanged();
                    }
                } else {
                    JSONArray jsonArray = new JSONArray(_results);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Transaction transaction = new Transaction();
                        JSONObject object = jsonArray.getJSONObject(i);
                        transaction.set_id(object.getString("id"));
                        transaction.setName(object.getString("name"));
                        transaction.setType(object.getString("type"));
                        transaction.setValue(object.getString("value"));
                        transaction.setMemo(object.getString("memo"));
                        if (object.getInt("expirable") == 1) {
                            transaction.setIsExpirable(true);
                        } else {
                            transaction.setIsExpirable(false);
                        }
                        transaction.setExpirationDate(Reserve.stringToDate(object.getString("expireDate")));
                        if (object.getInt("repeatable") == 1) {
                            transaction.setIsRepeatable(true);
                        } else {
                            transaction.setIsExpirable(false);
                        }
                        transaction.setCoolDown(object.getInt("coolDown"));
                        Reserve.get_transactionList().add(transaction);

                        taskList.clear();
                        taskList.addAll(Reserve.getListItems(TASK));
                        mTaskList.notifyDataSetChanged();

                        fineList.clear();
                        fineList.addAll(Reserve.getListItems(FINE));
                        mFineList.notifyDataSetChanged();

                        rewardList.clear();
                        rewardList.addAll(Reserve.getListItems(REWARD));
                        mRewardList.notifyDataSetChanged();
                    }

                    for (Transaction transaction: Reserve.get_transactionList()) {

                        String data = "loadAssignments&txnPK=" + transaction.get_id();
                        new ServerLoadAssignments(data, transaction).execute();
                    }
                }

            } catch (Exception e) {
                Log.e(TAG, "onPostExecute: " + e.getMessage());
                Toast toast = Toast.makeText(context, "An Error Occurred", Toast.LENGTH_SHORT);
                toast.show();
            }


        }
    }
}
