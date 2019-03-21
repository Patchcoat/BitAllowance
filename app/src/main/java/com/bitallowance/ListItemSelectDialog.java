package com.bitallowance;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class ListItemSelectDialog extends DialogFragment {

    private static final String TAG = "BADDS-ListItemDialog";
    private List<String> _options = new ArrayList<>();
    private ListItemClickListener _listener;
    private NestedListItemClickListener _nestedListener;
    private ListItem _selectedItem;
    private ListItemType _optionType = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
       // List<String> _options = new ArrayList<>();

        // Set the alert dialog title
        if (getArguments().getString("TITLE") != null)
            builder.setTitle(getArguments().getString("TITLE"));
        else
            builder.setTitle("Select An Option?");

        if (getArguments().getStringArrayList("OPTIONS") != null)
            _options.addAll(getArguments().getStringArrayList("OPTIONS"));
        else
            _options.add("CANCEL");



        // Initialize a new list of _options
        //final List<String> _options = new ArrayList<String>(Arrays.asList("Edit...", "Pay...", "Fine...", "Apply Reward", "Delete"));

        // Initialize a new array adapter instance
        final ArrayAdapter arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_selectable_list_item, _options) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Cast list view each item as text view
                TextView text_view = (TextView) super.getView(position, convertView, parent);

                text_view.getLayoutParams().height = (int) getActivity().getResources().getDisplayMetrics().density * 30;


                GradientDrawable gd = new GradientDrawable();

                //How to add a border
                gd.setStroke(1, 0xFF000000);

                // How to set the background color
                // gd.setColor(0xFF00FF00); // Changes this drawable to use a single color instead of a gradient
                //How to apply rounded corners
                //gd.setCornerRadius(5);


                text_view.setBackground(gd);

                //How to set font
                //text_view.setTypeface(_typeFace);

                //How to set text size
                //text_view.setTextSize(TypedValue.COMPLEX_UNIT_DIP,25);

                //How to set text color
                //text_view.setTextColor(Color.parseColor("#FF831952"));
                return text_view;
            }
        };

        // Set a single choice items list for alert dialog
        builder.setSingleChoiceItems(
                arrayAdapter, // Items list
                -1, // Index of checked item (-1 = no selection)
                new DialogInterface.OnClickListener() // Item click listener
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (_nestedListener != null){
                            _nestedListener.onNestedListItemDialogClick(i, _selectedItem, _optionType);
                        }else {

                            try {
                                _listener.onListItemDialogClick(i, _selectedItem);
                            } catch (Exception e) {
                                Log.e(TAG, "onClick: onClickListener not set for ListItemDialog.");
                            }
                        }
                        dialogInterface.dismiss();
                    }
                });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        // safety check
        if (getDialog() == null)
            return;

        WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        layoutParams.width = 800;

        getDialog().getWindow().setAttributes(layoutParams);

        // ... other stuff you want to do in your onStart() method
    }

    public void initializeNested(ListItem selectedItem, NestedListItemClickListener nestedListener, ListItemType optionType){
        _selectedItem = selectedItem;
        _nestedListener = nestedListener;
        _optionType = optionType;
    }

    public void initialize(ListItem selectedItem, ListItemClickListener listener){
        _selectedItem = selectedItem;
        _listener = listener;

    }

    public interface NestedListItemClickListener{
        void onNestedListItemDialogClick(int position, ListItem selectedItem, ListItemType selectionType);
    }
}