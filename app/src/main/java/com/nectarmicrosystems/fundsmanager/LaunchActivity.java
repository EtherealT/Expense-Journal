package com.nectarmicrosystems.fundsmanager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.Toast;
import java.sql.SQLException;

/**
 * Created by oluwatobi on 11/13/2015.
 */
public class LaunchActivity extends AppCompatActivity {
    TabHost tabHost;
    public static Activity fa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.launch_activity_layout);

        fa = this;

        tabHost = (TabHost) findViewById(R.id.tabhost);
        fa = this;

        tabHost.setup();

        TabHost.TabSpec tab1Specs = tabHost.newTabSpec("expensesTab");
        tab1Specs.setContent(R.id.tab1);
        tab1Specs.setIndicator("Expenses");
        tabHost.addTab(tab1Specs);

        TabHost.TabSpec tab2Specs = tabHost.newTabSpec("incomeTab");
        tab2Specs.setContent(R.id.tab2);
        tab2Specs.setIndicator("Income");
        tabHost.addTab(tab2Specs);

        //add this function in version 1.0.1
       /* TabHost.TabSpec tab3Specs = tabHost.newTabSpec("reportsTab");
        tab3Specs.setContent(R.id.tab3);
        tab3Specs.setIndicator("Reports");
        tabHost.addTab(tab3Specs);*/

        ExpenseItem entry = new ExpenseItem(LaunchActivity.this);
        IncomeItem entry2 = new IncomeItem(LaunchActivity.this);
        LinearLayout temp = (LinearLayout) findViewById(R.id.dynamicHold);
        LinearLayout temp2 = (LinearLayout) findViewById(R.id.incomeDynamicHold);
        //load expenses
        try {
            entry.open();
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "print failed:" + e.toString(), Toast.LENGTH_SHORT).show();
        } finally {
            entry.displayExpenses(temp);
            entry.close();
        }

        //load income
        try {
            entry2.open();
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "print failed:" + e.toString(), Toast.LENGTH_SHORT).show();
        } finally {
            entry2.displayIncome(temp2);
            entry2.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);

        switch (item.getItemId()){

            case R.id.settings:
                Intent i = new Intent(LaunchActivity.this, SettingsActivity.class);
                startActivityForResult(i, 0);
                break;

            case R.id.feedback:
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                break;

        }

        return false;
    }

    public void fabClicked(View v) {
        if (v.getId() == R.id.myFAB1) {
            Intent i = new Intent(LaunchActivity.this, NewExpenseActivity.class);
            startActivity(i);
        }

        if (v.getId() == R.id.myFAB2) {
            Intent i = new Intent(LaunchActivity.this, NewIncomeItemActivity.class);
            startActivity(i);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            finish();
            LaunchActivity.fa.finish();
            Intent i = new Intent(LaunchActivity.this, LaunchActivity.class);
            startActivity(i);
            overridePendingTransition(0, 0);
        }
    }
}