package com.csclub.busapp;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        getFragmentManager().beginTransaction().replace(android.R.id.content, new GeneralPreferenceFragment()).commit();
    }

    private void setupActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("/buses/");

            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int dataLength = (int) (dataSnapshot.getChildrenCount());

                    String[] buses = new String[dataLength];
                    ArrayList<Integer> normalBuses = new ArrayList<>();
                    ArrayList<String> specialBuses = new ArrayList<>();

                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        String bus = postSnapshot.child("Bus").getValue().toString();
                        try {
                            Integer intBus = Integer.parseInt(bus);
                            normalBuses.add(intBus);
                        } catch (NumberFormatException e) {
                            specialBuses.add(bus);
                        }
                    }

                    Collections.sort(normalBuses);
                    Collections.sort(specialBuses);

                    int count = 0;
                    for (Integer i : normalBuses) {
                        buses[count] = i.toString();
                        count += 1;
                    }
                    for (String s : specialBuses) {
                        buses[count] = s;
                        count += 1;
                    }

                    final ListPreference listPreference = (ListPreference) findPreference("userBusNumber");
                    listPreference.setEntries(buses);
                    listPreference.setEntryValues(buses);

                    Preference.OnPreferenceChangeListener preferenceChangeListener = new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object o) {
                            String userBusNumber = listPreference.getValue();
                            FirebaseMessaging.getInstance().unsubscribeFromTopic(userBusNumber);

                            String newBusNumber = o.toString();
                            getPreferenceManager().getSharedPreferences().edit().putString("userBusNumber", newBusNumber).apply();
                            FirebaseMessaging.getInstance().subscribeToTopic(newBusNumber);
                            return true;
                        }
                    };

                    listPreference.setOnPreferenceChangeListener(preferenceChangeListener);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    System.out.println("Error");
                }
            };

            myRef.addValueEventListener(valueEventListener);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}