package com.example.csci571hw9;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;

import net.steamcrafted.materialiconlib.MaterialIconView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ResultsRecyclerViewAdapter extends RecyclerView.Adapter<ResultsRecyclerViewAdapter.ViewHolder>{
    private static final String TAG = "ResultsRecyclerAdapter";

    private ArrayList<String> mNames;
    private JSONArray results;
    private Context mContext;

    private ImageLoader imageLoader;


    public ResultsRecyclerViewAdapter(JSONArray _results, Context mContext) {
        // this.mNames = mNames;
        this.results = _results;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.results_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        Log.d(TAG, "onBindViewHolder: called.");
        // viewHolder.textView.setText(mNames.get(i));
        try{
            viewHolder.titleView.setText(results.getJSONObject(i).getString("title"));
            Glide.with(mContext).asBitmap().load(results.getJSONObject(i).getString("image")).into(viewHolder.imageView);
            viewHolder.titleView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    try{
                        Log.d(TAG, "onClick: clicked on: " + results.getJSONObject(i).getString("title"));
                        Toast.makeText(mContext, results.getJSONObject(i).getString("title"), Toast.LENGTH_SHORT).show();

                        // invoke a new details view
                        Intent intent = new Intent(mContext, Details.class);
                        intent.putExtra("title", results.getJSONObject(i).getString("title"));
                        intent.putExtra("title_complete", results.getJSONObject(i).getString("tooltip"));
                        intent.putExtra("id", results.getJSONObject(i).getString("id"));
                        intent.putExtra("item_object", results.getJSONObject(i).toString());

                        if(mContext.getClass().getName().equals("com.example.csci571hw9.Search")){
                            Activity ac = (Search)mContext;
                            ac.startActivityForResult(intent, Activity.RESULT_OK);

                        }
                        else{
                            mContext.startActivity(intent);

                        }

                        Log.d(TAG, "onClick: back to wishlist or result page");
                    }
                    catch (Exception ex){
                        Log.d(TAG, "onClick: " + ex.toString());
                    }

                }
            });
            viewHolder.zipView.setText("Zip: " + results.getJSONObject(i).getString("zip"));
            viewHolder.shippingView.setText(results.getJSONObject(i).getString("shipping"));
//            public TextView conditionView;
            viewHolder.conditionView.setText(results.getJSONObject(i).getString("condition"));
            viewHolder.priceView.setText(results.getJSONObject(i).getString("price"));
            if(inCart(results.getJSONObject(i).getString("id"))){
                viewHolder.wishlistIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.cart_remove_orange));
            }
            else{
                viewHolder.wishlistIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.cart_plus_grey));
            }
            viewHolder.wishlistIcon.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view){
                    try{
                        JSONObject item = results.getJSONObject(i);
                        Log.d(TAG, "onClick: wishlist icon: " + item.getString("id"));
                        if(inCart(item.getString("id"))){
                            Log.d(TAG, "onClick: wishlist icon: in cart, remove");
                            removeFromCart(item.getString("id"));
                            Toast.makeText(mContext, results.getJSONObject(i).getString("title") + "was removed from wish list", Toast.LENGTH_SHORT).show();
                            viewHolder.wishlistIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.cart_plus_grey));
                        }
                        else{
                            Log.d(TAG, "onClick: wishlist icon: off cart, add");
                            addToCart(item);
                            Toast.makeText(mContext, results.getJSONObject(i).getString("title") + "was added to wish list", Toast.LENGTH_SHORT).show();
                            viewHolder.wishlistIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.cart_remove_orange));
                        }

                        // should update recycler view
                        // notifyDataSetChanged();
                        // Log.d(TAG, "onClick: current context = " + mContext.getClass() + ", " + mContext.getClass().getName().equals("com.example.csci571hw9.Results"));
                        if(mContext.getClass().getName().equals("com.example.csci571hw9.Search")){
                            Log.d(TAG, "onClick: remove recycler item and recalculate items and price");
                            // results.remove(i);
                            JSONArray newArray = new JSONArray();
                            for(int pos = 0; pos < results.length(); pos++){
                                if(pos == i) continue;;
                                newArray.put(results.getJSONObject(pos));
                            }
                            results = newArray;
                            notifyItemRemoved(i);
                            notifyItemRangeChanged(i, results.length());

                            TextView noResultsView = ((Search)mContext).findViewById(R.id.wishlist_no_results);
                            if(results.length() == 0) noResultsView.setVisibility(View.VISIBLE);
                            else noResultsView.setVisibility(View.INVISIBLE);

                            Activity ac = (Search)mContext;
                            TextView summaryItem = ac.findViewById(R.id.wishlist_summary_item);
                            TextView summaryPrice = ac.findViewById(R.id.wishlist_summary_price);
                            String itemInfo = "  Wishlist total(" + results.length() + " items):";
                            summaryItem.setText(itemInfo);
                            double price = 0.0;
                            for(int i = 0; i < results.length(); i++){
                                Log.d(TAG, "updateWishlist: " + results.getJSONObject(i).getString("price") + ", " + results.getJSONObject(i).getString("price").substring(1));
                                String p = results.getJSONObject(i).getString("price").substring(1);
                                price += Double.valueOf(p);
                            }
                            String priceInfo = "$" + String.format("%.2f", price) + "  ";
                            summaryPrice.setText(priceInfo);
                        }
                    }
                    catch (Exception ex){
                        Log.d(TAG, "onClick: " + ex.toString());
                    }

                }
            });

        }
        catch(Exception ex){
            Log.d(TAG, "onBindViewHolder: " + ex.toString());
        }
    }


    private boolean inCart(String itemId){
        try{
            String filename = "wishlist";
            FileInputStream inputStream = mContext.openFileInput(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }
            JSONArray object = new JSONArray(stringBuilder.toString());
            inputStream.close();

            Log.d(TAG, "inCart: itemId " + itemId);
            for(int i = 0; i < object.length(); i++){
                Log.d(TAG, "inCart: " + object.getJSONObject(i).getString("id"));
                if(object.getJSONObject(i).getString("id").equals(itemId)){
                    return true;
                }
            }
        }
        catch (Exception ex){
            Log.d(TAG, "inCart: " + ex.toString());
        }

        return false;
    }

    private void addToCart(JSONObject itemObject){
        try{
            String filename = "wishlist";
            FileInputStream inputStream = mContext.openFileInput(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }
            JSONArray objectArray = new JSONArray(stringBuilder.toString());
            inputStream.close();

            Log.d(TAG, "addToCart: " + itemObject.toString());
            objectArray.put(itemObject);

            String fileContents = objectArray.toString();
            FileOutputStream outputStream;
            outputStream = mContext.openFileOutput(filename, mContext.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
        }
        catch (Exception ex){
            Log.d(TAG, "inCart: " + ex.toString());
        }
    }

    private void removeFromCart(String itemId){
        try{
            String filename = "wishlist";
            FileInputStream inputStream = mContext.openFileInput(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }
            JSONArray object = new JSONArray(stringBuilder.toString());
            inputStream.close();

            JSONArray newArray = new JSONArray();
            Log.d(TAG, "inCart: itemId " + itemId);
            for(int i = 0; i < object.length(); i++){
                // Log.d(TAG, "inCart: " + object.getJSONObject(i).getString("id"));
                if(object.getJSONObject(i).getString("id").equals(itemId)) continue;
                newArray.put(object.getJSONObject(i));
            }

            String fileContents = newArray.toString();
            FileOutputStream outputStream;
            outputStream = mContext.openFileOutput(filename, mContext.MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
        }
        catch (Exception ex){
            Log.d(TAG, "inCart: " + ex.toString());
        }
    }

    @Override
    public int getItemCount() {
        return results.length();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView titleView;
        public RelativeLayout parentLayout;
        public ImageView imageView;
        public TextView zipView;
        public TextView shippingView;
        public TextView conditionView;
        public TextView priceView;
        public MaterialIconView wishlistIcon;
        public ViewHolder(View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.title);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            imageView = itemView.findViewById(R.id.image);
            zipView = itemView.findViewById(R.id.zip);
            shippingView = itemView.findViewById(R.id.shipping);
            conditionView = itemView.findViewById(R.id.condition);
            priceView = itemView.findViewById(R.id.price);
            wishlistIcon = itemView.findViewById(R.id.wishlist_icon);
        }
    }


}
