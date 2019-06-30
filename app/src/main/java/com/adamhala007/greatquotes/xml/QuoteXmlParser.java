package com.adamhala007.greatquotes.xml;

import com.adamhala007.greatquotes.database.Quote;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class QuoteXmlParser {

    XmlPullParser xpp;

    public static final String MALE = "Male";
    public static final String FEMALE = "Female";

    public static final Boolean MALE_VALUE = false;
    public static final Boolean FEMALE_VALUE = true;


    public QuoteXmlParser(InputStream is) throws XmlPullParserException {

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        xpp = factory.newPullParser();

        xpp.setInput(is, null);
    }

    public ArrayList<Quote> parseQuotes() throws XmlPullParserException, IOException {

        ArrayList<Quote> quotes = new ArrayList<>();
        int eventType = xpp.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.START_DOCUMENT) {

            } else if(eventType == XmlPullParser.START_TAG) {
                switch (xpp.getName()){

                    case "quote":

                        Quote quote = new Quote();
                        setQuoteAttributes(quote);

                        eventType = xpp.next();

                        if(eventType == XmlPullParser.TEXT)
                            quote.setQuote(xpp.getText());

                        quotes.add(quote);
                        break;

                    case "quotes":
                        xpp.next();

                        break;

                }

            }
            eventType = xpp.next();
        }

        return quotes;
    }

    private void setQuoteAttributes(Quote quote){

        for (int i = 0; i < xpp.getAttributeCount(); i++) {
            switch (xpp.getAttributeName(i)){
                case "gender":

                    if (xpp.getAttributeValue(i).equals(MALE)){
                        quote.setGender(MALE_VALUE);
                    }else if (xpp.getAttributeValue(i).equals(FEMALE)){
                        quote.setGender(FEMALE_VALUE);
                    }

                    break;

                case "firstName":
                    quote.setFirstName(xpp.getAttributeValue(i));
                    break;

                case "lastName":
                    quote.setLastName(xpp.getAttributeValue(i));
                    break;
            }
        }
    }
}
