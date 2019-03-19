package com.bitallowance;

import java.math.BigDecimal;
import java.util.Date;

public class CreditCard {

    // Private variables
    private float monthlyInterestRate;
    private Date paymentDueDate;
    private BigDecimal cashBalance;
    private BigDecimal creditLimit;
    private BigDecimal todayInterest;
    private BigDecimal weekInterest;
    private BigDecimal monthInterest;
    private BigDecimal paymentAmount;

    // Private methods
    private void makePayment() {

    }

    private void borrowFromCreditCard() {

    }

    // Getters and Setters
    public float getMonthlyInterestRate() {
        return this.monthlyInterestRate;
    }
    public void setMonthlyInterestRate(float _monthlyInterestRate) {
        this.monthlyInterestRate = _monthlyInterestRate;
    }

    public Date getPaymentDueDate() {
        return this.paymentDueDate;
    }
    public void setPaymentDueDate(Date _paymentDueDate) {
        this.paymentDueDate = _paymentDueDate;
    }

    public BigDecimal getCashBalance() {
        return  this.cashBalance;
    }
    public void setCashBalance(BigDecimal _cashBalance) {
        this.cashBalance = _cashBalance;
    }

    public BigDecimal getCreditLimit() {
        return this.creditLimit;
    }
    public void setCreditLimit(BigDecimal _creditLimit) {
        this.creditLimit = _creditLimit;
    }

    public BigDecimal getTodayInterest() {
        return this.todayInterest;
    }
    public void setTodayInterest(BigDecimal _todayInterest) {
        this.todayInterest = _todayInterest;
    }

    public BigDecimal getWeekInterest() {
        return this.weekInterest;
    }
    public void setWeekInterest(BigDecimal _weekInterest) {
        this.weekInterest = _weekInterest;
    }

    public BigDecimal getMonthInterest() {
        return  this.monthInterest;
    }
    public void setMonthInterest(BigDecimal _monthInterest) {
        this.monthInterest = _monthInterest;
    }

    public BigDecimal getPaymentAmount() {
        return this.paymentAmount;
    }
    public void setPaymentAmount(BigDecimal _paymentAmount) {
        this.paymentAmount = _paymentAmount;
    }
}
