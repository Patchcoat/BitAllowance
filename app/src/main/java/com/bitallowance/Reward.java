package com.bitallowance;

import java.util.Date;
import java.util.List;

public class Reward extends Transaction {

    //All of these are identical to Task. Shouldn't we put them in transaction?

    String _name;
    Date _expirationDate;
    Date _coolDown;
    List<Boolean> _active;
    //List<Entity> _assigned;

    void updateReward(){

    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }
}
