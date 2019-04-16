package com.csclub.busapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TableRow;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference myRef;
    private Button btn;
    private String[] buses;
    private boolean notNull = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display. getSize(size);
        int height = size.y;

        ImageView img = findViewById(R.id.image);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(0, (int) (height * 0.15), 0, (int) (height * 0.15));
        img.setLayoutParams(params);

        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(this);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("/");

        getBusInfo(new FirebaseCallback() {
            @Override
            public void onCallback(String[] a) {
                setInfo(a);
            }
        });
    }

    public void setInfo(String[] a) {
        this.buses = a;
        this.notNull = true;
    }

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
        if (notNull) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

            final String[] entries = buses;
            final int current = 0;

            builder.setTitle("Select Your Bus Number");
            builder.setSingleChoiceItems(entries, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {}
            });

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    ListView lw = ((AlertDialog)dialog).getListView();

                    try {
                        Object checkedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());

                        SharedPreferences localPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        localPrefs.edit().putString("new_bus_number", checkedItem.toString()).commit();

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);

                    } catch (ArrayIndexOutOfBoundsException e) {}
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
