package com.nectarmicrosystems.fundsmanager;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;

/**
 * Created by oluwatobi on 11/13/2015.
 */
public class ExpenseItem{
    private static final String DATABASE_NAME = "prudent me";
    private static final String DATABASE_TABLE = "expenses";
    private static final String DATABASE_TABLE2 = "income";
    private static final String DATABASE_TABLE3 = "added_currencies";
    private static final int DATABASE_VERSION = 1;

    public static final String KEY_ROW_ID = "_id";
    public static final String TITLE = "title";
    public static final String TYPE = "type";
    public static final String LIST_GROUP = "list_group";
    public static final String AMOUNT = "amount";
    public static final String DESCRIPTION = "description";
    public static final String ON_DAY = "on_day";

    public static final String CURRENCY = "currency";
    public static final String LOGO = "logo";

    private DbHelper helper;
    private final Context context;
    private SQLiteDatabase database;

    public ExpenseItem(Context c){
        this.context = c;
    }

    private static class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context c){
            super(c, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" +
                            KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            TITLE + " TEXT NOT NULL, " +
                            TYPE + " TEXT NOT NULL, " +
                            LIST_GROUP + " TEXT, " +
                            AMOUNT + " TEXT NOT NULL, " +
                            DESCRIPTION + " TEXT, " +
                            ON_DAY + " TEXT NOT NULL)"
            );

            db.execSQL("CREATE TABLE " + DATABASE_TABLE2 + " (" +
                            KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            TITLE + " TEXT NOT NULL, " +
                            AMOUNT + " TEXT NOT NULL, " +
                            DESCRIPTION + " TEXT, " +
                            ON_DAY + " TEXT NOT NULL)"
            );

            db.execSQL("CREATE TABLE " + DATABASE_TABLE3 + " (" +
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

    public ExpenseItem open() throws SQLException{
        helper = new DbHelper(context);
        database = helper.getWritableDatabase();
        return this;
    }

    public void close(){
        helper.close();
    }

    public void deleteEntry(long id){
        database.delete(DATABASE_TABLE, KEY_ROW_ID + " = " + id, null);
    }

    public void deleteListWithContents(long id){
        database.delete(DATABASE_TABLE, KEY_ROW_ID + " = " + id, null);
        database.delete(DATABASE_TABLE, LIST_GROUP + " = " + id, null);
    }

    public long createEntry(String title, String amount, String description, String type, String listGroup, String date){
        ContentValues cv = new ContentValues();
        cv.put(TITLE, title);
        cv.put(TYPE, type);
        cv.put(LIST_GROUP, listGroup);
        cv.put(AMOUNT, amount);
        cv.put(DESCRIPTION, description);
        cv.put(ON_DAY, date);
        return database.insert(DATABASE_TABLE, null, cv);
    }

    public void updateEntry(long id, String title, String amount, String description, String date){
        ContentValues cv = new ContentValues();
        cv.put(TITLE, title);
        cv.put(AMOUNT, amount);
        cv.put(DESCRIPTION, description);
        cv.put(ON_DAY, date);
        database.update(DATABASE_TABLE, cv, KEY_ROW_ID + " = " + id, null);
    }

    public void updateListTotal(long id, String amount){
        ContentValues cv = new ContentValues();
        cv.put(AMOUNT, amount);
        database.update(DATABASE_TABLE, cv, KEY_ROW_ID + "=" + id, null);
    }

    public void displayListItems(long key, LinearLayout l){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final String currency = prefs.getString("defaultCurrencies", "$");

        String[] columns = new String[]{KEY_ROW_ID, TITLE, AMOUNT, DESCRIPTION, TYPE, LIST_GROUP, ON_DAY};
        final Cursor c = database.query(DATABASE_TABLE, columns, null, null, null, null, null);

        int iTitle = c.getColumnIndex(TITLE);
        int iAmount = c.getColumnIndex(AMOUNT);
        int iDate = c.getColumnIndex(ON_DAY);
        int iGroup = c.getColumnIndex(LIST_GROUP);
        final int iType = c.getColumnIndex(TYPE);

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            if ((c.getString(iType).matches(NewSingleItemActivity.LIST_ITEM)) && (c.getString(iGroup).matches(String.valueOf(key)))) {
                final String returnedName = c.getString(iTitle);
                final String returnedAmount = c.getString(iAmount);
                final String returnedDate = c.getString(iDate);

                LinearLayout line = new LinearLayout(context);
                line.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                line.setOrientation(LinearLayout.HORIZONTAL);
                line.setBackgroundResource(R.drawable.back);
                line.setWeightSum(6);
                line.setPadding(20, 40, 20, 40);


                TextView nameView = new TextView(context);
                nameView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 4));
                nameView.setText(returnedName);
                nameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                nameView.setGravity(Gravity.START);

                TextView amountView = new TextView(context);
                amountView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 2));
                amountView.setText(currency + returnedAmount);
                amountView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                amountView.setGravity(Gravity.END);

                line.addView(nameView);
                line.addView(amountView);

                l.addView(line);
            }
        }
    }

    public void displayExpenses(LinearLayout l){
        //check if empty
        boolean empty = false;
        Cursor cur = database.rawQuery("SELECT COUNT(*) FROM " + DATABASE_TABLE, null);
        if (cur != null && cur.moveToFirst()) {
            empty = (cur.getInt (0) == 0);
        }
        cur.close();

        if(empty){
            Toast.makeText(context, "No Expenses to display", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final String currency = prefs.getString("defaultCurrencies", "$");

        String[] columns = new String[]{KEY_ROW_ID, TITLE, AMOUNT, DESCRIPTION, TYPE, LIST_GROUP, ON_DAY};
        final Cursor c = database.query(DATABASE_TABLE, columns, null, null, null, null, null);

        int iTitle = c.getColumnIndex(TITLE);
        int iAmount = c.getColumnIndex(AMOUNT);
        int iDate = c.getColumnIndex(ON_DAY);
        int iDescription = c.getColumnIndex(DESCRIPTION);
        int iRow = c.getColumnIndex(KEY_ROW_ID);
        final int iType = c.getColumnIndex(TYPE);

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
            if(!(c.getString(iType).matches(NewSingleItemActivity.LIST_ITEM))){
                final String returnedName = c.getString(iTitle);
                final String returnedAmount = c.getString(iAmount);
                final String returnedDate = c.getString(iDate);
                final String returnedDescription = c.getString(iDescription);
                final String type = c.getString(iType);
                final long id = c.getLong(iRow);

                LinearLayout line = new LinearLayout(context);
                line.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                line.setOrientation(LinearLayout.HORIZONTAL);
                line.setBackgroundResource(R.drawable.back);
                line.setWeightSum(6);
                line.setPadding(20, 40, 20, 40);

                TextView dateView = new TextView(context);
                dateView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                dateView.setText(returnedDate);
                dateView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                dateView.setGravity(Gravity.START);

                TextView nameView = new TextView(context);
                nameView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 4));
                nameView.setText(returnedName);
                nameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                nameView.setGravity(Gravity.START);

                TextView amountView = new TextView(context);
                amountView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                amountView.setText(currency + returnedAmount);
                amountView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                amountView.setGravity(Gravity.END);

                line.addView(dateView);
                line.addView(nameView);
                line.addView(amountView);

                l.addView(line);

                line.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(type.matches(NewSingleItemActivity.SINGLE_ITEM)){

                            Intent i1 = new Intent(context, SingleItemDetailsActivity.class);
                            Bundle b1 = new Bundle();
                            b1.putString("name", returnedName);
                            b1.putString("date", returnedDate);
                            b1.putString("amount", returnedAmount);
                            b1.putString("currency", currency);
                            b1.putString("details", returnedDescription);
                            b1.putLong("id", id);
                            i1.putExtras(b1);
                            context.startActivity(i1);

                        }else if(type.matches(NewSingleItemActivity.LIST)){

                            Intent i2 = new Intent(context, ListItemDetailsActivity.class);
                            Bundle b2 = new Bundle();
                            b2.putString("name",  returnedName);
                            b2.putString("date", returnedDate);
                            b2.putString("amount", returnedAmount);
                            b2.putString("currency", currency);
                            b2.putString("details", returnedDescription);
                            b2.putLong("id", id);
                            i2.putExtras(b2);
                            context.startActivity(i2);
                        }
                    }
                });
            }
        }
    }
}
