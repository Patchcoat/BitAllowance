package com.bitallowance;

import android.view.View;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * TransactionList Class
 * @author Dustin Christensen
 * @version 1.0
 * This class is used to create a list of Transactions. May the transaction
 * be a Reward, Task, Fine, Assigned, Inactive, etc. The distinctions
 * between the list will be determined by the filter assignment.
 * @since 03/11/2019
 */

public class TransactionList {

    // Variables
    private List<Transaction> transactionList;
    private List<Entity> entityList;
    private Filter filter;
    private Date timeFilter;
    private BigDecimal costFilter;
    private BigDecimal valueFilter;
    private BigDecimal fineFilter;

    // Private methods
    private void setGoal() {

    }

    private void request() {

    }

    private void filterTransactionList() {

    }

    private void followTransaction() {

    }

    // Public methods
    public void openViewTransaction() {

    }

    public void openAddTransaction(View view) {

    }

    public void openEditTransaction(View view) {

    }

    // Getters and Setters
    public List<Transaction> getTransactionList () {
        return this.transactionList;
    }
    public void setTransactionList(List<Transaction> _transactionList) {
        this.transactionList = _transactionList;
    }

    public List<Entity> getEntityList() {
        return this.entityList;
    }
    public void setEntityList(List<Entity> _entityList) {
        this.entityList = _entityList;
    }

    public Filter getFilter() {
        return this.filter;
    }
    public void setFilter(Filter _filter) {
        this.filter = _filter;
    }

    public Date getTimeFilter() {
        return this.timeFilter;
    }
    void setTimeFilter(Date _timeFilter) {
        this.timeFilter = _timeFilter;
    }

    public BigDecimal getCostFilter() {
        return this.costFilter;
    }
    public void setCostFilter(BigDecimal _costFilter) {
        this.costFilter = _costFilter;
    }

    public BigDecimal getValueFilter() {
        return this.valueFilter;
    }
    public void setValueFilter(BigDecimal _valueFilter) {
        this.valueFilter = _valueFilter;
    }

    public BigDecimal getFineFilter() {
        return this.fineFilter;
    }
    public void setFineFilter(BigDecimal _fineFilter) {
        this.fineFilter = _fineFilter;
    }
}
