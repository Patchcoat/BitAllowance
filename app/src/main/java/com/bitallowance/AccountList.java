package com.bitallowance;

import android.view.View;

import java.math.BigDecimal;
import java.util.Date;

/**
 * AccountList Class
 * @author Dustin Christensen
 * @version 1.0
 * This class will manage the account balance of a given entity
 * @since 03/12/2019
 */

public class AccountList {

    // Private Variables
    private BigDecimal cashBalance;
    private BigDecimal savingBalance;
    private BigDecimal investimentBalance;
    private BigDecimal totalAssets;
    private BigDecimal loanBalance;
    private BigDecimal creditCardBalance;
    private BigDecimal netWorth;
    private Date paymentDueBy;

    // Private Method
    private void requestLoan (View view){

    }

    // Public Methods
    public void openSavings(View view) {

    }

    public void openInvestiment(View view) {

    }

    public void openCreditCard(View view) {

    }

    public void openLoan(View view) {

    }

    // Getters and Setters
    public BigDecimal getCashBalance() {
        return this.cashBalance;
    }
    public void setCashBalance(BigDecimal _cashBalance) {
        this.cashBalance = _cashBalance;
    }

    public BigDecimal getSavingBalance() {
        return this.savingBalance;
    }
    public void setSavingBalance(BigDecimal _savingBalance) {
        this.savingBalance = _savingBalance;
    }

    public BigDecimal getInvestimentBalance() {
        return this.investimentBalance;
    }
    public void setInvestimentBalance(BigDecimal _investimentBalance) {
        this.investimentBalance = _investimentBalance;
    }

    public BigDecimal getTotalAssets() {
        return this.totalAssets;
    }
    public void setTotalAssets(BigDecimal _totalAssests) {
        this.totalAssets = _totalAssests;
    }

    public BigDecimal getLoanBalance() {
        return this.loanBalance;
    }
    public void setLoanBalance(BigDecimal _loanBalance) {
        this.loanBalance = _loanBalance;
    }

    public BigDecimal getCreditCardBalance() {
        return creditCardBalance;
    }
    public void setCreditCardBalance(BigDecimal _creditCardBalance) {
        this.creditCardBalance = _creditCardBalance;
    }

    public BigDecimal getNetWorth() {
        return this.netWorth;
    }
    public void setNetWorth(BigDecimal _netWorth) {
        this.netWorth = _netWorth;
    }

    public Date getPaymentDueBy() {
        return this.paymentDueBy;
    }
    public void setPaymentDueBy(Date _paymentDueBy) {
        this.paymentDueBy = _paymentDueBy;
    }
}