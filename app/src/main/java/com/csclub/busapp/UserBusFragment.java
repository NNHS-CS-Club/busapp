package com.csclub.busapp;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserBusFragment extends Fragment {

    private int height;
    private String userBusNumber;

    private View view;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_bus, container, false);
        navigationView = getActivity().findViewById(R.id.nav_view);
        toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Bus App");

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        height = size.y;

        SharedPreferences localPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        userBusNumber = localPrefs.getString("userBusNumber", "");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("/buses/");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                toolbar.setTitle("Bus App");
                TextView busTextView = view.findViewById(R.id.user_bus);
                busTextView.setHeight(height / 2);

                TextView statusTextView = view.findViewById(R.id.bus_status);
                statusTextView.setPadding(0, height / 3, 0, 0);
                statusTextView.setHeight(height / 2);

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String bus = postSnapshot.child("Bus").getValue().toString();
                    if (userBusNumber.equals(bus)) {
                        String change = postSnapshot.child("Change").getValue().toString();
                        if (change.equals("")) {
                            busTextView.setText("Bus " + bus);
                        } else {
                            busTextView.setText("Bus " + bus + "=" + change);
                        }
                        String status = postSnapshot.child("Status").getValue().toString();
                        statusTextView.setText(status);
                        setStatusColor(statusTextView, status);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Error");
            }
        };

        myRef.addValueEventListener(valueEventListener);
        return view;
    }

    private static void setStatusColor(TextView statusTextView, String status) {
        if (status.equals("HERE")) {
            statusTextView.setTextColor(Color.parseColor("#4285F4"));
        } else if (status.equals("LOADING")) {
            statusTextView.setTextColor(Color.parseColor("#0F9D58"));
        } else if (status.equals("GONE")) {
            statusTextView.setTextColor(Color.parseColor("#DB4437"));
        } else {
            statusTextView.setTextColor(Color.parseColor("#000000"));
        }
    }
}