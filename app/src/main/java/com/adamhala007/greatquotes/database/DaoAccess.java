package com.adamhala007.greatquotes.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface DaoAccess {

    @Insert
    void insertQuote(Quote quote);

    @Query("SELECT * FROM Quote WHERE quoteId = :quoteId")
    Quote getQuoteById(int quoteId);

    @Delete
    void deleteQuote (Quote quote);

    @Query("DELETE FROM quote WHERE quoteId = :quoteId")
    void deleteByQuoteId(long quoteId);

    @Query("SELECT * FROM Quote ")
    List<Quote> getAllQuotes();
}
