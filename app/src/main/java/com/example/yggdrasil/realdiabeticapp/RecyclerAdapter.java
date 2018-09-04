package com.example.yggdrasil.realdiabeticapp;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yggdrasil.realdiabeticapp.Models.GlucoseValue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yggdrasil on 18/4/2561.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private List<GlucoseValue> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView value, status, date, time, measured;
        public CardView cardView;
        public ImageView image;
        public ViewHolder(View v) {
            super(v);
            cardView = v.findViewById(R.id.cv);
            value = v.findViewById(R.id.bg_value);
            status = v.findViewById(R.id.status);
            date = v.findViewById(R.id.date);
            time = v.findViewById(R.id.time);
            measured = v.findViewById(R.id.measured);
            image = v.findViewById(R.id.imageView2);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerAdapter(List<GlucoseValue> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.cardView.setCardBackgroundColor( Color.parseColor("#FFF8F1"));
        holder.cardView.setElevation(2);
        holder.cardView.setCardElevation(8);
        holder.value.setText(mDataset.get(position).getValue() );
        holder.status.setText(mDataset.get(position).getStatus() );
        holder.measured.setText(mDataset.get(position).getMeasured());
        holder.date.setText( new SimpleDateFormat("yyyy-MM-dd")
                .format(mDataset.get(position).getDate()) );
        holder.time.setText(mDataset.get(position).getTime() );
        if ( mDataset.get(position).getStatus().equals("Hyperglycemia"))
            holder.image.setBackgroundColor(Color.RED);
        else if ( mDataset.get(position).getStatus().equals("High Blood Glucose"))
            holder.image.setBackgroundColor(Color.YELLOW);
        else if ( mDataset.get(position).getStatus().equals("Normal Blood Glucose"))
            holder.image.setBackgroundColor(Color.GREEN);
        else if ( mDataset.get(position).getStatus().equals("Low Blood Glucose"))
            holder.image.setBackgroundColor(Color.MAGENTA);
        else if ( mDataset.get(position).getStatus().equals("Hypoglycemia"))
            holder.image.setBackgroundColor(Color.BLUE);

        //holder.image.setBackgroundResource();

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}