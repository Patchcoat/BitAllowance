package com.bitallowance;

import java.util.Date;
import java.util.List;

public class Task extends Transaction {

    String _name;
    Date _dueDate;
    Date _coolDown; //Is a date the best variable for this?
    boolean _isFine;
    List<Boolean> _active; //Why is this a list?
    //List<Entity> _assigned;


    void updateTask() {
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public Date get_dueDate() {
        return _dueDate;
    }

    public void set_dueDate(Date _dueDate) {
        this._dueDate = _dueDate;
    }

    public Date get_coolDown() {
        return _coolDown;
    }

    public void set_coolDown(Date _coolDown) {
        this._coolDown = _coolDown;
    }

    public boolean is_isFine() {
        return _isFine;
    }

    public void set_isFine(boolean _isFine) {
        this._isFine = _isFine;
    }

    public List<Boolean> get_active() {
        return _active;
    }

    public void set_active(List<Boolean> _active) {
        this._active = _active;
    }
}

