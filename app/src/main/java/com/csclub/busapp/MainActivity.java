package com.csclub.busapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    SharedPreferences localPrefs;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    private void setupNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Status Change";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("0", name, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            name = "Bus Change";
            channel = new NotificationChannel("1", name, importance);
            notificationManager.createNotificationChannel((channel));
            notificationManager.deleteNotificationChannel("fcm_fallback_notification_channel");
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupNotificationChannels();

        localPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        final String userBusNumber = localPrefs.getString("userBusNumber", "");

        if (userBusNumber.equals("")) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("/buses/");

            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                 @Override
                 public void onDataChange(DataSnapshot snapshot) {
                     boolean found = false;
                     for (DataSnapshot child : snapshot.getChildren()) {
                        if (child.child("Bus").getValue().toString().equals(userBusNumber)) {
                            found = true;
                            break;
                        }
                     }
                     if (!(found)) {
                         Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                         startActivity(intent);
                     } else {
                         setContentView(R.layout.activity_main);

                         drawer = findViewById(R.id.drawer_layout);
                         navigationView = findViewById(R.id.nav_view);
                         navigationView.setNavigationItemSelectedListener(MainActivity.this);

                         Toolbar toolbar = findViewById(R.id.toolbar);
                         toolbar.setTitle("Bus App");
                         setSupportActionBar(toolbar);

                         ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                         drawer.addDrawerListener(toggle);

                         navigationView.getMenu().getItem(0).setTitle("Naperville North High School");

                         toggle.syncState();
                         if (savedInstanceState == null) {
                             getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserBusFragment()).commit();
                             navigationView.setCheckedItem(R.id.nav_user_bus);
                         }
                     }
                 }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
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
