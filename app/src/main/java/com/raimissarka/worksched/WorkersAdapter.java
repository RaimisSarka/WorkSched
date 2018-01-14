package com.raimissarka.worksched;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.raimissarka.worksched.data.WorkersContract;

/**
 * Created by Raimis on 1/4/2018.
 */

public class WorkersAdapter extends RecyclerView.Adapter<WorkersAdapter.WorkersViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    /**
     * Constructor using the context and the db cursor
     *
     * @param context the calling context/activity
     */
    public WorkersAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;
    }

    @Override
    public WorkersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get the RecyclerView item layout
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.worker_list_item, parent, false);
        return new WorkersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WorkersAdapter.WorkersViewHolder holder, final int position) {
        // Move the mCursor to the position of the item to be displayed
        if (!mCursor.moveToPosition(position))
            return; // bail if returned null

        final String name = mCursor.getString(mCursor.getColumnIndex(WorkersContract.WorkersEntry.COLUMN_WORKER_NAME));
        final long id = mCursor.getLong(mCursor.getColumnIndex(WorkersContract.WorkersEntry._ID));
        final String phone = mCursor.getString(mCursor.getColumnIndex(WorkersContract.WorkersEntry.COLUMN_PHONE_NUMBER));
        final int dependency = mCursor.getInt(mCursor.getColumnIndex(WorkersContract.WorkersEntry.COLUMN_PHONE_NUMBER));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EditOneWorkerActivity.class);
                intent.putExtra("WORKER_NAME", name);
                intent.putExtra("WORKER_SHIFT_DEPENDENCY", dependency);
                intent.putExtra("WORKER_PHONE", phone);
                intent.putExtra("WORKERS_ID", id+"");
                mContext.startActivity(intent);
            }
        });
        // Display the worker name
        holder.nameTextView.setText(name);
        holder.itemView.setTag(id);
    }


    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    /**
     * Inner class to hold the views needed to display a single item in the recycler-view
     */
    class WorkersViewHolder extends RecyclerView.ViewHolder {

        // Will display the Workers name
        TextView nameTextView;

        /**
         * Constructor for our ViewHolder. Within this constructor, we get a reference to our
         * TextViews
         *
         * @param itemView The View that you inflated in
         *                 {@link WorkersAdapter#onCreateViewHolder(ViewGroup, int)}
         */
        public WorkersViewHolder(final View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.text_view_name);
        }

    }
}