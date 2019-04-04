package com.bitallowance;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Reserve {
    private static int _id;
    private static List<Entity> _entityList = new ArrayList<>();
    private static List<Transaction> _transactionList = new ArrayList<>();
    private static String _currencyName;
    private static String _currencySymbol;

    public Reserve() {
    }

    public static int get_id(){
        return _id;
    }
    public static void set_id(int id){
        _id = id;
    }

    public static void addEntity(Entity newEntity) {
        _entityList.add(newEntity);
    }
    public static void updateEntity(Entity newEntity, int index){
        _entityList.set(index, newEntity);
    }
    public static void setEntityList(List<Entity> entityList) {
        _entityList = entityList;
    }

    public static void addTransaction(Transaction newTransaction){
        _transactionList.add(newTransaction);
    }
    public static void updateTransaction(Transaction newTransaction, int index) {
        _transactionList.set(index, newTransaction);
    }
    public static void setTransactionList(List<Transaction> transactionList) {
        _transactionList = transactionList;
    }

    public static List<Entity> get_entityList() {
        return _entityList;
    }

    public static List<Transaction> get_transactionList() {
        return _transactionList;
    }

    /**
     * Gets the saved currency symbol or "$" if the current currency symbol is null
     * @return a string containing the Reserve currency symbol.
     */
    public static String get_currencySymbol(){
        if (_currencySymbol != null)
            return _currencySymbol;
        else
            return "$";
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
     * This function returns a complete list of all existing items of the specified type.
     * @param type Specifies type of List Item to be returned.
     * @return List of the specified ListItem type.
     */
    public static List<ListItem> getListItems (ListItemType type){
        List<ListItem> returnList = new ArrayList<>();
        //If type == ALL we want all transactions
        if (type == ListItemType.ALL){
            returnList.addAll(_transactionList);
            //If type == Entity return all entities
        } else if (type == ListItemType.ENTITY) {
            returnList.addAll(_entityList);
            //Else create a list with only items of the specified type.
        } else {
            for (Transaction transaction: _transactionList) {
                if(transaction.getTransactionType() == type){
                    returnList.add(transaction);
                }
            }
        }
        return returnList;
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
