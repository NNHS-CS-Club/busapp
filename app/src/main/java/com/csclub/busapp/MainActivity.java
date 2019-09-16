package com.csclub.busapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    SharedPreferences localPrefs;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    private static final String CHANNEL_ID_1 = "Status Change";
    private static final String CHANNEL_ID_2 = "Bus Change";

    public boolean isConnectingToInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isConnectingToInternet()) {
            createNotificationChannels();

            FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");
            localPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            String userBusNumber = localPrefs.getString("userBusNumber", "-1");

            if (userBusNumber.equals("-1")) {
                Set<String> preferences = new HashSet<String>();
                preferences.add("CHANGES");
                preferences.add("HERE");
                preferences.add("LOADING");
                preferences.add("GONE");
                localPrefs.edit().putStringSet("notificationPreferences", preferences).apply();

                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            } else {
                setContentView(R.layout.activity_main);

                drawer = findViewById(R.id.drawer_layout);
                navigationView = findViewById(R.id.nav_view);
                navigationView.setNavigationItemSelectedListener(this);

                Toolbar toolbar = findViewById(R.id.toolbar);
                toolbar.setTitle("Bus App");
                setSupportActionBar(toolbar);

                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                drawer.addDrawerListener(toggle);

                navigationView.getMenu().getItem(0).setTitle("Naperville North High School");
                navigationView.getMenu().findItem(R.id.nav_user_bus).setTitle(userBusNumber);

                toggle.syncState();
                if (savedInstanceState == null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserBusFragment()).commit();
                    navigationView.setCheckedItem(R.id.nav_user_bus);
                }
            }
        } else {
            setContentView(R.layout.activity_no_connection);
        }
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String description = "Text";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_1, "Name", importance);
            channel.setDescription(description);
            channel.setVibrationPattern(new long[] { 0, 300, 200, 300 });
            channel.setLockscreenVisibility(NotificationManager.IMPORTANCE_HIGH);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            String description2 = "Text2";
            int importance2 = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel2 = new NotificationChannel(CHANNEL_ID_2, "Name2", importance2);
            channel2.setDescription(description2);
            channel2.setVibrationPattern(new long[] { 0, 300, 200, 300 });
            channel2.setLockscreenVisibility(NotificationManager.IMPORTANCE_HIGH);

            NotificationManager notificationManager2 = getSystemService(NotificationManager.class);
            notificationManager2.createNotificationChannel(channel2);
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
