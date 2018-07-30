package com.a2015.sf32.aukcije.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.a2015.sf32.aukcije.R;
import com.a2015.sf32.aukcije.model.Item;

import java.util.List;

public class ItemAdapter extends ArrayAdapter {
    Context context;
    List<Item> items;

    public ItemAdapter(Context context, List<Item> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_layout, null);
        }
        else {
            view = convertView;
        }

        TextView nameView = (TextView) view.findViewById(R.id.item_name);
        TextView descriptionView = (TextView) view.findViewById(R.id.item_description);

        nameView.setText( items.get(position).getName());
        descriptionView.setText( items.get(position).getDescription() );

        return view;
    }
}
