package com.example.yggdrasil.realdiabeticapp.Fragment;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.yggdrasil.realdiabeticapp.Constants;
import com.example.yggdrasil.realdiabeticapp.MainApplication;
import com.example.yggdrasil.realdiabeticapp.Models.GlucoseValue;
import com.example.yggdrasil.realdiabeticapp.Models.UserProfile;
import com.example.yggdrasil.realdiabeticapp.NewLoginActivity;
import com.example.yggdrasil.realdiabeticapp.R;
import com.example.yggdrasil.realdiabeticapp.RealmManagement.RealmInsert;
import com.example.yggdrasil.realdiabeticapp.RealmManagement.RealmSearch;
import com.example.yggdrasil.realdiabeticapp.RealmManagement.RealmUpdate;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class OverallFragment extends Fragment {

    private TextView fname, lname, lcheck_date, lcheck_time, lcheck_measured, lstatus_value
            , lstatus_status,hba1c, tip;

    private LayoutInflater mInflater;
    private ViewGroup mRootView;
    private String title;
    private int page;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private PieChart pieChart;
    private String TAG = "Overall Fragment";
    private String email;
    public static OverallFragment newInstance() {
        OverallFragment fragment = new OverallFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    public static OverallFragment newInstance(int page, String title) {
        OverallFragment fragment = new OverallFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");

        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(mAuth == null)
                    return;
                    //startActivity( new Intent( getActivity(), NewLoginActivity.class));
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_overall, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState){
        super.onViewCreated(v, savedInstanceState);
        email = mAuth.getCurrentUser().getEmail();
        pieChart = v.findViewById(R.id.piechart);
        Spinner spinner = v.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource( getActivity(), R.array.date_interval_array
        , R.layout.support_simple_spinner_dropdown_item);

        initialView(v);
        spinner.setAdapter(adapter);
        spinner.setSelection(2, true);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pieChart.removeAllViews();
                List<PieEntry> entries = new ArrayList<>();
                UpdatePie(entries, pieChart, parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        List<PieEntry> entries = new ArrayList<>();
        UpdatePie(entries, pieChart, spinner.getSelectedItem().toString());
        Log.d(TAG + ": spinner value" , spinner.getSelectedItem().toString());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

    }
    public void UpdatePie(List<PieEntry> entries, PieChart pieChart, String duration){

        RealmSearch realmSearch = new RealmSearch();
        Float[] arr_count;
        String[] label = {"Hyper", "High", "Normal", "Low", "Hypo"};
        try {
            arr_count = realmSearch.getPieResult( email, duration);
            for(int i = 0; i < arr_count.length; i++){
                if(arr_count[i] != 0f)
                    entries.add(new PieEntry(arr_count[i], label[i]));
                Log.d(TAG + "pie0" + i, String.valueOf(arr_count[i]));
            }
        } catch (Exception e){ Log.e(TAG, "Error occur when get Pie result");
            e.printStackTrace();
        }
        PieDataSet pieDataSet = new PieDataSet( entries, "Blood Glucose Range");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData pieData = new PieData( pieDataSet);
        pieChart.setData(pieData);

        /*pieChart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

            }
        });*/
    }

    @Override
    public void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onResume(){
        super.onResume();
        List<PieEntry> entries = new ArrayList<>();
        UpdatePie(entries, pieChart, "Month");
    }

    @Override
    public void onPause() {
        super.onPause();
        pieChart.removeAllViews();

    }

    public void initialView(View view){
        fname = view.findViewById(R.id.fragment_overall_name_value);
        lname = view.findViewById(R.id.fragment_overall_name_value2);
        lcheck_date = view.findViewById(R.id.fragment_overall_last_check_value);
        lcheck_time = view.findViewById(R.id.fragment_overall_last_check_value2);
        lcheck_measured = view.findViewById(R.id.fragment_overall_last_check_value3);
        lstatus_value = view.findViewById(R.id.fragment_overall_last_status_value);
        lstatus_status = view.findViewById(R.id.fragment_overall_last_status_value2);
        hba1c = view.findViewById(R.id.fragment_overall_hba1c_value);
        tip = view.findViewById(R.id.fragment_overall_tip_value);

        Log.d(TAG, "Prepare to initial View!");
        RealmSearch realmSearch = new RealmSearch();
        UserProfile userProfile = realmSearch.searchUserProfile( email);
        if ( userProfile != null) {
            Log.d(TAG, "Realm not null!");
            fname.setText(userProfile.getFName());
            lname.setText(userProfile.getLName());
        }
        else {
            Log.d(TAG, "Realm is null!");
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection(Constants.Collect_Profile)
                    .document(Constants.Document_User)
                        .collection(mAuth.getCurrentUser().getEmail().substring(0, 1) + "_Start")
                            .document(mAuth.getCurrentUser().getEmail())
                        .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult() ;
                                fname.setText(document.getString("First_Name") );
                                lname.setText(document.getString("Last_Name") );
                                Log.d(TAG, document.getString("Birthdate"));
                                Log.d(TAG, document.getString("First_Name"));
                                RealmUpdate realmUpdate = new RealmUpdate();
                                realmUpdate.upsertUserProfile(
                                        mAuth.getCurrentUser().getEmail(),
                                        document.getString("First_Name"),
                                        document.getString("Last_Name"),
                                        document.getString("Gender"),
                                        document.getString("Birthdate"),
                                        document.getString("Tel")
                                );
                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }

                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error getting document: ", e);
                        }
                    });

            Log.e(getClass().getSimpleName(), "Error: Profile is null");
        }
        GlucoseValue glucoseValue = realmSearch.searchLastestGlucose(email);
        if ( glucoseValue != null) {
            lcheck_date.setText(String.valueOf(
                    /*DateUtils.formatDateTime(getActivity(), glucoseValue.getDate().getTime(),
                            DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR) ));*/
                    new android.icu.text.SimpleDateFormat("yyyy-MM-dd").format(glucoseValue.getDate())
                    ) );
            lcheck_time.setText(glucoseValue.getTime());
            lcheck_measured.setText(glucoseValue.getMeasured());
            lstatus_value.setText(glucoseValue.getValue());
            lstatus_status.setText(glucoseValue.getStatus());
        }
    }
}
