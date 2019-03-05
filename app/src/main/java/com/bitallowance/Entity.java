package com.bitallowance;

import android.icu.util.LocaleData;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class Entity {

    // Variables for the Class
    private int id;
    private String userName;
    private String displayName;
    private Date birthday;
    private String email;
  
    private Date timeSinceLastLoad;
    private List<Transaction> transactions;
    private BigDecimal cashBalance;

    // Constructors
    public Entity() {

    }

    // Getters
    public  BigDecimal getCashBalance() {
        return cashBalance;
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

    public List<Transaction> getTransactions () {
        return this.transactions;
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

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    // Transaction Methods
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

    }

    public void modifyBalance(BigDecimal value, boolean addToBalance) {

    }

    // Update Entity method
    public void updateEntity() {

    }
}
