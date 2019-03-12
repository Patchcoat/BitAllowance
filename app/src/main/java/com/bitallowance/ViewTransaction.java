package com.bitallowance;

import android.view.View;

import java.util.List;

/**
 * ViewTransaction Class
 * @author Dustin Christensen
 * @version 1.0
 * This class is used to view the details of Transactions. May the transaction
 * be a Reward, Task, Fine, Assigned, Inactive, etc. The distinctions
 * between the list will be determined by the filter assignment.
 * @since 03/11/2019
 */

public class ViewTransaction {

    // Private variables
    private Transaction transactionList;
    private List<Entity> entityList;
    private Filter filter;

    // Private methods
    private void setGoal() {

    }

    private void request() {

    }

    private void refundEntity() {

    }

    private void refundAll() {

    }

    private void giveReward() {

    }

    private void followTransaction() {

    }

    private void markTransactionComplete() {

    }

    private void assignTransaction() {

    }

    private void assignTransactionToAll() {

    }

    // Public methods
    public void openEditTransaction(View view) {

    }

    // Getters and setters
    public Transaction getTransactionList() {
        return this.transactionList;
    }
    public void setTransactionList(Transaction _transactionList) {
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
}
