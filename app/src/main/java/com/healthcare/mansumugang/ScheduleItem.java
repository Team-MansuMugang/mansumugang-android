package com.healthcare.mansumugang;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * ScheduleItem 클래스는 일정 항목을 생성하고 처리하는데 필요한 메소드를 제공합니다.
 */
public class ScheduleItem {

    /**
     * 스케줄 뷰를 생성하여 반환합니다.
     *
     * @param context           컨텍스트
     * @param layoutBox         일정 항목을 추가할 레이아웃
     * @param schedule          스케줄 데이터
     * @param imageApiUrlPrefix 이미지 API URL 접두사
     * @param date              일정 날짜
     * @return 생성된 스케줄 뷰
     */
    public static void createScheduleView(Context context, LinearLayout layoutBox, ScheduleResponse.Schedule schedule, String imageApiUrlPrefix, String date) {
        String scheduleTimeStr = schedule.getTime(); // 일정 시간 문자열

        if (schedule.getHospital() != null) {

            // 스케줄 항목의 뷰를 생성합니다.
            View scheduleView = LayoutInflater.from(context).inflate(R.layout.schedule_item, layoutBox, false);
            TextView timeView = scheduleView.findViewById(R.id.timeText);
            timeView.setText(" " + scheduleTimeStr);

            // 레이아웃 박스에 생성된 스케줄 뷰를 추가합니다.
            layoutBox.addView(scheduleView);

            // 추가 정보 박스를 찾습니다.
            LinearLayout additionalBox = scheduleView.findViewById(R.id.additionalBox);
            TextView takingButton = scheduleView.findViewById(R.id.TakingButtonAct);


            // 병원 정보가 있는 경우, 병원 뷰를 생성하여 추가합니다.
            View hospitalView = createHospitalView(context, schedule.getHospital());
            additionalBox.addView(hospitalView);

            // 현재 시간이 스케줄 시간 범위 내에 있는지 확인합니다.
            boolean pastTime = inTimeRange(date, scheduleTimeStr);
            setTakingHospitalButtonAttributes(pastTime, takingButton, schedule.getHospital().isHospitalStatus());

            // TakingButton 클릭 리스너를 설정합니다.
            takingButton.setOnClickListener(v -> {
                if (context instanceof ScheduleActivity) {
                    ((ScheduleActivity) context).handleTakingButtonClick(schedule.getHospital().getHospitalId(), date);
                }
            });
        }
        if (schedule.getMedicines() == null) {
            return;
        }


        // 스케줄 항목의 뷰를 생성합니다.
        View scheduleView = LayoutInflater.from(context).inflate(R.layout.schedule_item, layoutBox, false);
        TextView timeView = scheduleView.findViewById(R.id.timeText);
        timeView.setText(" " + scheduleTimeStr);

        // 레이아웃 박스에 생성된 스케줄 뷰를 추가합니다.
        layoutBox.addView(scheduleView);

        // 추가 정보 박스를 찾습니다.
        LinearLayout additionalBox = scheduleView.findViewById(R.id.additionalBox);
        TextView takingButton = scheduleView.findViewById(R.id.TakingButtonAct);


        List<Long> medicineIds = new ArrayList<>();

        // 약물 아이템을 추가합니다.
        for (ScheduleResponse.Schedule.Medicine medicine : schedule.getMedicines()) {
            View medicineView = createMedicineView(context, medicine, imageApiUrlPrefix);
            additionalBox.addView(medicineView);

            // 약물 ID를 리스트에 추가합니다.
            medicineIds.add(medicine.getMedicineId());
        }

        // TakingButton 설정
        if (!medicineIds.isEmpty()) {
            ScheduleResponse.Schedule.Medicine firstMedicine = schedule.getMedicines().get(0);
            setTakingMeicineButtonAttributes(context, takingButton, firstMedicine);

            // TakingButton 클릭 리스너를 설정합니다.
            takingButton.setOnClickListener(v -> {
                if (context instanceof ScheduleActivity) {
                    ((ScheduleActivity) context).handleTakingButtonClick(medicineIds, schedule.getTime(), date);
                }
            });
        }

    }

    /**
     * 병원 뷰를 생성하여 반환합니다.
     *
     * @param context  컨텍스트
     * @param hospital 병원 데이터
     * @return 생성된 병원 뷰
     */
    private static View createHospitalView(Context context, ScheduleResponse.Schedule.Hospital hospital) {
        // 병원 항목의 뷰를 생성합니다.
        View medicineView = LayoutInflater.from(context).inflate(R.layout.medicine_item, null, false);

        TextView medicineNameView = medicineView.findViewById(R.id.medicineName);
        TextView hospitalNameView = medicineView.findViewById(R.id.hospitalName);
        TextView descriptionView = medicineView.findViewById(R.id.description);
        TextView hospitalLocationView = medicineView.findViewById(R.id.hodpitalLocation);
        ImageView hospitalImage = medicineView.findViewById(R.id.medicineImage);

        hospitalImage.setImageDrawable(context.getResources().getDrawable(R.drawable.hospital));
        hospitalImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        hospitalImage.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        hospitalImage.getLayoutParams().height = 700;

        medicineNameView.setText(hospital.getHospitalName());
        hospitalNameView.setVisibility(View.GONE);
        hospitalLocationView.setVisibility(View.VISIBLE);
        hospitalLocationView.setText(hospital.getHospitalAddress());
        descriptionView.setText(hospital.getHospitalDescription());

        // 병원 위치를 클릭하면 지도로 열리게 합니다.
        hospitalLocationView.setOnClickListener(v -> {
            double latitude = hospital.getLatitude();
            double longitude = hospital.getLongitude();
            String uri = String.format("geo:%f,%f?q=%f,%f(%s)", latitude, longitude, latitude, longitude, hospital.getHospitalName());
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            context.startActivity(intent);
        });

        return medicineView;
    }

    /**
     * 약물 뷰를 생성하여 반환합니다.
     *
     * @param context           컨텍스트
     * @param medicine          약물 데이터
     * @param imageApiUrlPrefix 이미지 API URL 접두사
     * @return 생성된 약물 뷰
     */
    private static View createMedicineView(Context context, ScheduleResponse.Schedule.Medicine medicine, String imageApiUrlPrefix) {
        // 약물 항목의 뷰를 생성합니다.
        View medicineView = LayoutInflater.from(context).inflate(R.layout.medicine_item, null, false);

        TextView medicineNameView = medicineView.findViewById(R.id.medicineName);
        TextView hospitalNameView = medicineView.findViewById(R.id.hospitalName);
        TextView descriptionView = medicineView.findViewById(R.id.description);
        ImageView medicineImage = medicineView.findViewById(R.id.medicineImage);

        medicineNameView.setText(medicine.getMedicineName());
        hospitalNameView.setText(medicine.getHospitalName());
        descriptionView.setText(medicine.getMedicineDescription());

        // 약물 이미지 설정
        if (medicine.getMedicineImageName() == null) {
            medicineImage.setImageDrawable(context.getResources().getDrawable(R.drawable.medicine));
            medicineImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            medicineImage.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            medicineImage.getLayoutParams().height = 500;
        } else {
            ViewGroup.LayoutParams params = medicineImage.getLayoutParams();
            medicineImage.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            medicineImage.getLayoutParams().height = 500;

            medicineImage.setLayoutParams(params);
            String imageUrl = imageApiUrlPrefix + medicine.getMedicineImageName();
            Glide.with(context).load(imageUrl).into(medicineImage);
        }

        return medicineView;
    }

    /**
     * 약물 상태에 따라 TakingButton의 속성을 설정합니다.
     *
     * @param context      컨텍스트
     * @param takingButton 버튼
     * @param medicine     약물 데이터
     */
    private static void setTakingMeicineButtonAttributes(Context context, TextView takingButton, ScheduleResponse.Schedule.Medicine medicine) {
        if (medicine.isStatus().equals("WAITING")) {
            takingButton.setText("약을 드셨다면 여기를 눌러주세요");
            takingButton.setBackgroundResource(R.drawable.schedule_button_blue);
        } else if (medicine.isStatus().equals("NOT_TIME")) {
            takingButton.setText("아직 약을 드실 시간이 아니에요");
            takingButton.setBackgroundResource(R.drawable.schedule_button_gray);
            takingButton.setEnabled(false);
        } else if (medicine.isStatus().equals("NO_TAKEN")) {
            takingButton.setText("약을 안 드셨어요");
            takingButton.setBackgroundResource(R.drawable.schedule_button_red);
            takingButton.setEnabled(false);
        } else if (medicine.isStatus().equals("PASS")) {
            takingButton.setText("약이 최근에 추가 되었어요");
            takingButton.setBackgroundResource(R.drawable.schedule_button_gray);
            takingButton.setEnabled(false);
        } else {
            takingButton.setText("약을 드셨어요!");
            takingButton.setBackgroundResource(R.drawable.schedule_button_green);
        }
    }

    /**
     * 병원 방문 버튼의 속성을 설정합니다.
     *
     * @param inTimeRange  일정 시간 범위 내에 있는지 여부
     * @param takingButton 버튼
     * @param status       병원 상태
     */
    private static void setTakingHospitalButtonAttributes(Boolean inTimeRange, TextView takingButton, Boolean status) {
        if (status) {
            takingButton.setText("병원을 방문하셨어요!");
            takingButton.setBackgroundResource(R.drawable.schedule_button_gray);
        } else if (inTimeRange) {
            takingButton.setText("병원에 방문하셨다면 여기를 눌러주세요");
            takingButton.setBackgroundResource(R.drawable.schedule_button_blue);
        } else {
            takingButton.setText("병원에 방문예정이 있습니다");
            takingButton.setBackgroundResource(R.drawable.schedule_button_gray);
            takingButton.setEnabled(false);
        }
    }

    /**
     * 주어진 날짜와 시간으로 일정이 현재 시간 범위 내에 있는지 확인합니다.
     *
     * @param date 일정 날짜
     * @param time 일정 시간
     * @return 일정이 현재 시간 범위 내에 있으면 true, 그렇지 않으면 false
     */
    private static boolean inTimeRange(String date, String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String dateTimeString = date + " " + time;
        try {
            Date scheduleTime = sdf.parse(dateTimeString);
            Calendar scheduleCalendar = Calendar.getInstance();
            scheduleCalendar.setTime(scheduleTime);

            Calendar currentTime = Calendar.getInstance();
            Calendar oneHourAfter = (Calendar) scheduleCalendar.clone();
            oneHourAfter.add(Calendar.HOUR_OF_DAY, 1);

            return currentTime.after(scheduleCalendar) && currentTime.before(oneHourAfter);
        } catch (ParseException e) {
            return false;
        }
    }
}
