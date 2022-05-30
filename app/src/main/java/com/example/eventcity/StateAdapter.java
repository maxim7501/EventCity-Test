package com.example.eventcity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class StateAdapter extends ArrayAdapter<State> {
    private LayoutInflater inflater;
    private int layout;
    private List<State> states;
    private Context context;

    public StateAdapter(Context context, int resource, List<State> states) {
        super(context, resource, states);
        this.context = context;
        this.states = states;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null){
            convertView = inflater.inflate(this.layout, parent, false);
        }

        ImageView flagView = convertView.findViewById(R.id.list_image);
        TextView nameView = convertView.findViewById(R.id.list_name);
        TextView dateView = convertView.findViewById(R.id.list_date);
        TextView timeView = convertView.findViewById(R.id.list_time);


        State state = states.get(position);

        nameView.setText(state.getName());
        dateView.setText(state.getDate());
        timeView.setText(state.getTime());

        Picasso.with(context).load(state.getFlagResource().trim()).into(flagView);

        return convertView;
    }
}
