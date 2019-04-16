package com.csclub.busapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.FirebaseApp;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    SharedPreferences localPrefs;
    private DrawerLayout drawer;

    private String school = "Naperville North High School";
    private String userBusNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        localPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        userBusNumber = localPrefs.getString("new_bus_number", "0");

        if (userBusNumber.equals("0")) {
            Set<String> preferences = new HashSet<String>();
            preferences.add("CHANGES");
            preferences.add("HERE");
            preferences.add("LOADING");
            preferences.add("GONE");

            localPrefs.edit().putStringSet("notification_preferences", preferences).commit();

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            setContentView(R.layout.activity_main);

            drawer = findViewById(R.id.drawer_layout);
            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle(userBusNumber);
            setSupportActionBar(toolbar);

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);

            navigationView.getMenu().getItem(0).setTitle(school);
            navigationView.getMenu().findItem(R.id.nav_user_bus).setTitle(userBusNumber);

            toggle.syncState();
            if (savedInstanceState == null) {

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserBusFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_user_bus);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (userBusNumber.equals("0")) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {

            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            localPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            userBusNumber = localPrefs.getString("new_bus_number", "0");

            Set<String> selections = localPrefs.getStringSet("notification_preferences", null);
            String[] selected = selections.toArray(new String[]{});

            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle(userBusNumber);
            setSupportActionBar(toolbar);

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);

            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.nav_user_bus).setTitle(userBusNumber);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserBusFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_user_bus);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_user_bus:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserBusFragment()).commit();
                break;
            case R.id.nav_all_buses:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AllBusesFragment()).commit();
                break;
            case R.id.nav_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            // case R.id.nav_logout:
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
