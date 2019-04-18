package com.csclub.busapp;

import android.app.ActionBar;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupActionBar();

        // Creates a Fragment to show the settings
        getFragmentManager().beginTransaction().replace(android.R.id.content, new GeneralPreferenceFragment()).commit();
    }

    private void setupActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            // Get a reference to the Firebase Database
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("/");

            // Read from the database
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.

                    // Map variable is a TreeMap because a TreeMap can sort the keys \
                    // (bus numbers) in order
                    TreeMap<String, String> map = new TreeMap<>((HashMap<String, String>)dataSnapshot.getValue());

                    String[] buses = new String[map.size()];

                    int count = 0;
                    for (String key : map.navigableKeySet()) {
                        // If the bus number has an = sign (a bus change), only show the
                        // original bus number in the settings
                        if (key.contains("=")) {
                            key = key.substring(0, key.indexOf("="));
                        }

                        // Add the sorted bus numbers to the bus array
                        buses[count] = key;
                        count++;
                    }

                    // Fill in the entries and entry values of the list preference defined in
                    // settings.xml
                    ListPreference listPreference = (ListPreference) findPreference("new_bus_number");
                    listPreference.setEntries(buses);
                    listPreference.setEntryValues(buses);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    System.out.println("Error");
                }
            };

            // Listen for any changes in the database
            myRef.addValueEventListener(valueEventListener);
        }
    }
}
