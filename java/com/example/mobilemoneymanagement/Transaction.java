package com.example.mobilemoneymanagement;

import com.google.firebase.firestore.Exclude;

public class Transaction {
    private String Reference, Name, Date;
    private double Amount;

    public Transaction(){
    }

    public Transaction (String transactionName, String transactionDate, String transactionReference, double transactionAmount) {
        this.Name = transactionName;
        this.Date = transactionDate;
        this.Reference = transactionReference;
        this.Amount = transactionAmount;;
    }

    @Exclude
    public String getReference() {
        return Reference;
    }

    public String getName(){
        return Name;
    }

    public String getDate() {
        return Date;
    }

    public double getAmount() {
        return Amount;
    }
}
