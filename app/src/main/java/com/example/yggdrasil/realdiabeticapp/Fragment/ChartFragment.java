package com.example.yggdrasil.realdiabeticapp.Fragment;

/**
 * Created by Yggdrasil on 12/1/2561.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.example.yggdrasil.realdiabeticapp.MainApplication;
import com.example.yggdrasil.realdiabeticapp.Models.GlucoseValue;
import com.example.yggdrasil.realdiabeticapp.NewLoginActivity;
import com.example.yggdrasil.realdiabeticapp.R;
import com.example.yggdrasil.realdiabeticapp.RealmManagement.RealmSearch;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.annotations.Index;

import static java.util.concurrent.TimeUnit.DAYS;

/**
 * Provides UI for the view with Cards.
 */
public class ChartFragment extends Fragment {

    private String email;
    private String title;
    private int page;
    private Realm realm;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    public static ChartFragment newInstance(int page, String title) {
        ChartFragment fragment = new ChartFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        email = getActivity().getSharedPreferences("EMAIL", Context.MODE_PRIVATE).getString("email", null);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mAuth == null)
                    startActivity( new Intent(getActivity(), NewLoginActivity.class));
            }
        };
        email = mAuth.getCurrentUser().getEmail();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        realm = Realm.getDefaultInstance();
        return inflater.inflate(R.layout.fragment_linechart, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        Log.d(getClass().getSimpleName(), email);
        LineChart lineChart = v.findViewById(R.id.linechart);
        RealmResults<GlucoseValue> data = realm.where(GlucoseValue.class)
                .equalTo("Email", email)
                .sort("Date", Sort.ASCENDING)
                .findAll();

        List<Entry> entries = new ArrayList<Entry>();
        Log.d(getClass().getSimpleName(), "size of data: " + data.size());
        if ( data.size() > 0) {
            String[] date, time;
            for ( int i = 0; i < data.size(); i++) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                //date = sdf.format(data.get(i).getDate()).split("-");
                time = data.get(i).getTime().split(":");
                if( i == 0){
                    entries.add( new Entry(
                            1 + (((Float.valueOf(time[0]) * 60) + (Float.valueOf(time[1]))) / 1440)
                            , Float.parseFloat(data.get(i).getValue()) ) );
                }
                else {
                    Float date_time =
                            datetimeToFloat( sdf.format(data.get(i).getDate())
                                    , data.get(i).getTime().split(":")
                                    , sdf.format(data.get(0).getDate()) );

                    entries.add(new Entry(date_time, Float.parseFloat(data.get(i).getValue())));
                }
            }
        }
        try {
            if (data.size() > 0){
                LineDataSet lineDataSet = new LineDataSet(entries, "Label");
                lineDataSet.setColor(R.style.HintText);
                lineDataSet.setValueTextColor(R.style.CounterText);
                lineDataSet.setHighlightEnabled(true);
                lineDataSet.setHighLightColor(Color.BLACK);
                LineData lineData = new LineData(lineDataSet);

                XAxis xAxis = lineChart.getXAxis();
                xAxis.setAxisMinimum(0f);
                xAxis.setGranularityEnabled(false);
                xAxis.setGranularity(1f);
                YAxis yAxis = lineChart.getAxisRight();
                yAxis.setGranularityEnabled(false);
                yAxis.setGranularity(1f);

                lineChart.setData(lineData);
                lineChart.setHighlightPerTapEnabled(false);
                LimitLine ll = new LimitLine(90f, "Hypoglycemia");
                ll.setLineColor(Color.RED);
                ll.setLineWidth(2f);
                yAxis.addLimitLine(ll);
                lineChart.notifyDataSetChanged();
                lineChart.invalidate();
            }
        } catch (Exception e){
            Log.e(getClass().getSimpleName(), "Errorrrrrrrrrrrrrrrrrrrrrrrr");
            e.printStackTrace();
        }
        if(!realm.isClosed())
            realm.close();
    }

    public Float datetimeToFloat(String dateStr
            , String[] timeStr
            , final String first_date){
        Log.e(getClass().getSimpleName(), "dateTimeToFloat called?");
        float diffDate = 0;
        Float diffTime, retTime;
        Date startDate, endDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try{
            startDate = dateFormat.parse(first_date);
            endDate = dateFormat.parse(dateStr);
            diffDate = DAYS.convert( (endDate.getTime() - startDate.getTime()), TimeUnit.MILLISECONDS) + 1;
        }catch  (ParseException e) {e.printStackTrace();}
        diffTime = ( Float.valueOf(timeStr[0]) * 60  + Float.valueOf(timeStr[1]) )/ 1440;
        retTime = Float.valueOf(new DecimalFormat("#.####").format(diffTime)) + diffDate;
        Toast.makeText(getActivity(), String.valueOf(diffTime), Toast.LENGTH_SHORT).show();
        return retTime;

    }

    @Override
    public void onResume() {
        super.onResume();
        realm = Realm.getDefaultInstance();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if ( !realm.isClosed())
            realm.close();
    }

    @Override
    public void onStart(){
        super.onStart();
    }
}
