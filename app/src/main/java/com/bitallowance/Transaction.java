package com.bitallowance;

import android.support.v4.util.ArrayMap;
import android.util.Log;

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
    List<Entity> _affected;
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
        if (entity == null)
            return;
        _assignments.put(entity, bool);
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

    /**
     * Getters & Setters Below
     */
    public ListItemType getTransactionType() {
        return _transactionType;
    }
    public void setTransactionType(ListItemType transactionType){
        _transactionType = transactionType;
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
            return _expirationDate.toString();
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
    public boolean applyTransaction(ListItem item) {
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
    }
}
