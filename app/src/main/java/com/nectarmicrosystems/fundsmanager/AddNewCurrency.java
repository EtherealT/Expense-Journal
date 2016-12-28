package com.nectarmicrosystems.fundsmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;

import java.sql.SQLException;

/**
 * Created by oluwatobi on 11/17/2015.
 */
public class AddNewCurrency extends AppCompatActivity{
    TabHost tabHost;
    EditText nameField;
    EditText amountField;
    Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.add_new_currency_layout);

        tabHost = (TabHost)findViewById(R.id.newCurrencyTabHost);
        nameField = (EditText)findViewById(R.id.currencyName);
        amountField = (EditText)findViewById(R.id.currencySign);
        addButton = (Button)findViewById(R.id.addCurrency);

        tabHost.setup();

        TabHost.TabSpec tab1Specs = tabHost.newTabSpec("newCurr");
        tab1Specs.setContent(R.id.tab1);
        tab1Specs.setIndicator("New Currency");
        tabHost.addTab(tab1Specs);

        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(nameField.getText().toString().matches("")) {
                    Toast.makeText(getApplicationContext(), "A name is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(amountField.getText().toString().matches("")) {
                    Toast.makeText(getApplicationContext(), "logo or iso required", Toast.LENGTH_SHORT).show();
                    return;
                }

                SettingsActivity.sa.addCurrency(nameField.getText().toString(), amountField.getText().toString());
                finish();
            }
        });
    }
}
