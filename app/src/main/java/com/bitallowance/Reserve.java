package com.bitallowance;

import java.util.List;

public class Reserve {
    private static List<Entity> _entityList;
    private static List<Transaction> _transactionList;
    private static String _currencyName;
    private static String _currencySymbol;

    public static void addEntity(Entity newEntity){
        _entityList.add(newEntity);
    }

    public static void addTransaction(Transaction newTransaction){
        _transactionList.add(newTransaction);
    }

    public static List<Entity> get_entityList() {
        return _entityList;
    }

    public static List<Transaction> get_transactionList() {
        return _transactionList;
    }

    public static String get_currencySymbol(){
        return _currencySymbol;
    }
    public static void set_currencySymbol(String newSymbol){
        _currencySymbol = newSymbol;
    }
    public static String getCurrencyName(){
        return _currencyName;
    }
    public static Boolean setCurrencyName(String newName) {
        if (newName == "" || newName.isEmpty()) {
            return false;
        } else {
            _currencyName = newName;
        }
        return true;
    }

}
