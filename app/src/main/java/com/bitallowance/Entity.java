package com.bitallowance;

import android.icu.util.LocaleData;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Entity {

    // Variables for the Class
    private int id;
    private String userName;
    private String displayName;
    private LocalDate birthday;
    private String email;
    private LocalDateTime timeSinceLastLoad;
    private List<Transaction> transations;

    // Constructors
    public Entity() {

    }

    // Getters
    public int getId() {
        return  this.id;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public LocalDate getBirthday () {
        return this.birthday;
    }

    public String getEmail () {
        return this.email;
    }

    public LocalDateTime getTimeSinceLastLoad () {
        return  this.timeSinceLastLoad;
    }

    public List<Transaction> getTransations () {
        return this.transations;
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

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTimeSinceLastLoad(LocalDateTime timeSinceLastLoad) {
        this.timeSinceLastLoad = timeSinceLastLoad;
    }

    public void setTransations(List<Transaction> transations) {
        this.transations = transations;
    }

    // Transaction Methods
    public void loadTransactions() {

    }

    public void updateTransations() {

    }

    public void executeTransaction() {

    }

    // Task methods
    public void loadTasks(LocalDateTime time) {

    }

    public void updateTask() {

    }

    public void confirmTask(Task task, boolean complete) {

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
