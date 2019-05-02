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
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference myRef;
    private String[] buses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display. getSize(size);
        int height = size.y;

        TextView title = findViewById(R.id.title);
        RelativeLayout.LayoutParams titleParams = (RelativeLayout.LayoutParams) title.getLayoutParams();
        titleParams.setMargins(0, height / 5, 0, 0);

        ImageView image = findViewById(R.id.image);
        RelativeLayout.LayoutParams imageParams = (RelativeLayout.LayoutParams) image.getLayoutParams();
        imageParams.setMargins(0, height / 25, 0, height / 25);
        image.setScaleX((float) (height) / (height + 500));
        image.setScaleY((float) (height) / (height + 500));

        Button btn = findViewById(R.id.btn);
        btn.setOnClickListener(this);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("/buses/");

        getBusInfo(new FirebaseCallback() {
            @Override
            public void onCallback(String[] a) {
                setInfo(a);
            }
        });
    }

    public void setInfo(String[] a) {
        this.buses = a;
    }

    private interface FirebaseCallback {
        void onCallback(String[] a);
    }

    private void getBusInfo(final FirebaseCallback fireBaseCallback) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int dataLength = (int) (dataSnapshot.getChildrenCount());
                String[] buses = new String[dataLength];

                int count = 0;
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
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
        if (buses != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

            builder.setTitle("Select Your Bus Number");
            builder.setSingleChoiceItems(buses, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {}
            });

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    ListView lw = ((AlertDialog)dialog).getListView();
                    try {
                        Object checkedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());

                        SharedPreferences localPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        localPrefs.edit().putString("userBusNumber", checkedItem.toString()).apply();

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);

                    } catch (ArrayIndexOutOfBoundsException e) {

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

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}