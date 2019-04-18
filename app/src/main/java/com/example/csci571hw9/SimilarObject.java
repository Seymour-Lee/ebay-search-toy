package com.example.csci571hw9;

import android.util.Log;

import org.json.JSONObject;

public class SimilarObject{
    private static final String TAG = "SimilarObject";
    public String title;
    public String image;
    public String url;
    public String price;
    public String shipping;
    public String days;

    public SimilarObject(JSONObject object){
        try{
            title = object.getString("name");
            image = object.getString("image");
            url = object.getString("url");
            price = object.getString("price");
            shipping = object.getString("shipping");
            days = object.getString("days");
        }
        catch (Exception ex){
            Log.d(TAG, "SimilarObject: " + ex.toString());
        }
    }
}
