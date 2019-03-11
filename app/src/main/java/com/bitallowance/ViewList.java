package com.bitallowance;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

/**
 * Entity Class
 * @author Dustin Christensen
 * @version 1.0
 * This class is used to populate the RECYCLEREVIEW, originally for the
 * RESERVEHOME, but can be used to populate any RECYCLERVIEW.
 * @since 02/25/2019
 */

public class ViewList extends RecyclerView.Adapter <ViewList.MyViewHolder> {
    private List data;
    private Context context;

    public ViewList(List data){
        this.data = data;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;
        public MyViewHolder(TextView v) {
            super(v);
            textView = v;
        }
    }

    @NonNull
    @Override
    public ViewList.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        TextView textView = (TextView) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_view, viewGroup, false);
        MyViewHolder viewHolder = new MyViewHolder(textView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewList.MyViewHolder myViewHolder, int i) {
        myViewHolder.textView.setText(data.get(i).toString());
        if (i % 3 == 0)
            myViewHolder.textView.setBackgroundColor(ContextCompat.getColor(context, R.color.Blue));
        else if ( i % 3 == 1)
            myViewHolder.textView.setBackgroundColor(ContextCompat.getColor(context, R.color.Green));
        else
            myViewHolder.textView.setBackgroundColor(ContextCompat.getColor(context, R.color.Red));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
