package com.bitallowance;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Parent class for Reward & Task
 * @author Doug Barlow
 * @version 1.0
 * @since 2/25/2019
 */

public class Transaction {
    String _id;
    BigDecimal _value;
    Operator _operator;
    //Used Date instead of LocalDateTime because LocalDateTime requires API 28 and Android Pie OS
    Date _timeStamp;
    String _memo;
    boolean _linked;
    boolean _executed;
    //List<Entity> _affected;


    Transaction(){
        _timeStamp = Calendar.getInstance().getTime();
    }

    void execute() {
    //I think this function should take a list of Entities
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
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public BigDecimal get_value() {
        return _value;
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

    public String get_memo() {
        return _memo;
    }

    public void set_memo(String _memo) {
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

}
