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
 * Created by oluwatobi on 11/15/2015.
 */
public class NewListItemActivity extends AppCompatActivity {
    TabHost tabHost;
    EditText nameField;
    EditText amountField;
    EditText descriptionField;
    Button addButton;
    Bundle b;
    String listGroup;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.new_list_item_activity_layout);

        tabHost = (TabHost)findViewById(R.id.listItemTabhost);
        nameField = (EditText)findViewById(R.id.listItemName);
        amountField = (EditText)findViewById(R.id.listItemAmount);
        descriptionField = (EditText)findViewById(R.id.listItemDescription);
        addButton = (Button)findViewById(R.id.addListItemBtn);

        b = getIntent().getExtras();
        if(b != null) {
            listGroup = b.getString("listGroup");
            date = b.getString("date");
        }

        tabHost.setup();

        TabHost.TabSpec tab1Specs = tabHost.newTabSpec("newListItm");
        tab1Specs.setContent(R.id.tab1);
        tab1Specs.setIndicator("New List Item");
        tabHost.addTab(tab1Specs);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nameField.getText().toString().matches("")) {
                    Toast.makeText(getApplicationContext(), "A name is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(amountField.getText().toString().matches("")) {
                    Toast.makeText(getApplicationContext(), "An amount is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                String setName;
                String setAmount;
                String setDescription;

                setName = nameField.getText().toString();
                setAmount = amountField.getText().toString();
                setDescription = descriptionField.getText().toString();

                ExpenseItem entry = new ExpenseItem(NewListItemActivity.this);

                try {
                    entry.open();
                    entry.createEntry(setName, setAmount, setDescription, NewSingleItemActivity.LIST_ITEM, listGroup, date);
                    entry.close();
                }catch (SQLException e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Insert Failed" + e.toString(), Toast.LENGTH_SHORT).show();
                }finally {
                    Toast.makeText(getApplicationContext(), "Item added successfully", Toast.LENGTH_SHORT).show();
                    NewShoppingListActivity.fb.updateList(setName, setAmount);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onPause(){
        super.onPause();
        finish();
    }
}
