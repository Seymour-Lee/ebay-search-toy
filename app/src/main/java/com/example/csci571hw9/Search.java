package com.example.csci571hw9;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

// TODO:
// 1. Skip rows in details tab, which are missing
// 2. Star Icon in shipping tab


public class Search extends AppCompatActivity implements SearchTab1.OnFragmentInteractionListener, SearchTab2.OnFragmentInteractionListener{

    private static final String TAG = "Search";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Log.d(TAG, "hello debugger");

        try{
            String filename = "wishlist";
            File file = new File(filename);
            if(file.exists() == false) {
                FileOutputStream outputStream;
                outputStream = openFileOutput(filename, MODE_PRIVATE);
                outputStream.write("[]".getBytes());
                outputStream.close();
            }
        }
        catch (Exception e){
            Log.d(TAG, "onCreate: " + e.toString());
        }


        TabLayout tabLayout = (TabLayout)findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("Search"));
        tabLayout.addTab(tabLayout.newTab().setText("WISH LIST"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager)findViewById(R.id.pager);
        final SearchPageAdapter adapter = new SearchPageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if(tab.getPosition() == 1) updateWishlist();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    public void updateWishlist(){
        recyclerView = findViewById(R.id.wishlist_recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        TextView noResultsView = findViewById(R.id.wishlist_no_results);
        noResultsView.setVisibility(View.INVISIBLE);

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
            Log.d(TAG, "onResponse: read from internal storage: " + object.toString());
            inputStream.close();
            mAdapter = new ResultsRecyclerViewAdapter(object, this);
            recyclerView.setAdapter(mAdapter);
            if(object.length() == 0) noResultsView.setVisibility(View.VISIBLE);
            else noResultsView.setVisibility(View.INVISIBLE);
            TextView summaryItem = findViewById(R.id.wishlist_summary_item);
            TextView summaryPrice = findViewById(R.id.wishlist_summary_price);
            String itemInfo = "  Wishlist total(" + object.length() + " items):";
            summaryItem.setText(itemInfo);
            double price = 0.0;
            for(int i = 0; i < object.length(); i++){
                Log.d(TAG, "updateWishlist: " + object.getJSONObject(i).getString("price") + ", " + object.getJSONObject(i).getString("price").substring(1));
                String p = object.getJSONObject(i).getString("price").substring(1);
                price += Double.valueOf(p);
            }
            String priceInfo = "$" + String.format("%.2f", price) + "  ";
            summaryPrice.setText(priceInfo);




        }
        catch (Exception ex){
            Log.d(TAG, "onCreateView: " + ex.toString());
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: ");
        updateWishlist();
    }

    private JSONObject getParameters(){
        JSONObject params = new JSONObject();
        EditText keywordEdit = findViewById(R.id.search_tab1_keyword_input);
        Spinner categorySpinner = findViewById(R.id.search_tab1_category_spinner);
        CheckBox newCheckBox = findViewById(R.id.search_tab1_condition_new_checkbox);
        CheckBox usedCheckBox = findViewById(R.id.search_tab1_condition_used_checkbox);
        CheckBox unspecifiedCheckBox = findViewById(R.id.search_tab1_condition_unspecified_checkbox);
        CheckBox localCheckBox = findViewById(R.id.search_tab1_shipping_local_checkbox);
        CheckBox freeCheckBox = findViewById(R.id.search_tab1_shipping_free_checkbox);
        EditText milesEdit = findViewById(R.id.search_tab1_miles_input);
        RadioGroup fromGroup = findViewById(R.id.search_tab1_from_radio_group);
        EditText zipcodeEdit = findViewById(R.id.search_tab1_zipcode_input);
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);


        HashMap<String, String> category2param = new HashMap<>();
        category2param.put("All", "all");
        category2param.put("Art", "art");
        category2param.put("Baby", "baby");
        category2param.put("Books", "books");
        category2param.put("Clothing, Shoes & Accessories", "clothings");
        category2param.put("Computers, Tablets & networking", "computers");
        category2param.put("Health & Beauty", "health");
        category2param.put("Music", "music");
        category2param.put("Video Games & Consoles", "video");

        String postalCode = "90007";
        try{
            if(fromGroup.getCheckedRadioButtonId() == R.id.search_tab1_zipcode_radio){
                postalCode = zipcodeEdit.getText().toString();
            }
            else if(fromGroup.getCheckedRadioButtonId() ==R.id.search_tab1_local_radio){
                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                postalCode = addresses.get(0).getPostalCode();
            }
            Log.d(TAG, "getParameters: postalCode is " + postalCode);




        }
        catch (Exception ex){
            Log.d(TAG, "getParameters: exception invoked: " + ex.toString());
        }

        try{
            params.put("keyword", keywordEdit.getText().toString());
            params.put("category", category2param.get(categorySpinner.getSelectedItem().toString()));
            params.put("new", Boolean.toString(newCheckBox.isChecked()));
            params.put("used", Boolean.toString(usedCheckBox.isChecked()));
            params.put("unspecified", Boolean.toString(unspecifiedCheckBox.isChecked()));
            params.put("local", Boolean.toString(localCheckBox.isChecked()));
            params.put("free", Boolean.toString(freeCheckBox.isChecked()));
            params.put("distance", milesEdit.getText().toString().equals("")? "10": milesEdit.getText().toString());
            params.put("from", "location");
            params.put("zipcode", postalCode);
        }
        catch (Exception e){
            Log.d(TAG, "getParameters: " + e.toString());
        }

        return params;
    }

    public void SearchKeyword(View view){
        Log.d(TAG, "in searchKeyword()");

        if(CheckValidation() == false) return;

        Intent intent = new Intent(this, Results.class);
        JSONObject params = getParameters();
        intent.putExtra("search_param", params.toString());
        try{
            intent.putExtra("keyword", params.get("keyword").toString());
        }
        catch (Exception ex){
            Log.d(TAG, "searchKeyword: " + ex.toString());
        }

        startActivity(intent);
    }

    private boolean CheckValidation(){
        // TODO: validate keyword and zipcode textView. display error message if necessary
        Boolean isValid = true;
        TextView keywordError = findViewById(R.id.search_keyword_error);
        TextView zipcodeEmpty = findViewById(R.id.search_zipcode_empty_error);
        TextView zipcodeInvalid = findViewById(R.id.search_zipcode_invalid_error);
        TextView distanceEmpty = findViewById(R.id.search_distance_empty_error);
        TextView distanceInvalid = findViewById(R.id.search_distance_invalid_error);
        keywordError.setVisibility(View.GONE);
        zipcodeEmpty.setVisibility(View.GONE);
        zipcodeInvalid.setVisibility(View.GONE);
        distanceEmpty.setVisibility(View.GONE);
        distanceInvalid.setVisibility(View.GONE);
        EditText keywordEdit = findViewById(R.id.search_tab1_keyword_input);
        String keywordString = keywordEdit.getText().toString();
        if(keywordString.trim().equals("")){
            keywordError.setVisibility(View.VISIBLE);
            isValid = false;
        }
        CheckBox enableNearbyCheckbox = findViewById(R.id.search_tab1_nearby_checkbox);
        if(enableNearbyCheckbox.isChecked()){
//            EditText distanceEdit = findViewById(R.id.search_tab1_miles_input);
//            String distanceString = distanceEdit.getText().toString().trim();
//            if(distanceString.equals("")){
//                distanceEmpty.setVisibility(View.VISIBLE);
//                isValid = false;
//            }
//            else{
//                Pattern distancePattern = Pattern.compile("^[0-9]+$");
//                if(!distancePattern.matcher(distanceString).matches()){
//                    distanceInvalid.setVisibility(View.VISIBLE);
//                    isValid = false;
//                }
//            }

            // if input zipcode manually
            RadioGroup fromGroup = findViewById(R.id.search_tab1_from_radio_group);
            if(fromGroup.getCheckedRadioButtonId() == R.id.search_tab1_zipcode_radio){
                EditText zipcodeInput = findViewById(R.id.search_tab1_zipcode_input);
                String zipcodeString = zipcodeInput.getText().toString().trim();
                if(zipcodeString.equals("")){
                    zipcodeEmpty.setVisibility(View.VISIBLE);
                    isValid = false;
                }
                else{
                    Pattern zipcodePattern = Pattern.compile("^[0-9]{5}$");
                    if(!zipcodePattern.matcher(zipcodeString).matches()){
                        zipcodeInvalid.setVisibility(View.VISIBLE);
                        isValid = false;
                    }
                }
            }
        }

        return isValid;
    }

    public void InitSearchForm(){
        EditText keywordEdit = findViewById(R.id.search_tab1_keyword_input);
        EditText milesEdit = findViewById(R.id.search_tab1_miles_input);
        EditText zipcodeEdit = findViewById(R.id.search_tab1_zipcode_input);
        keywordEdit.setText("");
        milesEdit.setText("10");
        zipcodeEdit.setText("");
        CheckBox enableNearby = findViewById(R.id.search_tab1_nearby_checkbox);
        enableNearby.setChecked(false);
    }
}
