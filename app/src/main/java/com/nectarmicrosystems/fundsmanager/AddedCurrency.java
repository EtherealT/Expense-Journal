package com.nectarmicrosystems.fundsmanager;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by oluwatobi on 11/17/2015.
 */
public class AddedCurrency {
    private static final String DATABASE_NAME = "prudent me";
    private static final String DATABASE_TABLE = "added_currencies";
    private static final int DATABASE_VERSION = 1;

    public static final String KEY_ROW_ID = "_id";
    public static final String CURRENCY = "currency";
    public static final String LOGO = "logo";

    private DbHelper helper;
    private final Context context;
    private SQLiteDatabase database;

    public AddedCurrency(Context c){
        this.context = c;
    }

    private static class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context c){
            super(c, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS" + DATABASE_TABLE + " (" +
                KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                CURRENCY + " TEXT NOT NULL, " +
                LOGO + " TEXT NOT NULL)"
            );

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

    public AddedCurrency open() throws SQLException {
        helper = new DbHelper(context);
        database = helper.getWritableDatabase();
        return this;
    }

    public void close(){
        helper.close();
    }

    public long createEntry(String currency, String logo){
        ContentValues cv = new ContentValues();
        cv.put(CURRENCY, currency);
        cv.put(LOGO, logo);
        return database.insert(DATABASE_TABLE, null, cv);
    }

    public void complete(ListPreference l){
        //check if empty
        boolean empty = false;
        Cursor cur = database.rawQuery("SELECT COUNT(*) FROM " + DATABASE_TABLE, null);
        if (cur != null && cur.moveToFirst()) {
            empty = (cur.getInt (0) == 0);
        }
        cur.close();

        if(empty)
            return;

        String[] columns = new String[]{KEY_ROW_ID, CURRENCY, LOGO};
        Cursor c = database.query(DATABASE_TABLE, columns, null, null, null, null, null);

        String returnedCurrency;
        String returnedLogo;

        int iCurr = c.getColumnIndex(CURRENCY);
        int iLogo = c.getColumnIndex(LOGO);

        String currency = l.getValue();

        CharSequence[] entries = l.getEntries();
        CharSequence[] entryValues = l.getEntryValues();

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
            returnedCurrency = c.getString(iCurr);
            returnedLogo = c.getString(iLogo);

            CharSequence[] newEntries = new CharSequence[entries.length + 1];
            CharSequence[] newEntryValues = new CharSequence[entryValues.length + 1];

            for (int i = 0; i < entries.length; i++){
                newEntries[i] = entries[i];
                newEntryValues[i] = entryValues[i];
            }

            newEntries[newEntries.length - 1] = returnedCurrency;
            newEntryValues[newEntryValues.length - 1] = returnedLogo;

            l.setEntries(newEntries);
            l.setEntryValues(newEntryValues);
            l.setValue(currency);
        }
    }
}
