package com.example.mansumugang;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * WeekCalendarAdapter 클래스는 주간 캘린더의 RecyclerView 어댑터를 관리합니다.
 */
public class WeekCalendarAdapter extends RecyclerView.Adapter<WeekCalendarAdapter.ViewHolder> {

    private Date[] weekDates;                          // 주간 날짜 배열
    private SimpleDateFormat dayFormat = new SimpleDateFormat("E", Locale.KOREAN); // 요일 형식
    private SimpleDateFormat dateFormat = new SimpleDateFormat("d", Locale.getDefault()); // 날짜 형식
    private int selectedPosition = RecyclerView.NO_POSITION; // 선택된 아이템의 위치

    /**
     * WeekCalendarAdapter 생성자
     * 주간 날짜 배열을 초기화합니다.
     */
    public WeekCalendarAdapter() {
        weekDates = new Date[7];
        Calendar calendar = Calendar.getInstance();

        // 오늘 날짜가 중앙에 오도록 설정
        int todayPosition = 3; // 주간 배열의 중앙 인덱스 (0-based)
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // 오늘 날짜를 중앙에 배치
        weekDates[todayPosition] = calendar.getTime();
        selectedPosition = todayPosition; // 중앙 날짜를 선택된 상태로 설정

        // 중앙에서 앞쪽으로 날짜 설정
        for (int i = todayPosition - 1; i >= 0; i--) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            weekDates[i] = calendar.getTime();
        }

        // 중앙에서 뒤쪽으로 날짜 설정
        calendar.setTime(weekDates[todayPosition]); // 중앙 날짜로 다시 설정
        for (int i = todayPosition + 1; i < 7; i++) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            weekDates[i] = calendar.getTime();
        }
    }

    /**
     * ViewHolder를 생성하여 반환합니다.
     *
     * @param parent   부모 뷰
     * @param viewType 뷰 타입
     * @return 생성된 ViewHolder
     */
    @NonNull
    @Override
    public WeekCalendarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_week_day, parent, false);
        return new ViewHolder(view);
    }

    /**
     * ViewHolder에 데이터를 바인딩합니다.
     *
     * @param holder   ViewHolder
     * @param position 위치
     */
    @Override
    public void onBindViewHolder(@NonNull WeekCalendarAdapter.ViewHolder holder, int position) {
        holder.dayTextView.setText(dayFormat.format(weekDates[position]));
        holder.dateTextView.setText(dateFormat.format(weekDates[position]));
        holder.itemView.setBackgroundColor(selectedPosition == position ? Color.LTGRAY : Color.TRANSPARENT);

        // 레이아웃 크기 설정
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        layoutParams.width = holder.itemView.getContext().getResources().getDisplayMetrics().widthPixels / 7;
        holder.itemView.setLayoutParams(layoutParams);

        // 아이템 클릭 리스너 설정
        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            if (previousPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(previousPosition);
            }
            notifyItemChanged(selectedPosition);
        });
    }

    /**
     * 아이템의 개수를 반환합니다.
     *
     * @return 아이템 개수
     */
    @Override
    public int getItemCount() {
        return weekDates.length;
    }

    /**
     * ViewHolder 클래스는 주간 캘린더의 각 아이템 뷰를 보유합니다.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dayTextView;  // 요일 텍스트 뷰
        TextView dateTextView; // 날짜 텍스트 뷰

        ViewHolder(View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.dayTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }
}
