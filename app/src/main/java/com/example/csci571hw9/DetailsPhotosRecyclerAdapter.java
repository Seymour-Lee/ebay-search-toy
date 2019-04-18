package com.example.csci571hw9;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import org.json.JSONArray;


public class DetailsPhotosRecyclerAdapter extends RecyclerView.Adapter<DetailsPhotosRecyclerAdapter.ViewHolder>{
    private static final String TAG = "PhotosRecyclerAdapter";
    private JSONArray photos;
    private Context mContext;

    public DetailsPhotosRecyclerAdapter(JSONArray _photos, Context mContext) {
        this.photos = _photos;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.photo_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        Log.d(TAG, "onBindViewHolder: called.");
        try{
            Glide.with(mContext).asBitmap().load(photos.getString(i)).into(viewHolder.imageView);
        }
        catch(Exception ex){
            Log.d(TAG, "onBindViewHolder: " + ex.toString());
        }
    }

    @Override
    public int getItemCount() {
        return photos.length();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public RelativeLayout parentLayout;
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.photo_parent_layout);
            imageView = itemView.findViewById(R.id.photo_image);
        }
    }
}
