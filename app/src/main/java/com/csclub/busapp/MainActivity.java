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

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    // Reference to the shared preferences stored on the user's device
    SharedPreferences localPrefs;
    // Reference to the navigation drawer
    private DrawerLayout drawer;
    // Hard coded school name. If there are multiple schools this can change.
    private String school = "Naperville North High School";
    // Reference to the user's bus number stored in the shared preferences
    private String userBusNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        localPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // User's bus nuumber is stored in the key 'new_bus_number', if it doesn't exist
        // then the default value is 0
        userBusNumber = localPrefs.getString("new_bus_number", "0");

        // The only time the user's bus number would be the default value would be when they
        // first open the app.
        if (userBusNumber.equals("0")) {
            // The key 'notification_preferences' stores the user's notifications preferences
            // By default the user will receive notifications for all of them
            Set<String> preferences = new HashSet<String>();
            preferences.add("CHANGES");
            preferences.add("HERE");
            preferences.add("LOADING");
            preferences.add("GONE");

            localPrefs.edit().putStringSet("notification_preferences", preferences).commit();

            // This redirects them to the Login page because it's their first time using the app
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            // If it isn't the user's first time, load the main layout
            setContentView(R.layout.activity_main);

            // Adds a listener for the items selected on the Navigation Drawer
            drawer = findViewById(R.id.drawer_layout);
            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            // Sets the title of the toolbar as the user's bus number and sets it as the
            // main action bar
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle(userBusNumber);
            setSupportActionBar(toolbar);

            // Toggles between an open and closed state of the navigation drawer when the
            // hamburger menu in the top left is pressed
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);

            // Sets the title of the first item in drawer_menu.xml from the default 'School'
            // title to the actual school variable. It also changes the named of the user's
            // bus from 'User Bus' to their actual bus number
            navigationView.getMenu().getItem(0).setTitle(school);
            navigationView.getMenu().findItem(R.id.nav_user_bus).setTitle(userBusNumber);

            toggle.syncState();
            // This checks to make sure that the app isn't loading the UserBusFragment twice,
            // only the first time
            if (savedInstanceState == null) {
                // Loads the UserBusFragment class
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserBusFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_user_bus);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Does the same things as the onCreate method, but instead when this activity
        // is resumed. For example when a user switches between apps, most of the time
        // their activities are resuming, not being entirely created again
        if (userBusNumber.equals("0")) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {

            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            localPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            userBusNumber = localPrefs.getString("new_bus_number", "0");

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
        // If the user selects their bus number in the Navigation Drawer, the app loads the
        // UserBusFragment. Similarly, selecting All Buses will load AllBusesFragment and
        // selecting Settings would load the Settings Activity
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
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        // If the drawer is open, close the drawer, otherwise exit the app
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
