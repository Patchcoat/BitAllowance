package com.bitallowance;

import android.util.Log;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Entity Class
 * @author Dustin Christensen
 * @version 1.0
 * Entity class, the date that will be attached to the users,
 * either as the teach/parent or as the student/child
 * @since 02/25/2019
 */

public class Entity implements ListItem {

    // Variables for the Class
    private static final String TAG = "BADDS-Entity";
    private int id;
    private String userName;
    private String displayName;
    private Date birthday;
    private String email;
  
    private Date timeSinceLastLoad;
    private List<Transaction> _transactionHistory;
    private BigDecimal cashBalance;

    // Constructors
    public Entity() {
        cashBalance = new BigDecimal(0);
        _transactionHistory = new ArrayList<>();
    }

    // Getters
    public  BigDecimal getCashBalance() {
        return cashBalance;
    }
    public void updateBalance(BigDecimal amount, boolean add){
        if (add){
            cashBalance = cashBalance.add(amount);
        } else {
            cashBalance = cashBalance.subtract(amount);
        }
    }
    public void setCashBalance(double balance){
        cashBalance = new BigDecimal(balance);
    }

    public int getId() {
        return  this.id;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Date getBirthday () {
        return this.birthday;
    }

    public String getEmail () {
        return this.email;
    }

    public Date getTimeSinceLastLoad () {
        return  this.timeSinceLastLoad;
    }

    public List<Transaction> getTransactionHistory() {
        return this._transactionHistory;
    }

    public void addToHistory(Transaction transaction) {
        _transactionHistory.add(transaction);
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTimeSinceLastLoad(Date timeSinceLastLoad) {
        this.timeSinceLastLoad = timeSinceLastLoad;
    }

    public void set_transactionHistory(List<Transaction> _transactionHistory) {
        this._transactionHistory = _transactionHistory;
    }

    // Transaction Methods

    /**
     * Gets a list of all transactions assigned to the entity
     * @return list of assigned transactions
     */
    public List<Transaction> getAssignedTransactions(){
        List<Transaction> transactions = new ArrayList<>();
        for (Transaction transaction : Reserve.get_transactionList()) {
            if (transaction.isAssigned(this)) {
                transactions.add(transaction);
            }
        }
        return transactions;
    }
    /**
     * Gets a list of all transactions of a specifc type assigned to the entity
     * @param type the type of transaction to return
     * @return list of assigned transactions
     */
    public List<Transaction> getAssignedTransactions(ListItemType type){
        List<Transaction> transactions = new ArrayList<>();
        for (Transaction transaction : Reserve.get_transactionList()) {
            if (transaction.isAssigned(this) && transaction.getTransactionType() == type) {
                transactions.add(transaction);
            }
        }
        return transactions;
    }
    public void loadTransactions() {

    }
    public void updateTransacions() {

    }

    public void executeTransaction() {

    }

    // Task methods
    public void loadTasks(Date time) {

    }

    public void updateTask() {

    }

    public void confirmTask(Transaction task, boolean complete) {


    }

    // Balance methods
    public void updateBalance(BigDecimal value) {
        cashBalance = value;
    }

    public void modifyBalance(BigDecimal value, boolean addToBalance) {

    }

    // Update Entity method
    public void updateEntity() {

    }

    @Override
    public String getName() {
        return displayName;
    }
    @Override
    public String getCardPrimaryDetails()
    {
        String details = Reserve.get_currencySymbol();
        details += " " + cashBalance.setScale(2);
        return details;
    }
    @Override
    public String getCardSecondaryDetails() {
        return Reserve.dateToString(birthday);
    }
    @Override
    public float getSortableValue(){
        return cashBalance.floatValue();
    }
    @Override
    public Date getSortableDate(){
        return birthday;
    }

    @Override
    public ListItemType getType() {
        return ListItemType.ENTITY;
    }

    /**
     * Uses the applyTransaction method of the passed listItem to apply the transaction
     * @param item - ListItem of type REWARD, FINE or TASK
     * @return
     * @throws IllegalArgumentException ListItem CANNOT be of type ENTITY
     */
    @Override
    public boolean applyTransaction(ListItem item) {
        if (item.getType() == ListItemType.ENTITY)
            Log.e(TAG, "applyTransaction: ListItem item type is not REWARD, FINE or TASK", new IllegalArgumentException());
        //We can use the transaction logic for this.
        return item.applyTransaction(this);
    }

    @Override
    public List<ListItem> getAssignmentList() {
        List<ListItem> assignmentList = new ArrayList<>();
        assignmentList.addAll(getAssignedTransactions());
        return assignmentList;
    }

    @Override
    public void update() {
        UpdateListItem update = new UpdateListItem();
        update.itemToUpdate(this);
        update.execute(String.valueOf(id));
    }

    @Override
    public void delete() {
        for (Transaction transaction: Reserve.get_transactionList()){
            transaction.deleteEntity(this);
        }
        Reserve.get_entityList().remove(this);
    }

}


