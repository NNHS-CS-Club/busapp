package com.csclub.busapp;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;

public class UserBusFragment extends Fragment {

    private TextView textView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_bus, container, false);

        textView = (TextView) view.findViewById(R.id.user_bus);
        SharedPreferences localPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String userBusNumber = localPrefs.getString("new_bus_number", "0");

        textView.setText(userBusNumber);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(userBusNumber);

        int index = Arrays.asList(Buses.getBuses()).indexOf(userBusNumber);
        String status = Arrays.asList(Buses.getStatuses()).get(index);
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

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;

        textView.setHeight(height / 2);
        textView2.setPadding(0, height / 3, 0, 0);
        textView2.setHeight(height / 2);

        return view;
    }
}
