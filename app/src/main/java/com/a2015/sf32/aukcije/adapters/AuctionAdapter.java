package com.a2015.sf32.aukcije.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.a2015.sf32.aukcije.R;
import com.a2015.sf32.aukcije.model.Auction;

import java.util.List;

public class AuctionAdapter extends ArrayAdapter {
    Context context;
    List<Auction> auctions;

    public AuctionAdapter(Context context, List<Auction> auctions) {
        super(context, 0, auctions);
        this.context = context;
        this.auctions = auctions;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.auction_layout, null);
        }
        else {
            view = convertView;
        }

        TextView idView = (TextView) view.findViewById(R.id.auction_id);
        TextView priceView = (TextView) view.findViewById(R.id.auction_price);

        idView.setText("Aukcija broj " + String.valueOf( auctions.get(position).getId() ) );
        priceView.setText("Pocetna cena " + String.valueOf( auctions.get(position).getStartPrice() ) );

        return view;
    }
}
