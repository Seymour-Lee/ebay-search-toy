package com.example.csci571hw9;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
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
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

// TODO:
// 2. search form layout                    11
// 3. search form validation                12
// 6. result item css                       5
// 8. details layout                        10
// 11. store web using browser              7
// 12. similar item layout                  8
// 14. no result display                    14
//     layout置顶，没结果就设置visible，view设gone
//                否则gone，view设sisible




public class Search extends AppCompatActivity implements SearchTab1.OnFragmentInteractionListener, SearchTab2.OnFragmentInteractionListener{

    private static final String TAG = "Search";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Log.d(TAG, "hello debugger");

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
    private void updateWishlist(){
        recyclerView = findViewById(R.id.wishlist_recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

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

        try{
            params.put("keyword", keywordEdit.getText().toString());
            params.put("category", category2param.get(categorySpinner.getSelectedItem().toString()));
            params.put("new", Boolean.toString(newCheckBox.isChecked()));
            params.put("used", Boolean.toString(usedCheckBox.isChecked()));
            params.put("unspecified", Boolean.toString(unspecifiedCheckBox.isChecked()));
            params.put("local", Boolean.toString(localCheckBox.isChecked()));
            params.put("free", Boolean.toString(freeCheckBox.isChecked()));
            params.put("distance", milesEdit.getText().toString());
            params.put("from", "location");
            params.put("zipcode", "90007");


        }
        catch (Exception ex){
            Log.d(TAG, "getParameters: exception invoked: " + ex.toString());
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

        return true;
    }

    public void InitSearchForm(){

    }
}
