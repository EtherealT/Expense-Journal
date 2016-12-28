package com.nectarmicrosystems.fundsmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.Toast;

import java.sql.SQLException;

/**
 * Created by oluwatobi on 11/19/2015.
 */
public class EditListActivity extends AppCompatActivity {
    TabHost tabHost;
    String name;
    String amount;
    String details;
    String date;
    String currency;
    long id;
    Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.edit_list_activity_layout);

        b = getIntent().getExtras();
        if(b != null) {
            name = b.getString("name");
            date = b.getString("date");
            amount = b.getString("amount");
            details = b.getString("details");
            currency = b.getString("currency");
            id = b.getLong("id");
        }

        tabHost = (TabHost)findViewById(R.id.editShoppingListTabHost);

        tabHost.setup();

        TabHost.TabSpec tab1Specs = tabHost.newTabSpec("editList");
        tab1Specs.setContent(R.id.tab1);
        tab1Specs.setIndicator("List Contents");
        tabHost.addTab(tab1Specs);

        ExpenseItem entry = new ExpenseItem(EditListActivity.this);
        LinearLayout temp = (LinearLayout) findViewById(R.id.listContentPanel);

        try {
            entry.open();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            entry.displayListItems(id, temp);
            entry.close();
        }
    }
}
