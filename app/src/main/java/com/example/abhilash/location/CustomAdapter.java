package com.example.abhilash.location;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Abhilash on 1/30/2016.
 */
public class CustomAdapter extends ArrayAdapter<Loc> {
    private ArrayList<Loc> locs;
    DecimalFormat f = new DecimalFormat("#.#");


    public CustomAdapter(Context context, ArrayList<Loc> locs) {
        super( context, R.layout.list_layout,  locs);
        this.locs = locs;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater myInflater = LayoutInflater.from(getContext());
        View customView = myInflater.inflate(R.layout.list_layout, null);


        Loc loc = locs.get(position);
        TextView locationText = (TextView) customView.findViewById(R.id.locationText);

        double dist = (loc.getDistance());

        locationText.setText( " : " + f.format(dist) + "k.m." + " away" );

        TextView locationName = (TextView) customView.findViewById(R.id.locationName);

        locationName.setText(loc.getName());
        return customView;


    }
}

