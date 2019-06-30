package com.adamhala007.greatquotes.activities;

import android.content.res.Configuration;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.adamhala007.greatquotes.R;
import com.adamhala007.greatquotes.database.Quote;
import com.adamhala007.greatquotes.database.QuoteDatabase;

import java.util.ArrayList;
import java.util.List;

public class NewQuoteActivity extends AppCompatActivity {

    Spinner spinner;
    EditText etFirstName, etLastName, etQuote;
    Button bDone;
    Boolean canBeSaved;
    LinearLayout backNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_quote);

        setActionBar();

        spinner = findViewById(R.id.spGender);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etQuote = findViewById(R.id.etQuote);
        bDone = findViewById(R.id.bDone);
        backNavigation = findViewById(R.id.back);

        canBeSaved = false;

        setGenderSpinner();
        setListeners();

    }

    private void setActionBar(){

        Toolbar toolbar = findViewById(R.id.toolbarNewQuote);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(false);

    }

    private void setGenderSpinner(){
        List<String> gender = new ArrayList<String>();
        gender.add(getResources().getString(R.string.select_gender));
        gender.add(getResources().getString(R.string.male));
        gender.add(getResources().getString(R.string.female));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, gender);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    private void setListeners(){
        etFirstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (!b){
                    checkFieldFirstName();
                }
            }


        });

        etLastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (!b){
                    checkFieldLastName();
                }
            }
        });

        etQuote.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (!b){
                    checkFieldQuote();
                }
            }
        });

        bDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveQuoteAndLeave();
            }
        });

        backNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void checkFieldFirstName(){
        if (!isMinimumLengthCorrect(etFirstName.getText().toString(),5)){
            etFirstName.setError(getResources().getString(R.string.minimum_field_length) + " 5");
            canBeSaved = false;
        }else{
            etFirstName.setError(null);
            canBeSaved = true;
        }
    }

    private void checkFieldLastName(){
        if (!isMinimumLengthCorrect(etLastName.getText().toString(),5)){
            etLastName.setError(getResources().getString(R.string.minimum_field_length) + " 5");
            canBeSaved = false;
        }else{
            etLastName.setError(null);
            canBeSaved = true;
        }
    }

    private void checkFieldQuote(){
        if (!isMinimumLengthCorrect(etQuote.getText().toString(),10)){
            etQuote.setError(getResources().getString(R.string.minimum_field_length) + " 10");
            canBeSaved = false;

        }else{
            etQuote.setError(null);
            canBeSaved = true;
        }
    }

    private boolean isMinimumLengthCorrect(String text, int length){
        return text.length() >= length;
    }

    private void saveQuoteAndLeave(){
        if (canBeSaved){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Boolean gender = getGenderValue();

                    Quote quote = createQuote(
                            etFirstName.getText().toString(),
                            etLastName.getText().toString(),
                            etQuote.getText().toString(),
                            gender
                    );

                    QuoteDatabase quoteDatabase = QuoteDatabase.getDatabase(NewQuoteActivity.this);
                    quoteDatabase.daoAccess().insertQuote(quote);


                }
            }).start();
            onBackPressed();
        }else {
            checkEditTextFields();
            Toast.makeText(NewQuoteActivity.this, getText(R.string.alert_required_fields), Toast.LENGTH_SHORT).show();
        }
    }


    private Boolean getGenderValue(){
        String spinnerValue = spinner.getSelectedItem().toString();

        if (spinnerValue.equals(getApplicationContext().getString(R.string.male))){
            return false;
        }else if (spinnerValue.equals(getApplicationContext().getString(R.string.female))){
            return true;
        }else{
            return null;
        }

    }

    private Quote createQuote(String firstName, String lastName, String quoteText, Boolean gender){
        Quote quote = new Quote();

        quote.setFirstName(firstName);
        quote.setLastName(lastName);
        quote.setQuote(quoteText);
        quote.setGender(gender);

        return quote;
    }

    private void checkEditTextFields(){
        checkFieldFirstName();
        checkFieldLastName();
        checkFieldQuote();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_quote, menu);

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            menu.findItem(R.id.done).setVisible(true);
            bDone.setVisibility(Button.INVISIBLE);
        }else{
            menu.findItem(R.id.done).setVisible(false);
            bDone.setVisibility(Button.VISIBLE);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.done:
                saveQuoteAndLeave();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
