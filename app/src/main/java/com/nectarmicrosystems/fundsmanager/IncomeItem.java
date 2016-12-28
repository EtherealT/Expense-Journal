package com.nectarmicrosystems.fundsmanager;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;

/**
 * Created by oluwatobi on 11/11/2015.
 */
public class IncomeItem {
    private static final String DATABASE_NAME = "prudent me";
    private static final String DATABASE_TABLE = "income";
    private static final int DATABASE_VERSION = 1;

    public static final String KEY_ROW_ID = "_id";
    public static final String TITLE = "title";
    public static final String AMOUNT = "amount";
    public static final String DESCRIPTION = "description";
    public static final String ON_DAY = "on_day";

    private DbHelper helper;
    private final Context context;
    private SQLiteDatabase database;

    public IncomeItem(Context c){
        this.context = c;
    }

    private static class DbHelper extends SQLiteOpenHelper{
        public DbHelper(Context c){
            super(c, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + DATABASE_TABLE + " (" +
                            KEY_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            TITLE + " TEXT NOT NULL, " +
                            AMOUNT + " TEXT NOT NULL, " +
                            DESCRIPTION + " TEXT, " +
                            ON_DAY + " TEXT NOT NULL)"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

    public IncomeItem open() throws SQLException {
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

    public void updateEntry(long id, String title, String amount, String description, String date){
        ContentValues cv = new ContentValues();
        cv.put(TITLE, title);
        cv.put(AMOUNT, amount);
        cv.put(DESCRIPTION, description);
        cv.put(ON_DAY, date);
        database.update(DATABASE_TABLE, cv, KEY_ROW_ID + " = " + id, null);
    }

    public long createEntry(String title, String amount, String description, String date){
        ContentValues cv = new ContentValues();
        cv.put(TITLE, title);
        cv.put(AMOUNT, amount);
        cv.put(DESCRIPTION, description);
        cv.put(ON_DAY, date);
        return database.insert(DATABASE_TABLE, null, cv);
    }

    public void displayIncome(LinearLayout l){
        //check if empty
        boolean empty = false;
        Cursor cur = database.rawQuery("SELECT COUNT(*) FROM " + DATABASE_TABLE, null);
        if (cur != null && cur.moveToFirst()) {
            empty = (cur.getInt (0) == 0);
        }
        cur.close();

        if(empty){
            Toast.makeText(context, "No Income to display", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final String currency = prefs.getString("defaultCurrencies", "$");

        String[] columns = new String[]{KEY_ROW_ID, TITLE, AMOUNT, DESCRIPTION, ON_DAY};
        Cursor c = database.query(DATABASE_TABLE, columns, null, null, null, null, null);

        int iTitle = c.getColumnIndex(TITLE);
        int iAmount = c.getColumnIndex(AMOUNT);
        int iDate = c.getColumnIndex(ON_DAY);
        int iDescription = c.getColumnIndex(DESCRIPTION);
        int iRow = c.getColumnIndex(KEY_ROW_ID);

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
            final String returnedName = c.getString(iTitle);
            final String returnedAmount = c.getString(iAmount);
            final String returnedDate = c.getString(iDate);
            final String returnedDescription = c.getString(iDescription);
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
                    Intent i1 = new Intent(context, IncomeItemDetailsActivity.class);
                    Bundle b1 = new Bundle();
                    b1.putString("name", returnedName);
                    b1.putString("date", returnedDate);
                    b1.putString("amount", returnedAmount);
                    b1.putString("currency", currency);
                    b1.putString("details", returnedDescription);
                    b1.putLong("id", id);
                    i1.putExtras(b1);
                    context.startActivity(i1);
                }
            });
        }
    }
}
