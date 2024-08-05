package com.example.mansumugang;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ScheduleItem {

    public static View createScheduleView(Context context, LinearLayout layoutBox, ScheduleResponse.Schedule schedule, String imageApiUrlPrefix) {
        // Inflate the schedule item layout
        View scheduleView = LayoutInflater.from(context).inflate(R.layout.schedule_item, layoutBox, false);
        TextView timeView = scheduleView.findViewById(R.id.timeText);
        timeView.setText(" " + schedule.getTime());

        // Find the additionalBox inside the scheduleView
        LinearLayout additionalBox = scheduleView.findViewById(R.id.additionalBox);

        // Add medicine items to the additionalBox
        for (ScheduleResponse.Schedule.Medicine medicine : schedule.getMedicines()) {
            View medicineView = createMedicineView(context, medicine, imageApiUrlPrefix);
            additionalBox.addView(medicineView);
        }

        return scheduleView;
    }

    private static View createMedicineView(Context context, ScheduleResponse.Schedule.Medicine medicine, String imageApiUrlPrefix) {
        View medicineView = LayoutInflater.from(context).inflate(R.layout.medicine_item, null, false);

        TextView medicineNameView = medicineView.findViewById(R.id.medicineName);
        TextView hospitalNameView = medicineView.findViewById(R.id.hospitalName);
        TextView descriptionView = medicineView.findViewById(R.id.description);
        ImageView medicineImage = medicineView.findViewById(R.id.medicineImage);
        Button takingButton = medicineView.findViewById(R.id.takingButton);

        medicineNameView.setText(medicine.getMedicineName());
        hospitalNameView.setText(medicine.getHospitalName());
        descriptionView.setText(medicine.getMedicineDescription());

        if (medicine.getMedicineImageName()== null){
            medicineImage.setImageDrawable(context.getResources().getDrawable(R.drawable.medicine));
            medicineImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            medicineImage.getLayoutParams().height = 200;

        } else{
            String imageUrl = imageApiUrlPrefix + medicine.getMedicineImageName();
            Glide.with(context).load(imageUrl).into(medicineImage);

        }


        takingButton.setText("먹었어요");
        if (medicine.isStatus().equals("WAITING") || medicine.isStatus().equals("NOT_TIME")) {
            takingButton.setBackgroundColor(context.getResources().getColor(R.color.SecondaryColorDark));
        } else {
            takingButton.setBackgroundColor(context.getResources().getColor(R.color.Gray45));
            takingButton.setTextColor(context.getResources().getColor(R.color.White));
            takingButton.setEnabled(false);
        }

        return medicineView;
    }
}
