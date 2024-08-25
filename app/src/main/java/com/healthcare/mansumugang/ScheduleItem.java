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


public class ScheduleItem {

    /**
     * 스케줄 뷰를 생성하여 반환합니다.
     *
     * @param context           Context
     * @param layoutBox         LinearLayout
     * @param schedule          ScheduleResponse.Schedule
     * @param imageApiUrlPrefix String
     * @return View
     */
    public static View createScheduleView(Context context, LinearLayout layoutBox, ScheduleResponse.Schedule schedule, String imageApiUrlPrefix, String date) {

        String scheduleTimeStr = schedule.getTime();

        View scheduleView = LayoutInflater.from(context).inflate(R.layout.schedule_item, layoutBox, false);
        TextView timeView = scheduleView.findViewById(R.id.timeText);
        timeView.setText(" " + scheduleTimeStr);

        // layoutBox에 scheduleView를 추가
        layoutBox.addView(scheduleView);

        // scheduleView 내에서 additionalBox를 찾음
        LinearLayout additionalBox = scheduleView.findViewById(R.id.additionalBox);
        TextView takingButton = scheduleView.findViewById(R.id.TakingButtonAct);

        List<Long> medicineIds = new ArrayList<>();

        if (schedule.getHospital() != null) {
            // 병원 정보가 있는 경우, 병원 뷰를 생성하여 추가
            View hospitalView = createHospitalView(context, schedule.getHospital());
            additionalBox.addView(hospitalView);
            boolean pastTime = inTimeRange(date, scheduleTimeStr);

            setTakingHospitalButtonAttributes(pastTime, takingButton, schedule.getHospital().isHospitalStatus());
            // TakingButton 클릭 리스너 설정
            takingButton.setOnClickListener(v -> {
                if (context instanceof ScheduleActivity) {
                    ((ScheduleActivity) context).handleTakingButtonClick(schedule.getHospital().getHospitalId(), date);
                }
            });
        }

        // additionalBox에 약물 아이템 추가
        for (ScheduleResponse.Schedule.Medicine medicine : schedule.getMedicines()) {
            View medicineView = createMedicineView(context, medicine, imageApiUrlPrefix);
            additionalBox.addView(medicineView);

            // medicineId를 리스트에 추가
            medicineIds.add(medicine.getMedicineId());
        }

        // TakingButton 설정
        if (!medicineIds.isEmpty()) {
            ScheduleResponse.Schedule.Medicine firstMedicine = schedule.getMedicines().get(0);
            setTakingMeicineButtonAttributes(context, takingButton, firstMedicine);
            takingButton.setOnClickListener(v -> {
                if (context instanceof ScheduleActivity) {
                    ((ScheduleActivity) context).handleTakingButtonClick(medicineIds, schedule.getTime(), date);
                }
            });
        }


        return scheduleView;
    }


    private static View createHospitalView(Context context, ScheduleResponse.Schedule.Hospital hospital) {
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
     * @param context           Context
     * @param medicine          ScheduleResponse.Schedule.Medicine
     * @param imageApiUrlPrefix String
     * @return View
     */
    private static View createMedicineView(Context context, ScheduleResponse.Schedule.Medicine medicine, String imageApiUrlPrefix) {
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
            Glide.with(context).load(imageUrl)

                    .into(medicineImage);
        }

        return medicineView;
    }

    /**
     * 약물 상태에 따라 TakingButton의 속성을 설정합니다.
     *
     * @param context      Context
     * @param takingButton Button
     * @param medicine     ScheduleResponse.Schedule.Medicine
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
            if (scheduleTime != null) {
                oneHourAfter.setTime(scheduleTime);
                oneHourAfter.add(Calendar.HOUR_OF_DAY, 1);

            }

            return currentTime.after(scheduleCalendar) && currentTime.before(oneHourAfter);
        } catch (ParseException e) {
            return false;
        }
    }
}