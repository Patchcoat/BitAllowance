package com.bitallowance;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static android.widget.Toast.makeText;

public class RecyclerViewAdapter extends RecyclerView.Adapter {
    private static final String TAG = "BADDS-RecyclerViewAdapt";
    List<ListItem> _listItems;
    Context _context;
    CardType _type;

    public RecyclerViewAdapter(Context context, List<ListItem> listItems, CardType type) {
        this._context = context;
        this._listItems = listItems;
        this._type = type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_list_item, parent, false);
        //View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_simple_row_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        RecyclerView.ViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        Log.d(TAG, "onCreateViewHolder: RecycleView Created");
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        // set the data in items
        ImageView imageView = (ImageView)holder.itemView.findViewById(R.id.recyclerView_imgAvatar);
        TextView textViewDetails1 = (TextView)holder.itemView.findViewById(R.id.recyclerView_txtDetails1);
        TextView textViewDetails2 = (TextView)holder.itemView.findViewById(R.id.recyclerView_txtDetails2);
        TextView textViewName = (TextView)holder.itemView.findViewById(R.id.recyclerView_txtName);


        bindSimple(textViewName, position);
        if (_type != CardType.Simple )
            bindNormal(textViewDetails1, imageView, position);
        if (_type == CardType.Detailed)
            bindDetailed(textViewDetails2, position);


      //  textViewDetails2.setHeight(0);
        // implement setOnClickListener event on item view.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // display a toast with person name on item click
                Toast toast = makeText(_context, _listItems.get(position).getName(), Toast.LENGTH_SHORT);
            }
        });
    }

    private void bindSimple(TextView txtName, final int position){
        txtName.setText(_listItems.get(position).getName());
    }

    private void bindNormal(TextView txtDetails1, ImageView imgAvatar, final int position) {
        int dpMultiplier = (int) _context.getResources().getDisplayMetrics().density;
        txtDetails1.getLayoutParams().height = (20 * dpMultiplier);

        imgAvatar.getLayoutParams().height = (50 * dpMultiplier);
        imgAvatar.getLayoutParams().width = (50 * dpMultiplier);

        txtDetails1.setText(_listItems.get(position).getCardPrimaryDetails());
    }

     private void bindDetailed(TextView txtDetails2, final int position){
        int dpMultiplier = (int) _context.getResources().getDisplayMetrics().density;
        txtDetails2.getLayoutParams().height = (20 * dpMultiplier);
        txtDetails2.setText(_listItems.get(position).getCardSecondaryDetails());
    }



    @Override
    public int getItemCount() {
        return _listItems.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;// init the item view's
        public MyViewHolder(View itemView) {
            super(itemView);
            // get the reference of item view's
            name = (TextView) itemView.findViewById(R.id.recyclerView_txtName);
        }
    }

    public void setCardType(CardType type){
        _type = type;
    }

    public enum  CardType {
        Simple, Normal, Detailed
    }
}
