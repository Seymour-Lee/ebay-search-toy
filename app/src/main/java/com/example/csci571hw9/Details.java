package com.example.csci571hw9;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telecom.Call;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.wssholmes.stark.circular_score.CircularScoreView;

import net.steamcrafted.materialiconlib.MaterialIconView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Details extends AppCompatActivity implements DetailsTabProduct.OnFragmentInteractionListener,
                                                          DetailsTabShipping.OnFragmentInteractionListener,
                                                          DetailsTabPhotos.OnFragmentInteractionListener,
                                                          DetailsTabSimilar.OnFragmentInteractionListener{
    private static final String TAG = "Details";

    private boolean cart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getIntent().getStringExtra("title"));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                Log.d(TAG, "onClick: TODO: write a function to go back");
                Intent data = new Intent();
                String text = "Result to be returned....";
                data.setData(Uri.parse(text));
                setResult(RESULT_OK, data);
                finish();
            }
        });

        final FloatingActionButton fab = findViewById(R.id.cart_fab);
        if(inCart()){
            fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.cart_remove_white));
        }
        else{
            fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.cart_plus_white));
        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, getIntent().getStringExtra("title"), Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                if(cart){
                    // remove from wishlist
                    fab.setImageDrawable(ContextCompat.getDrawable(Details.this, R.drawable.cart_plus_white));
                    cart = false;
                    removeFromCart();
                }
                else{
                    // add to wishlist
                    fab.setImageDrawable(ContextCompat.getDrawable(Details.this, R.drawable.cart_remove_white));
                    cart = true;
                    addToCart();
                }

            }
        });

        Log.d(TAG, "onCreate: title is: " + getIntent().getStringExtra("title"));

        final TabLayout tabLayout = (TabLayout)findViewById(R.id.details_tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("PRODUCT"));
        tabLayout.getTabAt(0).setIcon(R.drawable.information_variant_select);
        tabLayout.addTab(tabLayout.newTab().setText("SHIPPING"));
        tabLayout.getTabAt(1).setIcon(R.drawable.truck_variant_not);
        tabLayout.addTab(tabLayout.newTab().setText("PHOTOS"));
        tabLayout.getTabAt(2).setIcon(R.drawable.google_not);
        tabLayout.addTab(tabLayout.newTab().setText("SIMILAR"));
        tabLayout.getTabAt(3).setIcon(R.drawable.equal_not);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager)findViewById(R.id.details_pager);
        final DetailsPageAdapter adapter = new DetailsPageAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), this);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        // viewPager.setCurrentItem(2);



        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                Log.d(TAG, "onTabSelected: position is " + tab.getPosition());
                if(tab.getPosition() == 0){
                    tabLayout.getTabAt(0).setIcon(R.drawable.information_variant_select);
                    tabLayout.getTabAt(1).setIcon(R.drawable.truck_variant_not);
                    tabLayout.getTabAt(2).setIcon(R.drawable.google_not);
                    tabLayout.getTabAt(3).setIcon(R.drawable.equal_not);
                }
                else if(tab.getPosition() == 1){
                    tabLayout.getTabAt(0).setIcon(R.drawable.information_variant_not);
                    tabLayout.getTabAt(1).setIcon(R.drawable.truck_variant_select);
                    tabLayout.getTabAt(2).setIcon(R.drawable.google_not);
                    tabLayout.getTabAt(3).setIcon(R.drawable.equal_not);
                }
                else if(tab.getPosition() == 2){
                    tabLayout.getTabAt(0).setIcon(R.drawable.information_variant_not);
                    tabLayout.getTabAt(1).setIcon(R.drawable.truck_variant_not);
                    tabLayout.getTabAt(2).setIcon(R.drawable.google_select);
                    tabLayout.getTabAt(3).setIcon(R.drawable.equal_not);
                }
                else if(tab.getPosition() == 3){
                    tabLayout.getTabAt(0).setIcon(R.drawable.information_variant_not);
                    tabLayout.getTabAt(1).setIcon(R.drawable.truck_variant_not);
                    tabLayout.getTabAt(2).setIcon(R.drawable.google_not);
                    tabLayout.getTabAt(3).setIcon(R.drawable.equal_select);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



        // getItemDetails();
        // getItemPhotos();
        // getItemSimilars();



        MaterialIconView iconView = findViewById(R.id.facebook_icon);
        iconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: in facebook post");
                // https://www.facebook.com/sharer/sharer.php?u=https://www.ebay.com/itm/Apple-iPhone-6-Plus-64GB-Silver-Verizon-A1522-CDMA-GSM-/283449377622&quote=Buy%20Apple%20iPhone%206%20Plus%20-%2064GB%20-%20Silver%20(Verizon)%20A1522%20(CDMA%20+%20GSM)%20at%20$$199.99%20from%20LINK%20below
                try {
                    String postUrl = "https://www.facebook.com/sharer/sharer.php"
                            + "?u=" + info.getJSONObject("product").getString("natureserchurl")
                            + "&quote=Buy%20" + info.getJSONObject("product").getString("title")
                            + "%20at%20" + info.getJSONObject("product").getString("price")
                            + "%20from%20LINK%20below"
                            + "&hashtag=%23CSCI571Spring2019Ebay";
                            // + "&quote=Buy%20Apple%20iPhone%206%20Plus%20-%2064GB%20-%20Silver%20(Verizon)%20A1522%20(CDMA%20+%20GSM)%20at%20$$199.99%20from%20LINK%20below";
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(postUrl));
                    startActivity(browserIntent);
                }
                catch (Exception ex){
                    Log.d(TAG, "onClick: " + ex.toString());
                }

            }
        });
    }

    private JSONObject info;
    public void setProductTabContent(){
        try{
            // set product tab
            JSONObject product = info.getJSONObject("product");
            JSONObject shipping = info.getJSONObject("shipping");
            LinearLayout mGallery = findViewById(R.id.image_gallery_layout);
            mGallery.removeAllViews();
            LayoutInflater mInflater = LayoutInflater.from(Details.this);;
            JSONArray images = product.getJSONArray("images");
            for(int i = 0; i < images.length(); i++){
                View view = mInflater.inflate(R.layout.gallery_item,
                        mGallery, false);

                ImageView imageView = view.findViewById(R.id.gallery_image);
                Glide.with(Details.this).asBitmap().load(images.getString(i)).into(imageView);
                mGallery.addView(view);
            }
            TextView titleView = findViewById(R.id.product_title);
            TextView priceandshippingView = findViewById(R.id.product_price_shipping);
            TextView subtitleView = findViewById(R.id.product_subtitle);
            TextView priceView = findViewById(R.id.product_price);
            TextView brandView = findViewById(R.id.product_brand);
            titleView.setText(product.getString("title"));
            String shippingString = shipping.getString("cost").equals("Free Shipping")? "Free Shipping": shipping.getString("cost") + " Shipping";
            String priceString = "<font color='#6A13EC'><b>" + product.getString("price") + "</b></font>" + " With " + shippingString;
            priceandshippingView.setText(Html.fromHtml(priceString), TextView.BufferType.SPANNABLE);
            subtitleView.setText(product.getString("subtitle"));
            priceView.setText(product.getString("price"));
            JSONArray specifics = product.getJSONArray("specifics");
            String brandString = "";
            ArrayList<String> specificsString = new ArrayList<>();
            for(int i = 0; i < specifics.length(); i++){
                if(specifics.getJSONObject(i).getString("Name").equals("Brand")){
                    brandString = specifics.getJSONObject(i).getJSONArray("Value").getString(0);
                    specificsString.add(0, brandString);
                }
                else specificsString.add(specifics.getJSONObject(i).getJSONArray("Value").getString(0));
            }
            brandView.setText(brandString);
            TableLayout specificsTableView = findViewById(R.id.product_specifies);
            for(int i = 0; i < specificsString.size(); i++){
                TableRow row = new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);
                TextView rowText = new TextView(this);
                rowText.setText("\u2022 " + specificsString.get(i));
                row.addView(rowText);
                specificsTableView.addView(row);
            }

            // set shipping tab
            final JSONObject seller = info.getJSONObject("seller");
            TextView storeNameView = findViewById(R.id.shipping_store_name);
            TextView feedbackScoreView = findViewById(R.id.shipping_feedback_score);
            CircularScoreView popularityView = findViewById(R.id.shipping_popularity);
            TextView feedbackStarView = findViewById(R.id.shipping_feedback_star);
            TextView shippingCostView = findViewById(R.id.shipping_cost);
            TextView globalShippingView = findViewById(R.id.shipping_global);
            TextView handlingTimeView = findViewById(R.id.shipping_handling_time);
            TextView conditionView = findViewById(R.id.shipping_condition);
            TextView policyView = findViewById(R.id.shipping_policy);
            TextView returnWithinView = findViewById(R.id.shipping_return);
            TextView refundView = findViewById(R.id.shipping_refund);
            TextView shippedByView = findViewById(R.id.shipping_shipped);
            SpannableString content = new SpannableString(seller.getString("store"));
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            storeNameView.setText(content);
            storeNameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: in store name onclick");
                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(seller.getString("at")));
                        startActivity(browserIntent);
                    }
                    catch (Exception ex){
                        Log.d(TAG, "onClick: " + ex.toString());
                    }
                }
            });
            feedbackScoreView.setText(seller.getString("feedback"));
            // popularityView.setScore(Integer.valueOf(seller.getString("popularity")));
            popularityView.setScore((int)Double.parseDouble(seller.getString("popularity")));
            feedbackStarView.setText(seller.getString("rating"));
            shippingCostView.setText(shipping.getString("cost"));
            globalShippingView.setText(product.getString("golablshipping").equals("true")? "Yes": "No");
            handlingTimeView.setText(shipping.getString("handle_time"));
            conditionView.setText(product.getString("conditiondescription"));
            policyView.setText(product.getString("returnaccepted"));
            returnWithinView.setText(product.getString("returnwithin"));
            refundView.setText(product.getString("refund"));
            shippedByView.setText(product.getString("shippedby"));

        }
        catch (Exception ex){
            Log.d(TAG, "setProductTabContent: " + ex.toString());
        }

    }
    public void getItemDetails(){
        String url = "http://10.0.2.2:8081/search_item?id=" + getIntent().getStringExtra("id");
        // url.replace(" ", "%20");
        Log.d(TAG, "getItemDetails: url is " + url);
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try{
                    Log.d(TAG, response.getJSONObject("product").toString(4));
                    Log.d(TAG, response.getJSONObject("shipping").toString(4));
                    Log.d(TAG, response.getJSONObject("seller").toString(4));
                    info = response;
                    setProductTabContent();
                }
                catch (Exception ex){
                    Log.d(TAG, "onResponse: " + ex.toString());
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // textView.setText(error.getMessage());
                Log.d(TAG, error.toString());

                // show no result info
            }
        });

        queue.add(jsonObjReq);
    }

    private JSONArray photos;
    private RecyclerView photosRecycler;
    private RecyclerView.Adapter photosAdapter;
    private RecyclerView.LayoutManager photosLayoutManager;
    private void setPhotosTabContent(){
        photosRecycler = findViewById(R.id.photos_recycler);
        photosRecycler.setHasFixedSize(true);
        photosLayoutManager = new LinearLayoutManager(Details.this);
        photosRecycler.setLayoutManager(photosLayoutManager);
        photosAdapter = new DetailsPhotosRecyclerAdapter(photos, Details.this);
        photosRecycler.setAdapter(photosAdapter);
    }

    public void getItemPhotos(){
        String url = "http://10.0.2.2:8081/search_photos?title=" + getIntent().getStringExtra("title_complete");
        Log.d(TAG, "getItemPhotos: url is " + url);
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                try{
                    Log.d(TAG, response.toString(10));
                    photos = response;
                    TextView noResultsView = findViewById(R.id.photos_no_results);
                    if(response.length() == 0) noResultsView.setVisibility(View.VISIBLE);
                    else noResultsView.setVisibility(View.INVISIBLE);
                    setPhotosTabContent();
                }
                catch (Exception ex){
                    Log.d(TAG, "onResponse: " + ex.toString());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // textView.setText(error.getMessage());
                Log.d(TAG, error.toString());

                // show no result info
            }
        });

        queue.add(jsonObjReq);
    }



//    private JSONArray similars;
//    private JSONArray currentDisplay;
    private ArrayList<SimilarObject> similars;
    private ArrayList<SimilarObject> currentDisplay;
    private RecyclerView similarsRecycler;
    private RecyclerView.Adapter similarsAdapter;
    private RecyclerView.LayoutManager similarsLayoutManager;
    private void setSimilarsTabContent(){
        currentDisplay = similars;
        similarsRecycler = findViewById(R.id.similars_recycler);
        similarsRecycler.setHasFixedSize(true);
        similarsLayoutManager = new LinearLayoutManager(Details.this);
        similarsRecycler.setLayoutManager(similarsLayoutManager);
        similarsAdapter = new DetailsSimilarsRecyclerAdapter(currentDisplay, Details.this);
        similarsRecycler.setAdapter(similarsAdapter);

        final Spinner keySpinner = findViewById(R.id.similar_sort_key_spinner);
        final Spinner orderSpinner = findViewById(R.id.similar_sort_order_spinner);
        orderSpinner.setEnabled(false);
        keySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int order = (int)orderSpinner.getSelectedItemId();
                switch (position){
                    case 0:
                        currentDisplay = new ArrayList<>(similars);
                        orderSpinner.setEnabled(false);
                        // orderSpinner.setSelection(0);
                        break;
                    case 1:
                        orderSpinner.setEnabled(true);
                        if(order == 0){
                            Collections.sort(currentDisplay, new Comparator<SimilarObject>() {
                                @Override
                                public int compare(SimilarObject o1, SimilarObject o2) {
                                    return o1.title.compareTo(o2.title);
                                }
                            });
                        }
                        else if(order == 1){
                            Collections.sort(currentDisplay, new Comparator<SimilarObject>() {
                                @Override
                                public int compare(SimilarObject o1, SimilarObject o2) {
                                    return o2.title.compareTo(o1.title);
                                }
                            });
                        }
                        break;
                    case 2:
                        orderSpinner.setEnabled(true);
                        if(order == 0){
                            Collections.sort(currentDisplay, new Comparator<SimilarObject>() {
                                @Override
                                public int compare(SimilarObject o1, SimilarObject o2) {
                                    double price1 = Double.valueOf(o1.price.substring(1));
                                    double price2 = Double.valueOf(o2.price.substring(1));
                                    return Double.compare(price1, price2);
//                                    if(price1 < price2) return 1;
//                                    if(price1 > price2) return -1;
//                                    return 0;
                                }
                            });
                        }
                        else if(order == 1){
                            Collections.sort(currentDisplay, new Comparator<SimilarObject>() {
                                @Override
                                public int compare(SimilarObject o1, SimilarObject o2) {
                                    double price1 = Double.valueOf(o1.price.substring(1));
                                    double price2 = Double.valueOf(o2.price.substring(1));
                                    return Double.compare(price2, price1);
//                                    if(price1 < price2) return 1;
//                                    if(price1 > price2) return -1;
//                                    return 0;
                                }
                            });
                        }
                        break;
                    case 3:
                        orderSpinner.setEnabled(true);
                        if(order == 0){
                            Collections.sort(currentDisplay, new Comparator<SimilarObject>() {
                                @Override
                                public int compare(SimilarObject o1, SimilarObject o2) {
                                    int day1 = Integer.valueOf(o1.days);
                                    int day2 = Integer.valueOf(o2.days);
                                    if(day2 < day1) return 1;
                                    if(day2 > day1) return -1;
                                    return 0;
                                }
                            });
                        }
                        else if(order == 1){
                            Collections.sort(currentDisplay, new Comparator<SimilarObject>() {
                                @Override
                                public int compare(SimilarObject o1, SimilarObject o2) {
                                    int day1 = Integer.valueOf(o1.days);
                                    int day2 = Integer.valueOf(o2.days);
                                    if(day1 < day2) return 1;
                                    if(day1 > day2) return -1;
                                    return 0;
                                }
                            });
                        }
                        break;
                }
                similarsAdapter = new DetailsSimilarsRecyclerAdapter(currentDisplay, Details.this);
                similarsRecycler.setAdapter(similarsAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        orderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(keySpinner.getSelectedItemId() == 0){
                    return;
                }
                int key = (int)keySpinner.getSelectedItemId();
                switch (position){
                    case 0:
                        // ascending
                        if(key == 1){
                            Collections.sort(currentDisplay, new Comparator<SimilarObject>() {
                                @Override
                                public int compare(SimilarObject o1, SimilarObject o2) {
                                    return o1.title.compareTo(o2.title);
                                }
                            });
                        }
                        else if(key == 2){
                            Collections.sort(currentDisplay, new Comparator<SimilarObject>() {
                                @Override
                                public int compare(SimilarObject o1, SimilarObject o2) {
                                    double price1 = Double.valueOf(o1.price.substring(1));
                                    double price2 = Double.valueOf(o2.price.substring(1));
                                    return Double.compare(price1, price2);
//                                    if(price1 < price2) return 1;
//                                    if(price1 > price2) return -1;
//                                    return 0;
                                }
                            });
                        }
                        else if(key == 3){
                            Collections.sort(currentDisplay, new Comparator<SimilarObject>() {
                                @Override
                                public int compare(SimilarObject o1, SimilarObject o2) {
                                    int day1 = Integer.valueOf(o1.days);
                                    int day2 = Integer.valueOf(o2.days);
                                    if(day2 < day1) return 1;
                                    if(day2 > day1) return -1;
                                    return 0;
                                }
                            });
                        }
                        break;
                    case 1:
                        //descending
                        if(key == 1){
                            Collections.sort(currentDisplay, new Comparator<SimilarObject>() {
                                @Override
                                public int compare(SimilarObject o1, SimilarObject o2) {
                                    return o2.title.compareTo(o1.title);
                                }
                            });
                        }
                        else if(key == 2){
                            Collections.sort(currentDisplay, new Comparator<SimilarObject>() {
                                @Override
                                public int compare(SimilarObject o1, SimilarObject o2) {
                                    double price1 = Double.valueOf(o1.price.substring(1));
                                    double price2 = Double.valueOf(o2.price.substring(1));
                                    return Double.compare(price2, price1);
//                                    if(price1 < price2) return 1;
//                                    if(price1 > price2) return -1;
//                                    return 0;
                                }
                            });
                        }
                        else if(key == 3){
                            Collections.sort(currentDisplay, new Comparator<SimilarObject>() {
                                @Override
                                public int compare(SimilarObject o1, SimilarObject o2) {
                                    int day1 = Integer.valueOf(o1.days);
                                    int day2 = Integer.valueOf(o2.days);
                                    if(day1 < day2) return 1;
                                    if(day1 > day2) return -1;
                                    return 0;
                                }
                            });
                        }
                        break;
                }
                similarsAdapter = new DetailsSimilarsRecyclerAdapter(currentDisplay, Details.this);
                similarsRecycler.setAdapter(similarsAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void getItemSimilars(){
        String url = "http://10.0.2.2:8081/search_similar?id=" + getIntent().getStringExtra("id");
        // url.replace(" ", "%20");
        Log.d(TAG, "getItemSimilars: url is " + url);
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                try{
                    Log.d(TAG, response.toString(10));
//                    similars = response;
                    similars = new ArrayList<>();
                    for(int i = 0; i < response.length(); i++){
                        similars.add(new SimilarObject(response.getJSONObject(i)));
                    }
                    currentDisplay = new ArrayList<>(similars);
                    TextView noResultsView = findViewById(R.id.similar_no_results);
                    if(currentDisplay.size() == 0) noResultsView.setVisibility(View.VISIBLE);
                    else noResultsView.setVisibility(View.INVISIBLE);
                    setSimilarsTabContent();
                }
                catch (Exception ex){
                    Log.d(TAG, "onResponse: " + ex.toString());
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // textView.setText(error.getMessage());
                Log.d(TAG, error.toString());

                // show no result info
            }
        });

        queue.add(jsonObjReq);
    }

    private boolean inCart(){
        try{
            String filename = "wishlist";
            FileInputStream inputStream = openFileInput(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }
            JSONArray object = new JSONArray(stringBuilder.toString());
            inputStream.close();

            String itemId = getIntent().getStringExtra("id");
            Log.d(TAG, "inCart: itemId " + itemId);
            for(int i = 0; i < object.length(); i++){
                Log.d(TAG, "inCart: " + object.getJSONObject(i).getString("id"));
                if(object.getJSONObject(i).getString("id").equals(itemId)){
                    cart = true;
                    break;
                }
            }
        }
        catch (Exception ex){
            Log.d(TAG, "inCart: " + ex.toString());
        }

        return cart;
    }

    private void addToCart(){
        try{
            String filename = "wishlist";
            FileInputStream inputStream = openFileInput(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }
            JSONArray objectArray = new JSONArray(stringBuilder.toString());
            inputStream.close();

            JSONObject itemObject = new JSONObject(getIntent().getStringExtra("item_object"));
            Log.d(TAG, "addToCart: " + itemObject.toString());
            objectArray.put(itemObject);

            String fileContents = objectArray.toString();
            FileOutputStream outputStream;
            outputStream = openFileOutput(filename, MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
        }
        catch (Exception ex){
            Log.d(TAG, "inCart: " + ex.toString());
        }
    }

    private void removeFromCart(){
        try{
            String filename = "wishlist";
            FileInputStream inputStream = openFileInput(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }
            JSONArray object = new JSONArray(stringBuilder.toString());
            inputStream.close();

            String itemId = getIntent().getStringExtra("id");
            JSONArray newArray = new JSONArray();
            Log.d(TAG, "inCart: itemId " + itemId);
            for(int i = 0; i < object.length(); i++){
                // Log.d(TAG, "inCart: " + object.getJSONObject(i).getString("id"));
                if(object.getJSONObject(i).getString("id").equals(itemId)) continue;
                newArray.put(object.getJSONObject(i));
            }

            String fileContents = newArray.toString();
            FileOutputStream outputStream;
            outputStream = openFileOutput(filename, MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
        }
        catch (Exception ex){
            Log.d(TAG, "inCart: " + ex.toString());
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
