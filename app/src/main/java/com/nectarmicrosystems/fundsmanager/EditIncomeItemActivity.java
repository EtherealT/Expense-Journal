package com.nectarmicrosystems.fundsmanager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
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
 * Created by oluwatobi on 11/18/2015.
 */
public class EditIncomeItemActivity extends AppCompatActivity {
    TabHost tabHost;
    EditText nameField;
    EditText amountField;
    EditText descriptionField;
    Button addButton;
    EditText dateField;
    Calendar calendar;
    DatePickerDialog.OnDateSetListener date;
    Bundle b;

    String name;
    String dateValue;
    String amount;
    String details;
    String currency;
    long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.edit_income_item_activity_layout);

        tabHost = (TabHost)findViewById(R.id.editIncomeItemTabHost);
        nameField = (EditText)findViewById(R.id.editIncomeItemName);
        amountField = (EditText)findViewById(R.id.editIncomeItemAmount);
        descriptionField = (EditText)findViewById(R.id.editIncomeItemDescription);
        addButton = (Button)findViewById(R.id.editIncomeItemBtn);
        dateField = (EditText)findViewById(R.id.editIncomeItemInputDate);
        dateField.setInputType(InputType.TYPE_NULL);

        dateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditIncomeItemActivity.this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd/MM/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                dateField.setText(sdf.format(calendar.getTime()));
            }
        };

        tabHost.setup();

        TabHost.TabSpec tab1Specs = tabHost.newTabSpec("editItm");
        tab1Specs.setContent(R.id.tab1);
        tab1Specs.setIndicator("Edit Item");
        tabHost.addTab(tab1Specs);

        b = getIntent().getExtras();
        if(b != null) {
            name = b.getString("name");
            dateValue = b.getString("date");
            amount = b.getString("amount");
            details = b.getString("details");
            currency = b.getString("currency");
            id = b.getLong("id");
        }

        nameField.setText(name);
        amountField.setText(amount);
        descriptionField.setText(details);
        dateField.setText(dateValue);

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

                IncomeItem entry = new IncomeItem(EditIncomeItemActivity.this);
                try {
                    entry.open();
                    entry.updateEntry(id, setName, setAmount, setDescription, setDate);
                    entry.close();
                }catch (SQLException e){
                    e.printStackTrace();
                }finally {
                    Toast.makeText(getApplicationContext(), "Item updated", Toast.LENGTH_SHORT).show();
                    LaunchActivity.fa.finish();
                    Intent i = new Intent(EditIncomeItemActivity.this, LaunchActivity.class);
                    startActivity(i);
                    overridePendingTransition(0, 0);

                    Intent i1 = new Intent(EditIncomeItemActivity.this, IncomeItemDetailsActivity.class);
                    Bundle b1 = new Bundle();
                    b1.putString("name", setName);
                    b1.putString("date", setDate);
                    b1.putString("amount", setAmount);
                    b1.putString("currency", currency);
                    b1.putString("details", setDescription);
                    b1.putLong("id", id);
                    i1.putExtras(b1);
                    startActivity(i1);
                    overridePendingTransition(0, 0);
                    finish();
                }
            }
        });
    }
}
