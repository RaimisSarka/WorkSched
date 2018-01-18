package com.raimissarka.worksched;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.raimissarka.worksched.data.EmploymentsContract;

/**
 * Created by Raimis on 1/16/2018.
 */

public class EmploymentsAdapter extends RecyclerView.Adapter<EmploymentsAdapter.EmploymentsViewHolder> {

    private Cursor mCursor;
    private Context mContext;



    public EmploymentsAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;
    }



    @Override
    public EmploymentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get the RecyclerView item layout
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.employment_list_item, parent, false);
        return new EmploymentsViewHolder(view);
    }



    @Override
    public void onBindViewHolder(EmploymentsViewHolder holder, final int position) {
        // Move the mCursor to the position of the item to be displayed
        if (!mCursor.moveToPosition(position))
            return; // bail if returned null

        final long id = mCursor.getLong(mCursor.getColumnIndex(EmploymentsContract.EmploymentsEntry._ID));
        final String mName = mCursor.getString(mCursor.getColumnIndex(EmploymentsContract.EmploymentsEntry.COLUMN_EMPLOYMENT_NAME));



        holder.itemView.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EditOneEmploymentActivity.class);
                intent.putExtra("EMPLOYMENT_NAME", mName);
                intent.putExtra("EMPLOYMENT_ID", id+"");
                mContext.startActivity(intent);
            }


        });


        holder.mEmploymentNameTextView.setText(mName);
        holder.itemView.setTag(id);
    }



    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }



    public class EmploymentsViewHolder extends RecyclerView.ViewHolder {

        TextView mEmploymentNameTextView;


        public EmploymentsViewHolder(View itemView) {
            super(itemView);

            mEmploymentNameTextView = (TextView) itemView.findViewById(R.id.text_view_employment_name);
        }


    }


}
