package com.a2015.sf32.aukcije.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.a2015.sf32.aukcije.R;
import com.a2015.sf32.aukcije.model.Bid;

import java.util.List;

public class BidAdapter extends ArrayAdapter {
    Context context;
    List<Bid> bids;

    public BidAdapter(Context context, List<Bid> bids) {
        super(context, 0, bids);
        this.context = context;
        this.bids = bids;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.bid_layout, null);
        }
        else {
            view = convertView;
        }

        TextView idView = (TextView) view.findViewById(R.id.bid_id);
        TextView priceView = (TextView) view.findViewById(R.id.bid_price);

        idView.setText("Ponuda broj " + String.valueOf(bids.get(position).getId()) );
        priceView.setText( String.valueOf(bids.get(position).getPrice()) );

        return view;
    }
}
