package com.raimissarka.worksched;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.raimissarka.worksched.data.ShiftsContract;

import java.util.Date;

/**
 * Created by Raimis on 1/14/2018.
 */

public class ShiftsAdapter extends RecyclerView.Adapter<ShiftsAdapter.ShiftsViewHolder> {

    private Cursor mCursor;
    private Context mContext;



    public ShiftsAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;
    }



    @Override
    public ShiftsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get the RecyclerView item layout
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.shift_list_item, parent, false);
        return new ShiftsViewHolder(view);
    }



    @Override
    public void onBindViewHolder(ShiftsViewHolder holder, final int position) {
        // Move the mCursor to the position of the item to be displayed
        if (!mCursor.moveToPosition(position))
            return; // bail if returned null

        final long id = mCursor.getLong(mCursor.getColumnIndex(ShiftsContract.ShiftsEntry._ID));
        final String mName = mCursor.getString(mCursor.getColumnIndex(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_NAME));
        final int mNumber = mCursor.getInt(mCursor.getColumnIndex(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_NUMBER));
        final int mType = mCursor.getInt(mCursor.getColumnIndex(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_TYPE));
        final String mDate = mCursor.getString(mCursor.getColumnIndexOrThrow(ShiftsContract.ShiftsEntry.COLUMN_SHIFT_START_DATE));



        holder.itemView.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EditOneShiftActivity.class);
                intent.putExtra("SHIFT_NAME", mName);
                intent.putExtra("SHIFT_NUMBER", mNumber + "");
                intent.putExtra("SHIFT_TYPE", mType + "");
                intent.putExtra("SHIFT_START_DATE", mDate);
                intent.putExtra("SHIFT_ID", id+"");
                mContext.startActivity(intent);
            }


        });


        holder.mShiftNumberTextView.setText(String.valueOf(mNumber));
        holder.mShiftNameTextView.setText(mName);
        holder.itemView.setTag(id);
    }



    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }



    public class ShiftsViewHolder extends RecyclerView.ViewHolder {

        RadioButton mShowShiftButton;
        TextView mShiftNumberTextView;
        TextView mShiftNameTextView;


        public ShiftsViewHolder(View itemView) {
            super(itemView);

            mShowShiftButton = (RadioButton) itemView.findViewById(R.id.rb_shift_show);
            mShiftNumberTextView = (TextView) itemView.findViewById(R.id.tv_shift_number_text);
            mShiftNameTextView = (TextView) itemView.findViewById(R.id.tv_shift_item_name);
        }


    }


}
