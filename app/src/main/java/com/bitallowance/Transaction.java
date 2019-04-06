package com.bitallowance;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.ViewParent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * Transaction Class
 * @author Doug Barlow
 * @version 1.2
 * This version combines both tasks and rewards into a single class
 * @since 2/25/2019
 */

public class Transaction implements ListItem{
    private static final String TAG = "BADDS-Transaction";
    String _id;
    BigDecimal _value;
    Operator _operator;
    Date _timeStamp;
    String _memo;
    boolean _linked;
    boolean _executed;
    ListItemType _transactionType;
    List<Entity> _affected = new ArrayList<>();
    String _name;
    private boolean _expirable;
    private Date _expirationDate;
    //CoolDown in hours
    private int _coolDown;
    private boolean _repeatable;
    Map<Entity, Boolean> _assignments;


    Transaction(){
        _timeStamp = Calendar.getInstance().getTime();
        _assignments = new ArrayMap<>();
    }

    Map <Entity, Boolean> getAssignments(){
        return _assignments;
    }
    void setAssignments (Map<Entity, Boolean> newMap){
        _assignments = new ArrayMap<>();
        _assignments.putAll(newMap);

    }
    void updateAssignment (Entity entity, Boolean bool){
        if (entity == null || _assignments.get(entity) == bool){
            return;
        }

        _assignments.put(entity, bool);

        if (Reserve.serverIsPHP && get_id() != "0") {
            String data;
            if (bool) {
                data = "updateAssignment&entPK=" + entity.getId() + "&txnPK=" + this.get_id() + "&assigned=1";
            } else {
                data = "updateAssignment&entPK=" + entity.getId() + "&txnPK=" + this.get_id() + "&assigned=0";
            }
            new ServerUpdateAssignments(data).execute();
        }
    }

    /**
     * Deletes entity from the assignment map.
     * **NOTE** this is not the same removing the assignment
     * @param entity the Entity to be deleted
     */
    void deleteEntity (Entity entity) {
        //Remove entity if exists
        if (_assignments.containsValue(entity))
            _assignments.remove(entity);
    }

    /**
     * Checks to see if a transaction has been assigned to a particular entity
     * @param entity the entity object being checked
     * @return boolean indicating whether or not the transaction is assigned.
     */
    boolean isAssigned(Entity entity){
        //Make sure key exists to avoid nullptr exception
        if (_assignments.containsKey(entity)) {
            return _assignments.get(entity);
        } else {
            Log.e(TAG, "isAssigned: ERROR - Not in MAP.  Entity - " + entity.getName() );
            return false;
        }
    }



    void execute(Entity entity) {
    //I think this function should take a list of Entities
    }
    int getCoolDown(){
        return _coolDown;
    }
    void setCoolDown(int coolDown){
        _coolDown = coolDown;
    }
    boolean isRepeatable() {
        return _repeatable;
    }
    void setIsRepeatable(boolean isRepeatable){
        _repeatable = isRepeatable;
    }
    boolean isExpirable(){
        return _expirable;
    }
    void setIsExpirable(boolean isExpirable){
        _expirable = isExpirable;
    }
    Date getExpirationDate(){
        return _expirationDate;
    }
    void setExpirationDate(Date date){
        _expirationDate = date;
    }


    void executeUnlink() {
    }

    void unExecute() {
    }

    void cancel() {

    }


    void reverse() {

    }

    void updateOnlyUnexecute() {

    }

    @Override
    public int getItemID(){
        return Integer.parseInt(_id);
    }

    /**
     * Getters & Setters Below
     */
    public ListItemType getTransactionType() {
        return _transactionType;
    }
    public void setTransactionType(ListItemType transactionType){
        _transactionType = transactionType;
        switch (transactionType){
            case REWARD:
            case FINE:
                _operator = Operator.SUBTRACT;
                break;
            case TASK:
                _operator = Operator.ADD;
                break;
            default:
                _operator = Operator.MULTIPLY;
        }
    }

    /**
     * A special setType function that takes a string rather than a Transaction Type
     * @param transactionType
     */
    public void setType (String transactionType){
        switch (transactionType){
            case "TASK":
                _transactionType = ListItemType.TASK;
                _operator = Operator.ADD;
                break;
            case "FINE":
                _transactionType = ListItemType.FINE;
                _operator = Operator.SUBTRACT;
                break;
            case "REWARD":
                _transactionType = ListItemType.REWARD;
                _operator = Operator.SUBTRACT;
                break;
        }
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public BigDecimal getValue() {
        return _value;
    }

    public void setValue(String value){
        _value = new BigDecimal(value);
    }

    public void set_value(BigDecimal _value) {
        this._value = _value;
    }

    public Operator get_operator() {
        return _operator;
    }

    public void set_operator(Operator _operator) {
        this._operator = _operator;
    }

    public Date get_timeStamp() {
        return _timeStamp;
    }

    public void set_timeStamp(Date _timeStamp) {
        this._timeStamp = _timeStamp;
    }

    public String getMemo() {
        return _memo;
    }

    public void setMemo(String _memo) {
        this._memo = _memo;
    }

    public boolean is_linked() {
        return _linked;
    }

    public void set_linked(boolean _linked) {
        this._linked = _linked;
    }

    public boolean is_executed() {
        return _executed;
    }

    public void set_executed(boolean _executed) {
        this._executed = _executed;
    }

    public void setName(String name) {
        _name = name;
    }

    @Override
    public String getName() {
        return _name;
    }
    @Override
    public String getCardPrimaryDetails()
    {
        String details = Reserve.get_currencySymbol();
        details += " " + getValue().toString();
        return details;
    }
    @Override
    public String getCardSecondaryDetails() {
        if (_expirable)
            return Reserve.dateToString(_expirationDate);
        else
            return "Does not expire";
    }
    @Override
    public float getSortableValue(){
        return _value.floatValue();
    }
    @Override
    public Date getSortableDate(){
        return _expirationDate;
    }

    @Override
    public ListItemType getType() {
        return _transactionType;
    }

    /**
     * Applies the current transaction to the specified ENTITY
     * @param item ListItem (Needs to be of type ENTITY)
     * @return Whether  or not transaction was successful
     * @throws IllegalArgumentException ListItem MUST be type ENTITY
     */
    @Override
    public boolean applyTransaction(ListItem item, Context context) {
        //You can't apply a transaction to another transaction.
        if(item.getType() != ListItemType.ENTITY){
            Log.e(TAG, "applyTransaction: ListItem item not of type ENTITY", new IllegalArgumentException());
        }

        //Work with a temporary entity
        Entity entity = (Entity)item;

        switch (_transactionType){
            case REWARD:
                //Can't go into the negatives for a Reward
                if (entity.getCashBalance().floatValue() < _value.floatValue())
                    return false;
            case FINE:
                //Fines can be applied even if balance is not high enough
                entity.updateBalance(_value, false);
                break;
            default: //default is Task
                entity.updateBalance(_value, true);
        }

        Transaction transactionRecord = this;
        transactionRecord.setExpirationDate(new Date());

        entity.addToHistory(transactionRecord);

        //Save item to server
        if (Reserve.serverIsPHP) {
            //build the string to send to the server
            String data = "updateEntity&resPK=" + Reserve.get_id() + "&entPK=" + entity.getId() + "&display=" + entity.getDisplayName() +
                    "&email=" + entity.getEmail() + "&birthday=" + Reserve.dateStringSQL(entity.getBirthday()) + "&balance=" + entity.getCashBalance().toString();
            new ServerUpdateListItem(context, data, entity).execute();
        } else {
            entity.update();
        }

        //Apply changes to Reserve Entity List.
        int index = Reserve.get_entityList().indexOf(item);
        Reserve.get_entityList().set(index, entity);
        return true;
    }

    @Override
    public List<ListItem> getAssignmentList() {
        List<ListItem> assignmentList = new ArrayList<>();
        for (Entity entity: Reserve.get_entityList()) {
            if(isAssigned(entity))
                assignmentList.add(entity);
        }
        return assignmentList;
    }

    @Override
    public void update() {
        UpdateListItem update = new UpdateListItem();
        update.itemToUpdate(this);
        update.execute(_id);
    }

    @Override
    public void delete() {
        Reserve.get_transactionList().remove(this);
        new ServerDropItem(this).execute();
    }
}
