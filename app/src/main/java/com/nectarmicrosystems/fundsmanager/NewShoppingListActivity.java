package com.nectarmicrosystems.fundsmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import java.sql.SQLException;

/**
 * Created by oluwatobi on 11/11/2015.
 */
public class NewShoppingListActivity extends AppCompatActivity {
    Button saveList;
    TabHost tabHost;
    String listTitle;
    long rowId;
    String date;
    Bundle b;
    LinearLayout l;
    LinearLayout totalLine;
    double totalAmount;
    TextView totalAmountView;
    public static NewShoppingListActivity fb;
    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.new_shopping_list_activity_layout);
        tabHost = (TabHost)findViewById(R.id.shoppingListTabHost);
        saveList = (Button)findViewById(R.id.saveListBtn);
        l = (LinearLayout)findViewById(R.id.shopListDynamicHold);
        totalLine = (LinearLayout)findViewById(R.id.totalLine);
        totalAmountView = (TextView)findViewById(R.id.totalAmount);
        fb = this;
        this.totalAmount = 0;

        saveList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpenseItem eTemp = new ExpenseItem(NewShoppingListActivity.this);

                try{
                    eTemp.open();
                    eTemp.updateListTotal(rowId, String.valueOf(totalAmount));
                    eTemp.close();
                }catch (SQLException e){
                    e.printStackTrace();
                }

                finish();
                LaunchActivity.fa.finish();
                Intent i = new Intent(NewShoppingListActivity.this, LaunchActivity.class);
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });

        tabHost.setup();

        b = getIntent().getExtras();
        if(b != null) {
            listTitle = b.getString("listTitle");
            rowId = b.getLong("insertId");
            date = b.getString("date");
        }

        TabHost.TabSpec tab1Specs = tabHost.newTabSpec("newShopList");
        tab1Specs.setContent(R.id.tab1);
        tab1Specs.setIndicator(listTitle + " Shopping List");
        tabHost.addTab(tab1Specs);
    }

    public void addNewItemToList(View v){
        Intent i = new Intent(NewShoppingListActivity.this, NewListItemActivity.class);
        Bundle b = new Bundle();
        b.putString("listGroup", String.valueOf(rowId));
        b.putString("date", date);
        i.putExtras(b);
        startActivity(i);
    }

    public void updateList(String title, String amount){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String currency = prefs.getString("defaultCurrencies", "$");

        if(saveList.getVisibility() == View.INVISIBLE)
            saveList.setVisibility(View.VISIBLE);

        if(totalLine.getVisibility() == View.INVISIBLE)
            totalLine.setVisibility(View.VISIBLE);

        totalAmount += Double.parseDouble(amount);
        totalAmountView.setText(currency + String.valueOf(totalAmount));

        TextView tv = (TextView)findViewById(R.id.addItemHint);
        tv.setVisibility(View.GONE);

        LinearLayout line = new LinearLayout(context);
        line.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        line.setOrientation(LinearLayout.HORIZONTAL);
        line.setBackgroundResource(R.drawable.back);
        line.setWeightSum(100);
        line.setPadding(20, 40, 20, 40);

        TextView nameView = new TextView(context);
        nameView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 70));
        nameView.setText(title);
        nameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        nameView.setGravity(Gravity.START);

        TextView priceView = new TextView(context);
        priceView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 30));
        priceView.setText(currency + amount);
        priceView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        priceView.setGravity(Gravity.END);

        line.addView(nameView);
        line.addView(priceView);

        l.addView(line);
    }

    @Override
    public void onPause(){
        super.onPause();
        ExpenseItem eTemp = new ExpenseItem(NewShoppingListActivity.this);

        try{
            eTemp.open();
            eTemp.updateListTotal(rowId, String.valueOf(totalAmount));
            eTemp.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}