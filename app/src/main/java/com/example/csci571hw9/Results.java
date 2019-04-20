package com.example.csci571hw9;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class Results extends AppCompatActivity {

    private static final String TAG = "Results";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private JSONArray results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Log.d(TAG, "onCreate: results view started");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Search Results");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: TODO: write a function to go back");
                finish();
            }
        });

        recyclerView = findViewById(R.id.results_recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        ProgressBar progressBar = findViewById(R.id.results_progress_bar);
        TextView loadingText = findViewById(R.id.results_loading_textview);
        TextView summaryView = findViewById(R.id.results_summary);
        TextView noResultsView = findViewById(R.id.results_no_results);
        progressBar.setVisibility(View.VISIBLE);
        loadingText.setVisibility(View.VISIBLE);
        summaryView.setVisibility(View.GONE);
        noResultsView.setVisibility(View.INVISIBLE);


        String url = "";
        RequestQueue queue = Volley.newRequestQueue(this);
        try{

            String parameterString = getIntent().getStringExtra("search_param");
            Log.d(TAG, "onCreate: " + parameterString);
            JSONObject parameterObject = new JSONObject(parameterString);
            url = "http://10.0.2.2:8081/search_keyword"
                + "?keyword=" + parameterObject.getString("keyword")
                + "&categpry=" + parameterObject.getString("category")
                + "&new=" + parameterObject.getString("new")
                + "&used=" + parameterObject.getString("used")
                + "&unspecified=" + parameterObject.getString("unspecified")
                + "&local=" + parameterObject.getString("local")
                + "&free=" + parameterObject.getString("free")
                + "&distance=" + parameterObject.getString("distance")
                + "&from=" + parameterObject.getString("from")
                + "&zipcode=" + parameterObject.getString("zipcode");
            Log.d(TAG, "onCreate: url is " + url);
        }
        catch (Exception ex){
            Log.d(TAG, "onCreate: " + ex.toString());
        }

        JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                try{
                    Log.d(TAG, response.toString(10));
                    ProgressBar progressBar = findViewById(R.id.results_progress_bar);
                    TextView loadingText = findViewById(R.id.results_loading_textview);
                    TextView summaryView = findViewById(R.id.results_summary);
                    progressBar.setVisibility(View.GONE);
                    loadingText.setVisibility(View.GONE);
                    summaryView.setVisibility(View.VISIBLE);
                    String resultText = "Showing <font color='#f94b1f'>" + response.length() + "</font> results for <font color='#f94b1f'>" + getIntent().getStringExtra("keyword") + "</font>";
                    summaryView.setText(Html.fromHtml(resultText), TextView.BufferType.SPANNABLE);
                    TextView noResultsView = findViewById(R.id.results_no_results);
                    if(response.length() == 0) noResultsView.setVisibility(View.VISIBLE);
                    else noResultsView.setVisibility(View.INVISIBLE);
                    results = response;
                    mAdapter = new ResultsRecyclerViewAdapter(results, Results.this);
                    recyclerView.setAdapter(mAdapter);
                }
                catch (Exception ex){
                    Log.d(TAG, "onResponse: " + ex.toString());
                }

                Results.this.test();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString());
            }
        });

        queue.add(jsonObjReq);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: ");
        recyclerView = findViewById(R.id.results_recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new ResultsRecyclerViewAdapter(results, Results.this);
        recyclerView.setAdapter(mAdapter);
    }

    public void test(){
        Log.d(TAG, "test: called");

    }
}

