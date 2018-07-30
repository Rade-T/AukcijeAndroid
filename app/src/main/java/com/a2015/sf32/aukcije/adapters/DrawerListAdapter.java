package com.a2015.sf32.aukcije.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.a2015.sf32.aukcije.R;
import com.a2015.sf32.aukcije.model.DrawerItem;

import java.util.ArrayList;

public class DrawerListAdapter extends BaseAdapter {
    Context context;
    ArrayList<DrawerItem> drawerItems;

    public DrawerListAdapter(Context context, ArrayList<DrawerItem> drawerItems) {
        this.context = context;
        this.drawerItems = drawerItems;
    }

    @Override
    public int getCount() {
        return drawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return drawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.drawer_list_item, null);
        }
        else {
            view = convertView;
        }

        TextView titleView = (TextView) view.findViewById(R.id.name);
        TextView subtitleView = (TextView) view.findViewById(R.id.description);

        titleView.setText( drawerItems.get(position).getName());
        subtitleView.setText( drawerItems.get(position).getDescription() );

        return view;
    }
}
