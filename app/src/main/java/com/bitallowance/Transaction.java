package com.bitallowance;

import android.support.v4.util.ArrayMap;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Transaction Class
 * @author Doug Barlow
 * @version 1.2
 * This version combines both tasks and rewards into a single class
 * @since 2/25/2019
 */

public class Transaction implements ListItem{
    String _id;
    BigDecimal _value;
    Operator _operator;
    Date _timeStamp;
    String _memo;
    boolean _linked;
    boolean _executed;
    TransactionType _transactionType;
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
    }

    Map <Entity, Boolean> getAssignments(){
        return _assignments;
    }
    void setAssignments (Map<Entity, Boolean> newMap){
        _assignments = new ArrayMap<>();
        _assignments.putAll(newMap);
    }
    void updateAssignment (Entity entity, Boolean bool){
        _assignments.put(entity, bool);
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

    void delete() {

    }

    void reverse() {

    }

    void update() {

    }

    void updateOnlyUnexecute() {

    }

    /**
     * Getters & Setters Below
     */
    public TransactionType getTransactionType() {
        return _transactionType;
    }
    public void setTransactionType(TransactionType transactionType){
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
        String details = Reserve.get_currencySymbol();
        details += " " + getValue().toString();
        return details;
    }
}
