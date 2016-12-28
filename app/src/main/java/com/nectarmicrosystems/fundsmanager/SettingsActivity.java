package com.nectarmicrosystems.fundsmanager;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

import java.sql.SQLException;

/**
 * Created by oluwatobi on 11/17/2015.
 */
public class SettingsActivity extends PreferenceActivity{
    ListPreference lp;
    public static SettingsActivity sa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
        sa = this;
        PreferenceScreen root = this.getPreferenceScreen();
        lp = (ListPreference) root.findPreference("defaultCurrencies");
        setupAddedCurrencies();
    }

    public void setupAddedCurrencies(){
        AddedCurrency entry = new AddedCurrency(this);
        try {
            entry.open();
            entry.complete(lp);
            entry.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void addCurrency(String curr, String lg){
        AddedCurrency entry = new AddedCurrency(this);
        try {
            entry.open();
            entry.createEntry(curr, lg);
            entry.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        setupAddedCurrencies();
    }

    @Override
    public void onResume(){
        super.onResume();
        setResult(RESULT_OK);
    }
}
