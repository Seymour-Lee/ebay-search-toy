package com.example.csci571hw9;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;

import java.util.ArrayList;

public class DetailsSimilarsRecyclerAdapter extends RecyclerView.Adapter<DetailsSimilarsRecyclerAdapter.ViewHolder>{
    private static final String TAG = "DetailsSimilarsAdapter";
    private ArrayList<SimilarObject> similars;
    private Context mContext;

    public DetailsSimilarsRecyclerAdapter(ArrayList<SimilarObject> _photos, Context mContext) {
        this.similars = _photos;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.similar_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        Log.d(TAG, "onBindViewHolder: called.");
        try{
            // Glide.with(mContext).asBitmap().load(similars.getString(i)).into(viewHolder.imageView);
            viewHolder.titleView.setText(similars.get(i).title);
            Glide.with(mContext).asBitmap().load(similars.get(i).image).into(viewHolder.imageView);
            viewHolder.shippingView.setText(similars.get(i).shipping);
            viewHolder.daysView.setText(similars.get(i).days + " Days Left");
            viewHolder.costView.setText(similars.get(i).price);
            viewHolder.cardView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    String postUrl = similars.get(i).url;
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(postUrl));
                    mContext.startActivity(browserIntent);
                }
            });
        }
        catch(Exception ex){
            Log.d(TAG, "onBindViewHolder: " + ex.toString());
        }
    }

    @Override
    public int getItemCount() {
        return similars.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public RelativeLayout parentLayout;
        public ImageView imageView;
        public TextView titleView;
        public TextView shippingView;
        public TextView daysView;
        public TextView costView;
        public CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.similar_parent_layout);
            imageView = itemView.findViewById(R.id.similar_image);
            titleView = itemView.findViewById(R.id.similar_title);
            shippingView = itemView.findViewById(R.id.similar_shipping);
            daysView = itemView.findViewById(R.id.similar_days);
            costView = itemView.findViewById(R.id.similar_cost);
            cardView = itemView.findViewById(R.id.similar_card_view);
        }
    }
}