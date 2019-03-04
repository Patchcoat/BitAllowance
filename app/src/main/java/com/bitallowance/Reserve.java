package com.bitallowance;

import java.util.List;

public class Reserve {
    static List<Entity> _entityList;
    static List<Transaction> _transactionList;
    static String _currency;

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
}
