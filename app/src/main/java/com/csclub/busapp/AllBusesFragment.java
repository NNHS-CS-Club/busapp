package com.csclub.busapp;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class AllBusesFragment extends Fragment {

    private static int width;
    private static int height;

    private Toolbar toolbar;
    private String userBusNumber;
    private TableLayout table;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_buses, container, false);

        toolbar = getActivity().findViewById(R.id.toolbar);
        SharedPreferences localPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        userBusNumber = localPrefs.getString("userBusNumber", "");
        table = view.findViewById(R.id.table);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("/buses/");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int dataLength = (int) (dataSnapshot.getChildrenCount());

                String[] buses = new String[dataLength];
                String[] statuses = new String[dataLength];
                HashMap<String, ArrayList<String>> values = new HashMap<>();
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
                    ArrayList<String> value = new ArrayList<>();
                    value.add(postSnapshot.child("Status").getValue().toString());
                    value.add(postSnapshot.child("Change").getValue().toString());
                    values.put(bus, value);
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

                for (int i = 0; i < dataLength; i++) {
                    String busNumber = buses[i];
                    String change = values.get(busNumber).get(1);
                    statuses[i] = values.get(busNumber).get(0);
                    if (!(change.equals(""))) {
                        busNumber += "=" + change;
                    }
                    buses[i] = busNumber;
                }

                if (toolbar != null) {
                    toolbar.setTitle("All Buses");
                }

                try {
                    for (int i = 0; i < dataLength; i++) {
                        final TextView busTextView = getActivity().findViewById(i * 2);
                        busTextView.setText(buses[i]);

                        final TextView statusTextView = getActivity().findViewById(i * 2 + 1);
                        statusTextView.setText(statuses[i]);

                        setStatusColor(statusTextView, statuses[i]);
                    }
                } catch (Exception e){
                    table.removeAllViews();

                    TableRow header = new TableRow(getContext());
                    header.addView(makeTableCell("Bus", -1, userBusNumber));
                    header.addView(makeTableCell("Status", -2, userBusNumber));
                    table.addView(header);

                    for (int i = 0; i < buses.length; i++) {
                        TableRow tableRow = new TableRow(getContext());
                        tableRow.addView(makeTableCell(buses[i], i * 2, userBusNumber));
                        tableRow.addView(makeTableCell(statuses[i], i * 2 + 1, userBusNumber));
                        table.addView(tableRow);
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

    private TextView makeTableCell(String text, int id, String userBusNumber) {
        TextView textView = new TextView(getContext());
        textView.setId(id);
        if (text.equals(userBusNumber)) {
//            SpannableStringBuilder ssb = new SpannableStringBuilder("  " + userBusNumber);
//            ssb.setSpan(new ImageSpan(context, R.drawable.checkmark), 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//            textView.setText(ssb, TextView.BufferType.SPANNABLE);
            textView.setText(text);
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.checkmark, null);
            int left = (int) (width / 4 - drawable.getMinimumWidth() * 1.5);
            int right = (int) (left + drawable.getMinimumWidth());
            drawable.setBounds(left, 0, right,
                    drawable.getMinimumHeight());
            textView.setCompoundDrawables(drawable, null, null, null);
        } else {
            textView.setText(text);
        }
        textView.setTextSize(25);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        textView.setLayoutParams(new TableRow.LayoutParams(width / 2, height / 9));
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundResource(R.drawable.ic_table_border);

        setStatusColor(textView, text);

        return textView;
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