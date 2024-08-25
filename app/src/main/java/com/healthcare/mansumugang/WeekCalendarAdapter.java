package com.healthcare.mansumugang;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WeekCalendarAdapter extends RecyclerView.Adapter<WeekCalendarAdapter.ViewHolder> {

    private Date[] weekDates;
    private SimpleDateFormat dayFormat = new SimpleDateFormat("E", Locale.KOREAN);
    private SimpleDateFormat dateFormat = new SimpleDateFormat("d", Locale.getDefault());
    private int selectedPosition = RecyclerView.NO_POSITION;
    private OnDateSelectedListener dateSelectedListener;

    public WeekCalendarAdapter(OnDateSelectedListener dateSelectedListener) {
        this.dateSelectedListener = dateSelectedListener;
        weekDates = new Date[7];
        Calendar calendar = Calendar.getInstance();
        int todayPosition = 3;
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        weekDates[todayPosition] = calendar.getTime();
        selectedPosition = todayPosition;

        for (int i = todayPosition - 1; i >= 0; i--) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            weekDates[i] = calendar.getTime();
        }

        calendar.setTime(weekDates[todayPosition]);
        for (int i = todayPosition + 1; i < 7; i++) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            weekDates[i] = calendar.getTime();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_week_day, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.dayTextView.setText(dayFormat.format(weekDates[position]));
        holder.dateTextView.setText(dateFormat.format(weekDates[position]));

        if (selectedPosition == position) {
            holder.dateTextView.setBackgroundResource(R.drawable.week_calender_rounded_corner_active);
            holder.dateTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.White));
        } else {
            holder.dateTextView.setBackgroundResource(R.drawable.week_calender_rounded_corner);
            holder.dateTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.Black));
        }

        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        layoutParams.width = holder.itemView.getContext().getResources().getDisplayMetrics().widthPixels / 7;
        holder.itemView.setLayoutParams(layoutParams);

        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            if (previousPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(previousPosition);
            }
            notifyItemChanged(selectedPosition);

            if (dateSelectedListener != null) {
                dateSelectedListener.onDateSelected(weekDates[selectedPosition]);
            }
        });
    }

    @Override
    public int getItemCount() {
        return weekDates.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dayTextView;
        TextView dateTextView;

        ViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            dayTextView = itemView.findViewById(R.id.dayTextView);
        }
    }
}
