package com.example.mansumugang;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ScheduleItem {

    /**
     * 스케줄 뷰를 생성하여 반환합니다.
     *
     * @param context          Context
     * @param layoutBox        LinearLayout
     * @param schedule         ScheduleResponse.Schedule
     * @param imageApiUrlPrefix String
     * @return View
     */
    public static View createScheduleView(Context context, LinearLayout layoutBox, ScheduleResponse.Schedule schedule, String imageApiUrlPrefix , String date , ScrollView scrollView) {
        // 스케줄 아이템 레이아웃을 인플레이트


        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        Date currentTime = new Date();
        String scheduleTimeStr = schedule.getTime();


        View scheduleView = LayoutInflater.from(context).inflate(R.layout.schedule_item, layoutBox, false);
        TextView timeView = scheduleView.findViewById(R.id.timeText);
        timeView.setText(" " + scheduleTimeStr);



        // layoutBox에 scheduleView를 추가
        layoutBox.addView(scheduleView);

        // scheduleView 내에서 additionalBox를 찾음
        LinearLayout additionalBox = scheduleView.findViewById(R.id.additionalBox);
        Button takingButton = scheduleView.findViewById(R.id.TakingButtonAct);

        List<Long> medicineIds = new ArrayList<>();

        if (schedule.getHospital() != null) {
            // 병원 정보가 있는 경우, 병원 뷰를 생성하여 추가
            View hospitalView = createHospitalView(context, schedule.getHospital());
            additionalBox.addView(hospitalView);
            setTakingHospitalButtonAttributes(context, takingButton , schedule.getHospital().isHospitalStatus() );
            // TakingButton 클릭 리스너 설정
            takingButton.setOnClickListener(v -> {
                if (context instanceof ScheduleActivity) {
                    ((ScheduleActivity) context).handleTakingButtonClick(schedule.getHospital().getHospitalId() , date);
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
        // 약물 상태에 따라 TakingButton 속성 설정
        if (!medicineIds.isEmpty()) {
            // 리스트의 첫 번째 약물 ID를 사용하여 버튼 속성 설정
            ScheduleResponse.Schedule.Medicine firstMedicine = schedule.getMedicines().get(0);
            setTakingMeicineButtonAttributes(context, takingButton, firstMedicine);

            // TakingButton 클릭 리스너 설정
            takingButton.setOnClickListener(v -> {
                if (context instanceof ScheduleActivity) {
                    ((ScheduleActivity) context).handleTakingButtonClick(medicineIds , schedule.getTime(), date);
                }
            });
        }

        try {
            Date scheduleTime = timeFormat.parse(scheduleTimeStr);

            if (scheduleTime.before(currentTime)){
                // 특정 조건을 만족하는 경우 additionalBox로 스크롤
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        // ScrollView의 LinearLayout의 위치로 스크롤 이동
                        int childIndex = layoutBox.indexOfChild(scheduleView);
                        if (childIndex != -1) {
                            try {
                                View childView = layoutBox.getChildAt(childIndex+1);
                                scrollView.scrollTo(0, childView.getTop());


                            } catch (Exception e)
                            {
                                View childView = layoutBox.getChildAt(childIndex);
                                scrollView.scrollTo(0, childView.getTop());

                            }
                        }
                    }
                });
                return scheduleView;

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return scheduleView;
    }

    private static View createHospitalView(Context context, ScheduleResponse.Schedule.Hospital hospital) {
        View medicineView = LayoutInflater.from(context).inflate(R.layout.medicine_item, null, false);

        TextView medicineNameView = medicineView.findViewById(R.id.medicineName);
        TextView hospitalNameView = medicineView.findViewById(R.id.hospitalName);
        TextView descriptionView = medicineView.findViewById(R.id.description);
        TextView hodpitalLocationView = medicineView.findViewById(R.id.hodpitalLocation);
        ImageView hospitalImage = medicineView.findViewById(R.id.medicineImage);


        hospitalImage.setImageDrawable(context.getResources().getDrawable(R.drawable.hospital));
        hospitalImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        hospitalImage.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        hospitalImage.getLayoutParams().height = 700;
        medicineNameView.setText(hospital.getHospitalName());
        hospitalNameView.setVisibility(View.GONE);
        hodpitalLocationView.setVisibility(View.VISIBLE);
        hodpitalLocationView.setText(hospital.getHospitalAddress());
        descriptionView.setText(hospital.getHospitalDescription());


//        첨부 이미지 사용
//        if (hospital.getMedicineImageName() == null) {
//            hospitalImage.setImageDrawable(context.getResources().getDrawable(R.drawable.hospital));
//            hospitalImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
//            hospitalImage.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
//            hospitalImage.getLayoutParams().height = 700;
//
//        } else {
//            ViewGroup.LayoutParams params = hospitalImage.getLayoutParams();
//            hospitalImage.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
//            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//            hospitalImage.getLayoutParams().height = 700;
//
//            hospitalImage.setLayoutParams(params);
//            String imageUrl = imageApiUrlPrefix + medicine.getMedicineImageName();
//            Glide.with(context)
//                    .load(imageUrl)
//
//                    .into(hospitalImage);
//        }
        return medicineView;

    }

    /**
     * 약물 뷰를 생성하여 반환합니다.
     *
     * @param context          Context
     * @param medicine         ScheduleResponse.Schedule.Medicine
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
            medicineImage.getLayoutParams().height = 700;

        } else {
            ViewGroup.LayoutParams params = medicineImage.getLayoutParams();
            medicineImage.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            medicineImage.getLayoutParams().height = 700;

            medicineImage.setLayoutParams(params);
            String imageUrl = imageApiUrlPrefix + medicine.getMedicineImageName();
            Glide.with(context)
                    .load(imageUrl)

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
    private static void setTakingMeicineButtonAttributes(Context context, Button takingButton, ScheduleResponse.Schedule.Medicine medicine) {

        if (medicine.isStatus().equals("WAITING")) {
            takingButton.setText("약을 드셨다면 여기를 눌러주세요");
            takingButton.setBackgroundColor(context.getResources().getColor(R.color.Info));
            takingButton.setTextColor(context.getResources().getColor(R.color.White));
        } else if (medicine.isStatus().equals("NOT_TIME")) {
            takingButton.setText("아직 약을 드실 시간이 아니에요");
            takingButton.setBackgroundColor(context.getResources().getColor(R.color.Gray45));
            takingButton.setTextColor(context.getResources().getColor(R.color.White));
            takingButton.setEnabled(false);
        } else if (medicine.isStatus().equals("NO_TAKEN")) {
            takingButton.setText("약을 안 드셨어요");
            takingButton.setTextColor(context.getResources().getColor(R.color.White));
            takingButton.setBackgroundColor(context.getResources().getColor(R.color.ImportantError));
            takingButton.setEnabled(false);
        } else if (medicine.isStatus().equals("PASS")) {
            takingButton.setText("약이 최근에 추가 되었어요");
            takingButton.setTextColor(context.getResources().getColor(R.color.White));
            takingButton.setBackgroundColor(context.getResources().getColor(R.color.SecondaryColorLight));
            takingButton.setEnabled(false);
        } else {
            takingButton.setText("약을 드셨어요!");
            takingButton.setTextColor(context.getResources().getColor(R.color.White));
            takingButton.setBackgroundColor(context.getResources().getColor(R.color.Success));

        }
    }
    private static void setTakingHospitalButtonAttributes(Context context, Button takingButton, Boolean status) {
        System.out.println(status);
        if (status) {
            takingButton.setText("병원을 방문하셨어요!");
            takingButton.setBackgroundColor(context.getResources().getColor(R.color.Gray45));
            takingButton.setTextColor(context.getResources().getColor(R.color.White));
        } else {
            takingButton.setText("병원에 방문하셨다면 여기를 눌러주세요");
            takingButton.setBackgroundColor(context.getResources().getColor(R.color.Info));
            takingButton.setTextColor(context.getResources().getColor(R.color.White));
        }
    }
}
