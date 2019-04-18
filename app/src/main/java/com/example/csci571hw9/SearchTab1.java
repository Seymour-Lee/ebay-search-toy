package com.example.csci571hw9;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.AdapterView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchTab1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchTab1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchTab1 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private static final String TAG = "SearchTab1";

    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Handler handler;

    public SearchTab1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchTab1.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchTab1 newInstance(String param1, String param2) {
        SearchTab1 fragment = new SearchTab1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_tab1, container, false);
        final Context mContext = getActivity();
        final AppCompatAutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.search_tab1_zipcode_input);
        final SearchAutoCompleteAdapter searchAutoCompleteAdapter = new SearchAutoCompleteAdapter(mContext, android.R.layout.simple_dropdown_item_1line);

        autoCompleteTextView.setThreshold(2);
        autoCompleteTextView.setAdapter(searchAutoCompleteAdapter);
        autoCompleteTextView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        // selectedText.setText(searchAutoCompleteAdapter.getObject(position));
                        Log.d(TAG, "onItemClick: " + searchAutoCompleteAdapter.getObject(position));
                    }
                });

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(autoCompleteTextView.getText())) {
                        // makeApiCall(autoCompleteTextView.getText().toString());
                        // String zipcodePrefix = autoCompleteTextView.getText().toString();
                        String url = "http://10.0.2.2:8081/postalcode?code=" + autoCompleteTextView.getText().toString();
                        // url.replace(" ", "%20");
                        Log.d(TAG, "postalcode: url is " + url);
                        RequestQueue queue = Volley.newRequestQueue(mContext);
                        JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                            @Override
                            public void onResponse(JSONArray response) {
                                try{
                                    Log.d(TAG, response.toString(10));
                                    List<String> stringList = new ArrayList<>();
                                    for (int i = 0; i < response.length(); i++){
                                        String code = response.getString(i);
                                        stringList.add(code);
                                    }
                                    searchAutoCompleteAdapter.setData(stringList);
                                    searchAutoCompleteAdapter.notifyDataSetChanged();
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
                }
                return false;
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }




}
