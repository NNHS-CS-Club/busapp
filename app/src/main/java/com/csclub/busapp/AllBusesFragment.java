package com.csclub.busapp;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.preference.ListPreference;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class AllBusesFragment extends Fragment {

    private TableLayout tbl;
    private Display display;
    private DatabaseReference myRef;
    private boolean seen = false;
    private FragmentActivity fa;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_buses, container, false);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("/");

        display = getActivity().getWindowManager().getDefaultDisplay();

        fa = getActivity();

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("All Buses");

        tbl = (TableLayout) view.findViewById(R.id.table);

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

                if (!(seen)) {
                    Point size = new Point();
                    display.getSize(size);
                    int width = size.x;
                    int height = size.y;

                    for (int i = 0; i < buses.length; i++) {
                        TableRow newRow = new TableRow(getContext());

                        TextView newText1 = new TextView(getContext());
                        newText1.setText(buses[i]);
                        newText1.setId(i * 2);
                        newText1.setTextSize(25);
                        newText1.setTextColor(Color.BLACK);
                        newText1.setTypeface(newText1.getTypeface(), Typeface.BOLD);
                        newText1.setLayoutParams(new TableRow.LayoutParams(width / 2, height / 9));
                        newText1.setGravity(Gravity.CENTER);
                        newText1.setBackgroundResource(R.drawable.ic_table_border);

                        TextView newText2 = new TextView(getContext());
                        newText2.setId(i * 2 + 1);
                        newText2.setText(statuses[i]);
                        newText2.setTextSize(25);
                        newText2.setTypeface(newText2.getTypeface(), Typeface.BOLD);
                        newText2.setBackgroundResource(R.drawable.ic_table_border);
                        newText2.setLayoutParams(new TableRow.LayoutParams(width / 2, height / 9));
                        newText2.setGravity(Gravity.CENTER);

                        if (statuses[i].equals("HERE")) {
                            newText2.setTextColor(Color.parseColor("#4285F4"));
                        } else if (statuses[i].equals("LOADING")) {
                            newText2.setTextColor(Color.parseColor("#0F9D58"));
                        } else if (statuses[i].equals("GONE")) {
                            newText2.setTextColor(Color.parseColor("#DB4437"));
                        } else {
                            newText2.setTextColor(Color.parseColor("#000000"));
                        }


                        newRow.addView(newText1);
                        newRow.addView(newText2);
                        tbl.addView(newRow);
                    }
                    setInfo();
                } else {
                    for (int i = 0; i < buses.length; i++) {
                        final TextView t1 = fa.findViewById(i * 2);
                        String previous1 = (String) t1.getText();
                        t1.setText(buses[i]);

                        final TextView t2 = fa.findViewById(i * 2 + 1);
                        String previous2 = (String) t2.getText();
                        t2.setText(statuses[i]);

                        if (statuses[i].equals("HERE")) {
                            t2.setTextColor(Color.parseColor("#4285F4"));
                        } else if (statuses[i].equals("LOADING")) {
                            t2.setTextColor(Color.parseColor("#0F9D58"));
                        } else if (statuses[i].equals("GONE")) {
                            t2.setTextColor(Color.parseColor("#DB4437"));
                        } else {
                            t2.setTextColor(Color.parseColor("#000000"));
                        }

                        Runnable delayedTask = new Runnable() {
                            @Override
                            public void run() {
                                t1.setBackgroundResource(R.drawable.ic_table_border);
                            }
                        };

                        Runnable delayedTask2 = new Runnable() {
                            @Override
                            public void run() {
                                t2.setBackgroundResource(R.drawable.ic_table_border);
                            }
                        };

                        if (!(buses[i].equals(previous1))) {
                            ColorDrawable[] color1 = {new ColorDrawable(Color.parseColor("#FBFBFB")), new ColorDrawable(Color.parseColor("#909090"))};
                            TransitionDrawable trans1 = new TransitionDrawable(color1);
                            ColorDrawable[] color2 = {new ColorDrawable(Color.parseColor("#909090")), new ColorDrawable(Color.parseColor("#FBFBFB"))};
                            TransitionDrawable trans2 = new TransitionDrawable(color2);

                            trans1.startTransition(150);
                            t1.setBackground(trans1);
                            trans2.startTransition(150);
                            t1.setBackground(trans2);

                            getView().postDelayed(delayedTask, 300);
                        }

                        if (!(statuses[i].equals(previous2))) {
                            ColorDrawable[] color1 = {new ColorDrawable(Color.parseColor("#FBFBFB")), new ColorDrawable(Color.parseColor("#909090"))};
                            TransitionDrawable trans1 = new TransitionDrawable(color1);
                            ColorDrawable[] color2 = {new ColorDrawable(Color.parseColor("#909090")), new ColorDrawable(Color.parseColor("#FBFBFB"))};
                            TransitionDrawable trans2 = new TransitionDrawable(color2);

                            trans1.startTransition(150);
                            t2.setBackground(trans1);
                            trans2.startTransition(150);
                            t2.setBackground(trans2);

                            getView().postDelayed(delayedTask2, 300);
                        }
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

    public void setInfo() {
        this.seen = true;
    }
}
