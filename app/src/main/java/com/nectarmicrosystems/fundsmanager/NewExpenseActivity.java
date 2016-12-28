package com.nectarmicrosystems.fundsmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TextView;

/**
 * Created by oluwatobi on 11/10/2015.
 */
public class NewExpenseActivity extends AppCompatActivity implements View.OnClickListener{
    TextView singleItem;
    TextView shoppingList;
    TabHost tabHost;

    @Override
        protected void onCreate (Bundle savedInstanceState){
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.new_expense_activity_layout);
        tabHost = (TabHost)findViewById(R.id.newExpTabHost);
        tabHost.setup();

        TabHost.TabSpec tab1Specs = tabHost.newTabSpec("newExp");
        tab1Specs.setContent(R.id.tab1);
        tab1Specs.setIndicator("New Expense");
        tabHost.addTab(tab1Specs);

        singleItem = (TextView)findViewById(R.id.newSingleItem);
        shoppingList = (TextView)findViewById(R.id.newShoppingList);
    }

    @Override
    protected void onPause(){
        super.onPause();
        finish();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.newSingleItem){
            Intent i = new Intent(NewExpenseActivity.this, NewSingleItemActivity.class);
            startActivity(i);
        }

        if(v.getId() == R.id.newShoppingList){
            Intent i = new Intent(NewExpenseActivity.this, SetShoppingListName.class);
            startActivity(i);
        }
    }
}
