package com.bitallowance;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bitallowance.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static android.widget.Toast.makeText;

public class RecyclerViewAdapter extends RecyclerView.Adapter {
    ArrayList personNames;
    Context context;
    public RecyclerViewAdapter(Context context, ArrayList personNames) {
        this.context = context;
        this.personNames = personNames;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_simple_row_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        RecyclerView.ViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        // set the data in items
        TextView textView = (TextView)holder.itemView.findViewById(R.id.recyclerView_txtName);
        textView.setText(personNames.get(position).toString());
        // implement setOnClickListener event on item view.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // display a toast with person name on item click
                Toast toast = makeText(context, personNames.get(position).toString(), Toast.LENGTH_SHORT);
            }
        });
    }
    @Override
    public int getItemCount() {
        return personNames.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;// init the item view's
        public MyViewHolder(View itemView) {
            super(itemView);
            // get the reference of item view's
            name = (TextView) itemView.findViewById(R.id.recyclerView_txtName);
        }
    }
}
