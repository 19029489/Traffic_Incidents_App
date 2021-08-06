package com.example.trafficincidentsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class IncidentAdapter extends ArrayAdapter<Incident> {

    private ArrayList<Incident> incident;
    private Context context;

    private TextView tvType, tvMessage;

    public IncidentAdapter(Context context, int resource, ArrayList<Incident> objects){
        super(context, resource, objects);
        incident = objects;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row, parent, false);

        tvType = (TextView) rowView.findViewById(R.id.tvType);
        tvMessage = (TextView) rowView.findViewById(R.id.tvMessage);

        Incident currentIncident = incident.get(position);

        tvType.setText(currentIncident.getType());

        tvMessage.setText(currentIncident.getMessage());

        return rowView;
    }

}