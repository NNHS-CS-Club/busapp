package com.csclub.busapp;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class AllBusesFragment extends Fragment {

    private TableLayout tbl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_buses, container, false);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("All Buses");

        tbl = (TableLayout) view.findViewById(R.id.table);

        String[] buses = Buses.getBuses();
        String[] statuses = Buses.getStatuses();

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        for (int i = 0; i < buses.length; i++) {
            TableRow newRow = new TableRow(this.getContext());

            TextView newText1 = new TextView(this.getContext());
            newText1.setText(buses[i]);
            newText1.setTextSize(22);
            newText1.setTextColor(Color.BLACK);
            newText1.setTypeface(newText1.getTypeface(), Typeface.BOLD);
            newText1.setLayoutParams(new TableRow.LayoutParams(width / 2, height / 11));
            newText1.setGravity(Gravity.CENTER);
            newText1.setBackgroundResource(R.drawable.ic_table_border);

            TextView newText2 = new TextView(this.getContext());
            newText2.setText(statuses[i]);
            newText2.setTextSize(22);
            newText2.setTypeface(newText2.getTypeface(), Typeface.BOLD);
            newText2.setBackgroundResource(R.drawable.ic_table_border);
            newText2.setLayoutParams(new TableRow.LayoutParams(width / 2, height / 11));
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

        return view;
    }


}
