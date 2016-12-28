package com.nectarmicrosystems.fundsmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;

/**
 * Created by oluwatobi on 11/18/2015.
 */
public class SingleItemDetailsActivity extends AppCompatActivity {
    TabHost tabHost;
    TextView tv1;
    TextView tv2;
    TextView tv3;
    TextView tv4;
    Button edit;
    Button delete;
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
        this.setContentView(R.layout.single_item_details_activity_layout);

        tabHost = (TabHost)findViewById(R.id.itemDetailsTabHost);
        tv1 = (TextView)findViewById(R.id.singleItemAddedName);
        tv2 = (TextView)findViewById(R.id.singleItemAddedAmount);
        tv3 = (TextView)findViewById(R.id.singleItemAddedDescrip);
        tv4 = (TextView)findViewById(R.id.singleItemAddedDate);
        edit = (Button)findViewById(R.id.editSingleItem);
        delete = (Button)findViewById(R.id.deleteSingleItem);

        tabHost.setup();

        TabHost.TabSpec tab1Specs = tabHost.newTabSpec("itemDetails");
        tab1Specs.setContent(R.id.tab1);
        tab1Specs.setIndicator("Item Details");
        tabHost.addTab(tab1Specs);

        b = getIntent().getExtras();
        if(b != null) {
            name = b.getString("name");
            date = b.getString("date");
            amount = b.getString("amount");
            details = b.getString("details");
            currency = b.getString("currency");
            id = b.getLong("id");
        }

        tv1.setText(name);
        tv2.setText(currency + amount);
        tv3.setText(details);
        tv4.setText(date);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1 = new Intent(SingleItemDetailsActivity.this, EditItemActivity.class);
                Bundle b1 = new Bundle();
                b1.putString("name", name);
                b1.putString("date", date);
                b1.putString("amount", amount);
                b1.putString("currency", currency);
                b1.putString("details", details);
                b1.putLong("id", id);
                i1.putExtras(b1);
                startActivity(i1);
                finish();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpenseItem entry = new ExpenseItem(SingleItemDetailsActivity.this);
                try {
                    entry.open();
                    entry.deleteEntry(id);
                    entry.close();
                }catch (SQLException e){
                    e.printStackTrace();
                }finally {
                    LaunchActivity.fa.finish();
                    Intent i = new Intent(SingleItemDetailsActivity.this, LaunchActivity.class);
                    startActivity(i);
                    overridePendingTransition(0, 0);
                    Toast.makeText(getApplicationContext(), "Item Deleted", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    @Override
    public void onPause(){
        super.onPause();
        finish();
    }
}
