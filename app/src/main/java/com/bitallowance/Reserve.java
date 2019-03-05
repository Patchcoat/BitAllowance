package com.bitallowance;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Reserve {
    private static List<Entity> _entityList = new ArrayList<>();
    private static List<Transaction> _transactionList;
    private static String _currencyName;
    private static String _currencySymbol;

    public Reserve() {
    }

    public static void addEntity(Entity newEntity){
        _entityList.add(newEntity);
    }
    public static void updateEntity(Entity newEntity, int index){
        _entityList.set(index, newEntity);
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


    /**
     * A class I made to format Date objects to MM/DD/YYYY strings
     * @author Doug Barlow
     * @version 1.0
     * @return formatted date string.
     */
    public static String dateToString(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String dateString = month + "/" + day + "/" + year;
        return dateString;
    }

}