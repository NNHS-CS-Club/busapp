package com.csclub.busapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.TreeMap;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    // A reference to the Firebase Database that is connected to this app
    private DatabaseReference myRef;
    // An array of all the bus numbers
    private String[] buses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Finds the height of the user's display
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display. getSize(size);
        int height = size.y;

        // Sets the layout parameters and margins of the image
        ImageView img = findViewById(R.id.image);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(0, (int) (height * 0.15), 0, (int) (height * 0.15));
        img.setLayoutParams(params);

        // Adds a click listener to the button. When it is clicked, the method
        // onClick(View v) will be called
        Button btn = findViewById(R.id.btn);
        btn.setOnClickListener(this);

        // Gets a reference to the Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("/");

        // When the data has been read from the database, call setInfo(String[] a)
        // This callback function ensures that the data is read before calling
        // setInfo() and not after
        getBusInfo(new FirebaseCallback() {
            @Override
            public void onCallback(String[] a) {
                setInfo(a);
            }
        });
    }

    public void setInfo(String[] a) {
        // Now the global buses variable contains all the bus number
        this.buses = a;
    }

    // The interface that provides a template for what methods to implement
    // when using a callback function for reading in data from Firebase
    private interface FirebaseCallback {
        void onCallback(String[] a);
    }

    private void getBusInfo(final FirebaseCallback fireBaseCallback) {
        // Read from the database
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Map<String, String> temp = (Map<String, String>) dataSnapshot.getValue();
                Map<String, String> map = new TreeMap<>(temp);

                String[] buses = new String[map.size()];

                int count = 0;
                for (String key : ((TreeMap<String, String>) map).navigableKeySet()) {
                    if (key.contains("=")) {
                        key = key.substring(0, key.indexOf("="));
                    }
                    buses[count] = key;
                    count++;
                }

                fireBaseCallback.onCallback(buses);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Error");
            }
        };

        myRef.addValueEventListener(valueEventListener);
    }

    @Override
    public void onClick(View v) {
        // Ensures that the bus numbers have been loading into the buses variable
        if (buses != null) {
            // Builds a new AlertDialog that will show all the bus numbers so the user
            // can select theirs
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

            builder.setTitle("Select Your Bus Number");
            builder.setSingleChoiceItems(buses, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {}
            });

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Shows all the items in the bus number list
                    ListView lw = ((AlertDialog)dialog).getListView();

                    try {
                        Object checkedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());

                        // Stores the user's selected bus number locally so it can be remembered
                        // even when they close the app and open it later
                        SharedPreferences localPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        localPrefs.edit().putString("new_bus_number", checkedItem.toString()).commit();

                        // Changes the activity to the main one because the user has finished
                        // the login process
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);

                    } catch (ArrayIndexOutOfBoundsException e) {
                        // Catches if the user pressed 'OK' without selecting a bus number
                    }
                }
            });

            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
