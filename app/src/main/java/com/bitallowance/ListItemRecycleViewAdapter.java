package com.bitallowance;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import static android.widget.Toast.makeText;

/**
 * This class is a custom RecycleView Adapter made for displaying ListItems
 * NOTE - Parent activity should implement ListItemRecycleViewAdapter.ListItemClickListener if using
 * this adapter.
 */
public class ListItemRecycleViewAdapter extends RecyclerView.Adapter {
    private static final String TAG = "BADDS-ListItemRecycle";
    private List<ListItem> _listItems;
    private Context _context;
    private CardType _type;
    private ListItemClickListener _listItemClickListener;

    /**
     * Constructor for ListItemRecycleViewAdapter. Allows for the binding of ListItems to a recycleView
     * NOTE - Parent activity should implement ListItemClickListener if using this adapter.
     * @param context Context of parent activity. Usually (this)
     * @param listItemClickListener Interface used to pass onClick events back to parent. Usually (this)
     * @param listItems A list of ListItems to be displayed on the RecycleView (List<ListItem>)
     * @param type The amount of details that should be displayed on each card. (SIMPLE, NORMAL or DETAILED)
     */
    public ListItemRecycleViewAdapter(Context context, ListItemClickListener listItemClickListener,
                                      List<ListItem> listItems, CardType type) {
        this._context = context;
        this._listItems = listItems;
        this._type = type;
        this._listItemClickListener = listItemClickListener;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        // inflate the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_list_item, parent, false);
        //View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_simple_row_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        RecyclerView.ViewHolder vh = new ListItemViewHolder(v, _listItemClickListener, this); // pass the view to View Holder
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


        // Bind data depending on CardType
        bindSimple(textViewName, position);
        if (_type != CardType.Simple )
            bindNormal(textViewDetails1, imageView, position);
        if (_type == CardType.Detailed)
            bindDetailed(textViewDetails2, position);

        // Color change based on type
        if (_listItems.get(position).getType() == ListItemType.ENTITY) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(_context, R.color.LtYellow));
        }
        if (_listItems.get(position).getType() == ListItemType.REWARD) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(_context, R.color.LtBlue));
        }
        if (_listItems.get(position).getType() == ListItemType.TASK) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(_context, R.color.LtGreen));
        }
        if (_listItems.get(position).getType() == ListItemType.FINE) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(_context, R.color.LtRed));
        }

    }

    /**
     * Displays SIMPLE card which shows only a small image and a name.
     * This is called automatically
     * @param txtName
     * @param position
     */
    private void bindSimple(TextView txtName, final int position){
        txtName.setText(_listItems.get(position).getName());
    }

    /**
     * Enables NORMAL card by displaying and binding data to txtDetails1 label and increasing display
     * size of the ImageView
     * @param txtDetails1
     * @param position
     */
    private void bindNormal(TextView txtDetails1, ImageView imgAvatar, final int position) {
        int dpMultiplier = (int) _context.getResources().getDisplayMetrics().density;
        txtDetails1.getLayoutParams().height = (20 * dpMultiplier);

        imgAvatar.getLayoutParams().height = (50 * dpMultiplier);
        imgAvatar.getLayoutParams().width = (50 * dpMultiplier);

        txtDetails1.setText(_listItems.get(position).getCardPrimaryDetails());
    }

    /**
     * Enables DETAILED card by displaying and binding data to txtDetails2 label
     * @param txtDetails2
     * @param position
     */
     private void bindDetailed(TextView txtDetails2, final int position){
        int dpMultiplier = (int) _context.getResources().getDisplayMetrics().density;
        txtDetails2.getLayoutParams().height = (20 * dpMultiplier);
        txtDetails2.setText(_listItems.get(position).getCardSecondaryDetails());
    }

    /**
     * Returns size of list being displayed by recycler view
     * @return _listItems.size()
     */
    @Override
    public int getItemCount() {
        return _listItems.size();
    }

    /**
     * Custom ViewHolder for ListItemRecycleView
     */
    public class ListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ListItemClickListener onClickListener;
        private ListItemRecycleViewAdapter adapter;

        /**
         * Constructor for ListItemViewHolder
         * @param itemView Current View
         * @param onClickListener Interface linked to parent activity so onclick event can be returned.
         * @param adapter Parent adapter so that onclick events can be distinguished on activities with
         *                multiple ListItemRecycleViews
         */
        public ListItemViewHolder(View itemView, ListItemClickListener onClickListener, ListItemRecycleViewAdapter adapter) {
            super(itemView);
            this.onClickListener = onClickListener;
            this.adapter = adapter;
            itemView.setOnClickListener(this);
        }

        /**
         * Passes onclick event to parent Activity implementing onItemClickListener
         * @param v
         */
        @Override
        public void onClick(View v) {
            onClickListener.onRecyclerViewItemClick(getAdapterPosition(), adapter);
        }
    }

    /**
     * Specifies the amount of detail the ListItem-RecycleView should display.
     * Simple - Just Image & Name
     * Normal - Image, Name & Primary Details.
     * Detailed - Image, Name, Primary Details & Secondary Details.
     */
    public enum  CardType {
        Simple, Normal, Detailed
    }


}
