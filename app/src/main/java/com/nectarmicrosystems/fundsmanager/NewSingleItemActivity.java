package com.nectarmicrosystems.fundsmanager;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by oluwatobi on 11/11/2015.
 */
public class NewSingleItemActivity extends AppCompatActivity{
    TabHost tabHost;
    EditText nameField;
    EditText amountField;
    EditText descriptionField;
    Button addButton;
    EditText dateField;
    Calendar calendar;
    DatePickerDialog.OnDateSetListener date;

    public static final String SINGLE_ITEM = "single_item";
    public static final String LIST_ITEM = "list_item";
    public static final String LIST = "list";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.new_single_item_activity_layout);
        tabHost = (TabHost)findViewById(R.id.singleItemTabHost);
        nameField = (EditText)findViewById(R.id.addSingleItemName);
        amountField = (EditText)findViewById(R.id.addSingleItemAmount);
        descriptionField = (EditText)findViewById(R.id.addSingleItemDescription);
        addButton = (Button)findViewById(R.id.addSingleItemBtn);

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
                String setDate;

                setName = nameField.getText().toString();
                setAmount = amountField.getText().toString();
                setDescription = descriptionField.getText().toString();
                setDate = dateField.getText().toString();

                ExpenseItem entry = new ExpenseItem(NewSingleItemActivity.this);

                try {
                    entry.open();
                    entry.createEntry(setName, setAmount, setDescription, SINGLE_ITEM, "",setDate);
                    entry.close();
                }catch (SQLException e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Insert Failed" + e.toString(), Toast.LENGTH_SHORT).show();
                }finally {
                    Toast.makeText(getApplicationContext(), "Item added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                    LaunchActivity.fa.finish();
                    Intent i = new Intent(NewSingleItemActivity.this, LaunchActivity.class);
                    startActivity(i);
                    overridePendingTransition(0, 0);
                }

            }
        });

        tabHost.setup();

        TabHost.TabSpec tab1Specs = tabHost.newTabSpec("newExpItm");
        tab1Specs.setContent(R.id.tab1);
        tab1Specs.setIndicator("New Expense Item");
        tabHost.addTab(tab1Specs);

        dateField = (EditText)findViewById(R.id.singleItemInputDate);
        dateField.setInputType(InputType.TYPE_NULL);
        calendar = Calendar.getInstance();
        updateLabel();

        dateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(NewSingleItemActivity.this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dateField.setText(sdf.format(calendar.getTime()));
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu){
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onPause(){
        super.onPause();
        finish();
    }
}