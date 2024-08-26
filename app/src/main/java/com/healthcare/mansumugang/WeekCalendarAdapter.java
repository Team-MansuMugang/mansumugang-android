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

/**
 * WeekCalendarAdapter 클래스는 RecyclerView에 주간 달력을 표시하는 어댑터입니다.
 */
public class WeekCalendarAdapter extends RecyclerView.Adapter<WeekCalendarAdapter.ViewHolder> {

    private Date[] weekDates; // 주간 날짜를 저장하는 배열
    private SimpleDateFormat dayFormat = new SimpleDateFormat("E", Locale.KOREAN); // 요일 포맷
    private SimpleDateFormat dateFormat = new SimpleDateFormat("d", Locale.getDefault()); // 날짜 포맷
    private int selectedPosition = RecyclerView.NO_POSITION; // 현재 선택된 날짜의 위치
    private OnDateSelectedListener dateSelectedListener; // 날짜 선택 리스너

    /**
     * WeekCalendarAdapter 생성자
     *
     * @param dateSelectedListener 날짜 선택 시 호출되는 리스너
     */
    public WeekCalendarAdapter(OnDateSelectedListener dateSelectedListener) {
        this.dateSelectedListener = dateSelectedListener;
        weekDates = new Date[7]; // 1주일을 위한 배열

        Calendar calendar = Calendar.getInstance();
        int todayPosition = 3; // 오늘 날짜의 배열 인덱스 (중간 위치로 설정)
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        weekDates[todayPosition] = calendar.getTime();
        selectedPosition = todayPosition;

        // 오늘을 기준으로 주간 날짜 설정 (오늘 이전 날짜)
        for (int i = todayPosition - 1; i >= 0; i--) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            weekDates[i] = calendar.getTime();
        }

        // 오늘을 기준으로 주간 날짜 설정 (오늘 이후 날짜)
        calendar.setTime(weekDates[todayPosition]);
        for (int i = todayPosition + 1; i < 7; i++) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            weekDates[i] = calendar.getTime();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // item_week_day 레이아웃을 inflate하여 ViewHolder를 생성합니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_week_day, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 해당 위치의 날짜 정보를 ViewHolder에 바인딩합니다.
        holder.dayTextView.setText(dayFormat.format(weekDates[position]));
        holder.dateTextView.setText(dateFormat.format(weekDates[position]));

        // 선택된 날짜와 현재 항목의 날짜를 비교하여 배경 및 텍스트 색상 설정
        if (selectedPosition == position) {
            holder.dateTextView.setBackgroundResource(R.drawable.week_calender_rounded_corner_active);
            holder.dateTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.White));
        } else {
            holder.dateTextView.setBackgroundResource(R.drawable.week_calender_rounded_corner);
            holder.dateTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.Black));
        }

        // 각 날짜 항목의 너비를 화면 너비의 1/7로 설정
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        layoutParams.width = holder.itemView.getContext().getResources().getDisplayMetrics().widthPixels / 7;
        holder.itemView.setLayoutParams(layoutParams);

        // 날짜 항목 클릭 시 선택된 날짜를 리스너에 전달
        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            // 이전 선택 항목과 현재 선택 항목의 상태를 업데이트
            if (previousPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(previousPosition);
            }
            notifyItemChanged(selectedPosition);

            // 선택된 날짜를 리스너에 전달
            if (dateSelectedListener != null) {
                dateSelectedListener.onDateSelected(weekDates[selectedPosition]);
            }
        });
    }

    @Override
    public int getItemCount() {
        // 주간 날짜 배열의 길이 반환 (7일)
        return weekDates.length;
    }

    /**
     * ViewHolder 클래스는 각 날짜 항목의 뷰를 저장합니다.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dayTextView; // 요일 텍스트 뷰
        TextView dateTextView; // 날짜 텍스트 뷰

        ViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            dayTextView = itemView.findViewById(R.id.dayTextView);
        }
    }
}
