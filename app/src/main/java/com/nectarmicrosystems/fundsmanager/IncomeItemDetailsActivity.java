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
public class IncomeItemDetailsActivity extends AppCompatActivity {
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
        this.setContentView(R.layout.income_item_details_activity_layout);

        tabHost = (TabHost)findViewById(R.id.incomeItemDetailsTabHost);
        tv1 = (TextView)findViewById(R.id.incomeItemAddedName);
        tv2 = (TextView)findViewById(R.id.incomeItemAddedAmount);
        tv3 = (TextView)findViewById(R.id.incomeItemAddedDescrip);
        tv4 = (TextView)findViewById(R.id.incomeItemAddedDate);
        edit = (Button)findViewById(R.id.editIncomeItem);
        delete = (Button)findViewById(R.id.deleteIncomeItem);

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
                Intent i1 = new Intent(IncomeItemDetailsActivity.this, EditIncomeItemActivity.class);
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
                IncomeItem entry = new IncomeItem(IncomeItemDetailsActivity.this);
                try {
                    entry.open();
                    entry.deleteEntry(id);
                    entry.close();
                }catch (SQLException e){
                    e.printStackTrace();
                }finally {
                    LaunchActivity.fa.finish();
                    Intent i = new Intent(IncomeItemDetailsActivity.this, LaunchActivity.class);
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
