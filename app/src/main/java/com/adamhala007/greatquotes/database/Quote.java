package com.adamhala007.greatquotes.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Quote {
    @NonNull
    public Integer getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(@NonNull Integer quoteId) {
        this.quoteId = quoteId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private Integer quoteId;
    private String firstName;
    private String lastName;
    private String quote;
    private Boolean gender;

    public Quote(){

    }

    @Override
    public String toString() {
        return quote + " (" + firstName + " " + lastName + ")";
    }
}
