package com.nectarmicrosystems.fundsmanager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
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
 * Created by oluwatobi on 11/15/2015.
 */
public class SetShoppingListName extends AppCompatActivity {
    TabHost tabHost;
    EditText nameField;
    EditText dateField;
    EditText description;
    Button addButton;
    Calendar calendar;
    DatePickerDialog.OnDateSetListener date;
    long insertId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.set_shopping_list_name_layout);

        tabHost = (TabHost)findViewById(R.id.listExpenseNameTabHost);
        nameField = (EditText)findViewById(R.id.shoppingListTitle);
        addButton = (Button)findViewById(R.id.createListBtn);
        description = (EditText)findViewById(R.id.shoppingListDescription);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameField.getText().toString().matches("")) {
                    Toast.makeText(getApplicationContext(), "give your list a name", Toast.LENGTH_SHORT).show();
                    return;
                }

                String listTitle = nameField.getText().toString();
                ExpenseItem entry = new ExpenseItem(SetShoppingListName.this);
                String setDescription = description.getText().toString();

                try {
                    entry.open();
                    insertId = entry.createEntry(listTitle + " (list)", "0", setDescription, NewSingleItemActivity.LIST, String.valueOf(insertId), dateField.getText().toString());
                    entry.close();
                }catch (SQLException e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "List creation failed" + e.toString(), Toast.LENGTH_SHORT).show();
                }finally {
                    Toast.makeText(getApplicationContext(), "List created successfully", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(SetShoppingListName.this, NewShoppingListActivity.class);
                    Bundle b = new Bundle();
                    b.putString("listTitle", listTitle);
                    b.putLong("insertId", insertId);
                    b.putString("date", dateField.getText().toString());
                    i.putExtras(b);
                    startActivity(i);
                }
            }
        });

        tabHost.setup();

        TabHost.TabSpec tab1Specs = tabHost.newTabSpec("newShopListName");
        tab1Specs.setContent(R.id.tab1);
        tab1Specs.setIndicator("New Shopping List");
        tabHost.addTab(tab1Specs);

        dateField = (EditText)findViewById(R.id.shoppingListDate);
        dateField.setInputType(InputType.TYPE_NULL);
        calendar = Calendar.getInstance();
        updateLabel();

        dateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(SetShoppingListName.this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
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

    @Override
    protected void onPause(){
        super.onPause();
        finish();
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dateField.setText(sdf.format(calendar.getTime()));
    }
}
