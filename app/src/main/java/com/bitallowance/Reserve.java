package com.bitallowance;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class Reserve {
    private static String TAG = "BADDS - Reserve";
    private static int _id;
    private static List<Entity> _entityList = new ArrayList<>();
    private static List<Transaction> _transactionList = new ArrayList<>();
    private static String _currencyName;
    private static String _currencySymbol;
    static boolean serverIsPHP = false;

    public Reserve() {
    }

    public static int get_id() {
        return _id;
    }

    public static void set_id(int id) {
        _id = id;
    }

    public static void addEntity(Entity newEntity) {
        _entityList.add(newEntity);
    }

    public static void updateEntity(Entity newEntity, int index) {
        _entityList.set(index, newEntity);
    }

    public static void setEntityList(List<Entity> entityList) {
        _entityList = entityList;
    }

    public static void addTransaction(Transaction newTransaction) {
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
     *
     * @return a string containing the Reserve currency symbol.
     */
    public static String get_currencySymbol() {
        if (_currencySymbol != null)
            return _currencySymbol;
        else
            return "$";
    }

    public static void set_currencySymbol(String newSymbol) {
        _currencySymbol = newSymbol;
    }

    public static String getCurrencyName() {
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
     *
     * @param type Specifies type of List Item to be returned.
     * @return List of the specified ListItem type.
     */
    public static List<ListItem> getListItems(ListItemType type) {
        List<ListItem> returnList = new ArrayList<>();
        //If type == ALL we want all transactions
        if (type == ListItemType.ALL) {
            returnList.addAll(_transactionList);
            //If type == Entity return all entities
        } else if (type == ListItemType.ENTITY) {
            returnList.addAll(_entityList);
            //Else create a list with only items of the specified type.
        } else {
            for (Transaction transaction : _transactionList) {
                if (transaction.getTransactionType() == type) {
                    returnList.add(transaction);
                }
            }
        }
        return returnList;
    }


    /**
     * A class I made to format Date objects to a detailed string
     *
     * @return formatted date string.
     * @author Doug Barlow
     * @version 1.0
     */
    public static String dateToString(Date date) {

        if (date == null){
            return " - ";
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String dateString;
        switch (month) {
            case 0:
                dateString = "January ";
                break;
            case 1:
                dateString = "February ";
                break;
            case 2:
                dateString = "March ";
                break;
            case 3:
                dateString = "April ";
                break;
            case 4:
                dateString = "May ";
                break;
            case 5:
                dateString = "June ";
                break;
            case 6:
                dateString = "July ";
                break;
            case 7:
                dateString = "August ";
                break;
            case 8:
                dateString = "September ";
                break;
            case 9:
                dateString = "October ";
                break;
            case 10:
                dateString = "November ";
                break;
            default:
                dateString = "December ";
                break;
        }
        dateString += day + ", " + year;
        return dateString;
    }

    /**
     * A class I made to format Date objects to MM/DD/YYYY strings
     *
     * @return formatted date string.
     * @author Doug Barlow
     * @version 1.0
     */
    public static String dateToDigitString(Date date) {
        if (date == null){
            return " - ";
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String dateString = month + "/" + day + "/" + year;
        return dateString;
    }

    /**
     * Takes a date object and returns a SQL formatted datestring;
     *
     * @return formatted date string.
     */
    public static String dateStringSQL(Date date) {
        if (date == null) {
            return "0000-00-00";
        }
        Log.d(TAG, "dateStringSQL: DateString = " + date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String dateString = year + "-";
        if (month < 10)
            dateString += "0";
        dateString += month + "-";
        if (day < 10)
            dateString += "0";
        dateString += day;
        return dateString;
    }

    /**
     * Takes a string value formatted YYYY-MM-DD and returns a matching Date object
     *
     * @return formatted date string.
     */
    public static Date stringToDate(String date) {
        Log.d(TAG, "stringToDate: DateString = " + date);

        Calendar calendar = new GregorianCalendar();

        int year;
        int month;
        int day;

        try {
            year = Integer.parseInt(date.substring(0, 4));
            month = Integer.parseInt(date.substring(5, 7));
            day = Integer.parseInt(date.substring(8));

            if (year < 1900){
                return null;
            }

            Log.e(TAG, "stringToDate: Year " + year + " Month " + month + " Day " + day);
        } catch (Exception e) {
            Log.e(TAG, "stringToDate: Error: Invalid datestring format");
            return null;
        }

        calendar.set(year, (month - 1) , day);
        return new Date(calendar.getTimeInMillis());
    }

}
