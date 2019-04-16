package com.csclub.busapp;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
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
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class UserBusFragment extends Fragment {


    private TextView textView;
    private String userBusNumber;
    private View view;
    private DatabaseReference myRef;
    private Display display;
    private Toolbar toolbar;
    private NavigationView navigationView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_bus, container, false);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("/");

        navigationView = getActivity().findViewById(R.id.nav_view);

        toolbar = getActivity().findViewById(R.id.toolbar);

        display = getActivity().getWindowManager().getDefaultDisplay();

        textView = (TextView) view.findViewById(R.id.user_bus);
        SharedPreferences localPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        userBusNumber = localPrefs.getString("new_bus_number", "0");

        textView.setText(userBusNumber);

        getBusInfo(new FirebaseCallback() {
            @Override
            public void onCallback(TextView textView2) {
                Point size = new Point();
                display.getSize(size);
                int height = size.y;

                textView.setHeight(height / 2);
                textView2.setPadding(0, height / 3, 0, 0);
                textView2.setHeight(height / 2);
            }
        });
        return view;
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
                String[] statuses = new String[map.size()];

                int count = 0;
                for (String key : ((TreeMap<String, String>) map).navigableKeySet()) {
                    buses[count] = key;
                    statuses[count] = map.get(key);
                    count++;
                }

                int index = -1;
                String status = "";

                try {
                    index = Arrays.asList(buses).indexOf(userBusNumber);
                    status = Arrays.asList(statuses).get(index);
                    TextView t = view.findViewById(R.id.user_bus);
                    t.setText(userBusNumber);
                    toolbar.setTitle(userBusNumber);
                    navigationView.getMenu().findItem(R.id.nav_user_bus).setTitle(userBusNumber);
                } catch (Exception e) {
                    List<String> error = Arrays.asList(buses);
                    for (int i = 0; i < error.size(); i++) {
                        if (error.get(i).contains(userBusNumber)) {
                            index = i;
                            TextView t = view.findViewById(R.id.user_bus);
                            t.setText(error.get(i));
                            toolbar.setTitle(error.get(i));
                            navigationView.getMenu().findItem(R.id.nav_user_bus).setTitle(error.get(i));
                            break;
                        }
                    }
                }

                status = Arrays.asList(statuses).get(index);
                TextView textView2 = (TextView) view.findViewById(R.id.bus_status);
                textView2.setText(status);

                if (status.equals("HERE")) {
                    textView2.setTextColor(Color.parseColor("#4285F4"));
                } else if (status.equals("LOADING")) {
                    textView2.setTextColor(Color.parseColor("#0F9D58"));
                } else if (status.equals("GONE")) {
                    textView2.setTextColor(Color.parseColor("#DB4437"));
                } else {
                    textView2.setTextColor(Color.parseColor("#000000"));
                }

                fireBaseCallback.onCallback(textView2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Error");
            }
        };

        myRef.addValueEventListener(valueEventListener);
    }

    private interface FirebaseCallback {
        void onCallback(TextView textView2);
    }
}
