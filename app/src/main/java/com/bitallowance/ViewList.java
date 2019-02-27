package com.bitallowance;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.TextView;

import java.util.List;

public class ViewList extends RecyclerView.ViewList <ViewList.MyViewHolder> {

    // Variables
    private List listItems;
    private Context context;

    public ViewList(List listItems) {
        this.listItems = listItems;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public MyViewHolder(TextView tV) {
            super(tV);
            textView = tV;
        }
    }

    @NonNull
    @Override
    public ViewList.MyViewHolder onCreateViewHolder(@NonNull )

}
