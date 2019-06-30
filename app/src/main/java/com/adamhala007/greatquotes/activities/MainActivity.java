package com.adamhala007.greatquotes.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.adamhala007.greatquotes.R;
import com.adamhala007.greatquotes.database.QuoteDatabase;
import com.adamhala007.greatquotes.database.Quote;
import com.adamhala007.greatquotes.xml.QuoteXmlParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private SimpleAdapter sa;
    private ActionBar actionbar;

    private QuoteDatabase quoteDatabase;
    private List<Quote> quotes;
    private int currentQuote;

    private TextView tvQuote, tvName, tvToolbarTitle;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setActionbar();

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.nav_view);
        tvQuote = findViewById(R.id.tvDisplayQuote);
        tvName = findViewById(R.id.tvDisplayName);
        tvToolbarTitle = findViewById(R.id.tvToolbarTitle);

        loadQuotes();

    }


    private void setActionbar(){
        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
    }

    private void loadQuotes(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                quoteDatabase = QuoteDatabase.getDatabase(MainActivity.this);
                quotes = quoteDatabase.daoAccess().getAllQuotes();

                if (quotes.isEmpty()){
                    loadQuotesFromXml();
                }

                chooseRandomQuote();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displayQuote();
                        createNavigationDrawer();
                    }
                });

            }
        }).start();
    }

    private void loadQuotesFromXml(){
        try {
            QuoteXmlParser quoteXmlParser = new QuoteXmlParser(getAssets().open("quotes.xml"));
            quotes = quoteXmlParser.parseQuotes();

            for (Quote quote: quotes) {
                quoteDatabase.daoAccess().insertQuote(quote);
            }

            quotes = quoteDatabase.daoAccess().getAllQuotes();
        } catch (XmlPullParserException|IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void chooseRandomQuote(){
        Random random = new Random();
        currentQuote = random.nextInt(quotes.size());
    }

    private void displayQuote(){
        Quote quote = quotes.get(currentQuote);
        tvQuote.setText(quote.getQuote());
        tvName.setText(quote.getFirstName() + " " + quote.getLastName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                createNavigationDrawer();
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

            case R.id.add:

                Intent intent = new Intent(this, NewQuoteActivity.class);
                startActivity(intent);
                return true;

            case R.id.remove:

                createDeleteDialog();

                return true;


            case R.id.plus:

                mDrawerLayout.closeDrawers();
                Intent intent2 = new Intent(this, NewQuoteActivity.class);
                startActivity(intent2);
                return true;

            case R.id.closeDrawer:

                mDrawerLayout.closeDrawers();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void createDeleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.delete);
        builder.setMessage(R.string.alert_delete);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteAndReloadCurrentQuote();
                loadQuotes();
            }
        });
        builder.setNegativeButton(R.string.no, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void createNavigationDrawer(){
        ArrayList<HashMap<String, String>> list = fillNavigationDrawerList();

        sa = new SimpleAdapter(this, list, R.layout.drawer_list_item, new String[] {"line1", "line2"}, new int[] {R.id.text1, R.id.text2});
        mDrawerList.setAdapter(sa);
        setListeners();

    }

    private ArrayList<HashMap<String, String>> fillNavigationDrawerList(){

        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        HashMap<String, String> item;

        for (Quote quote: quotes) {
            item = new HashMap<>();
            item.put("line1", quote.getQuote());
            item.put("line2", quote.getFirstName() + " " + quote.getLastName());
            list.add(item);
        }

        return list;
    }


    private void setListeners(){

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mDrawerLayout.closeDrawers();
                currentQuote = i;
                displayQuote();

            }
        });

        mDrawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Respond when the drawer is opened
                        changeMenuItemsOnDrawerOpened();
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        // Respond when the drawer is closed
                        changeMenuItemsOnDrawerClosed();
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                    }
                }
        );
    }

    private void changeMenuItemsOnDrawerOpened(){
        actionbar.setDisplayHomeAsUpEnabled(false);
        menu.findItem(R.id.remove).setVisible(false);
        menu.findItem(R.id.add).setVisible(false);
        menu.findItem(R.id.plus).setVisible(true);
        menu.findItem(R.id.closeDrawer).setVisible(true);
        tvToolbarTitle.setText(getText(R.string.quotes));
    }

    private void changeMenuItemsOnDrawerClosed(){
        actionbar.setDisplayHomeAsUpEnabled(true);
        menu.findItem(R.id.remove).setVisible(true);
        menu.findItem(R.id.add).setVisible(true);
        menu.findItem(R.id.plus).setVisible(false);
        menu.findItem(R.id.closeDrawer).setVisible(false);
        tvToolbarTitle.setText("");
    }

    private void deleteAndReloadCurrentQuote(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                quoteDatabase.daoAccess().deleteByQuoteId(quotes.get(currentQuote).getQuoteId());
                loadQuotes();
            }
        }).start();

    }

    @Override
    protected void onResume() {
        if (quotes!=null){
            loadQuotes();
        }

        super.onResume();
    }
}