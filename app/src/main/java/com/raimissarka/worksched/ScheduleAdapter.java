package com.raimissarka.worksched;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Raimis on 1/22/2018.
 */

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private List<DayData> dayDataList;

    public ScheduleAdapter(List<DayData> dayDataList) {
        this.dayDataList = dayDataList;
    }

    @Override
    public ScheduleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_list_item, parent, false);
        return new ScheduleViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(ScheduleViewHolder holder, int position) {

        int colorAccent = ContextCompat.getColor(holder.itemView.getContext(), R.color.colorAccent);
        int colorText = ContextCompat.getColor(holder.itemView.getContext(), R.color.colorSimpleText);

        DayData mData = dayDataList.get(position);
        if (mData.getWeekend()){
            holder.mDateTextView.setTextColor(colorAccent);
        } else {
            holder.mDateTextView.setTextColor(colorText);
        }

        holder.mDateTextView.setText(mData.getDate());
        holder.mShiftNoTextView.setText(mData.getShiftNo());
        holder.mWorkersNameTextView.setText(mData.getWorkerName());
    }




    @Override
    public int getItemCount() {
        return dayDataList.size();
    }

    public class ScheduleViewHolder extends RecyclerView.ViewHolder {

        public TextView mDateTextView;
        public TextView mShiftNoTextView;
        public TextView mWorkersNameTextView;

        public ScheduleViewHolder(View itemView) {
            super(itemView);
            mDateTextView = (TextView) itemView.findViewById(R.id.text_view_schedule_view_date);
            mShiftNoTextView = (TextView) itemView.findViewById(R.id.text_view_schedule_view_shift_no);
            mWorkersNameTextView = (TextView) itemView.findViewById(R.id.text_view_schedule_view_name);
        }
    }
}
