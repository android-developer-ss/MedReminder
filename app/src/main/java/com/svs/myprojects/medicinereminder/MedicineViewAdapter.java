package com.svs.myprojects.medicinereminder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;


/**
 * Created by snehalsutar on 2/15/16.
 */
public class MedicineViewAdapter extends RecyclerView.Adapter<MedicineViewAdapter.MyViewHolder> {
    ArrayList<MedicineViewItems> dataset_;
    MedicineViewItems dataValues = null;
    Context mContext = null;
    MedicineViewAdapterInterface mMedicineViewAdapterInterface;
    String LOG_TAG = "svs_me";

//Constructor.
    public MedicineViewAdapter(ArrayList<MedicineViewItems> dataset, Context paramContext,
                               MedicineViewAdapterInterface medicineViewAdapterInterface) {
        dataset_ = dataset;
        mContext = paramContext;
        this.mMedicineViewAdapterInterface = medicineViewAdapterInterface;
    }

    /***********************************************************************************************
     * VIEW HOLDER CLASS
     **********************************************************************************************/
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        //        public ImageView logoView;
        public TextView reminderCreateTime;
        public TextView medicineNameText;
        public TextView medicineTimeText;
        public TextView reminderBoolText;
        public TextView repeatingDaysText;
        public LinearLayout container;
        String id;

        public MyViewHolder(View v) {
            super(v);
            reminderCreateTime = (TextView) itemView.findViewById(R.id.reminder_create_time);
            medicineNameText = (TextView) itemView.findViewById(R.id.medicine_name);
            medicineTimeText = (TextView) itemView.findViewById(R.id.medicine_time);
            reminderBoolText = (TextView) itemView.findViewById(R.id.reminder);
            repeatingDaysText = (TextView) itemView.findViewById(R.id.repeat_text);
            container = (LinearLayout) itemView.findViewById(R.id.reminder_container);
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_medicine_reminder_recyclerview_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(v);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, final int position) {
        dataValues = dataset_.get(position);
        //------------------------------------------------------------------------------------------
        // Set reminder creation time
        //------------------------------------------------------------------------------------------
        viewHolder.reminderCreateTime.setText(DateFormat.format("MM/dd/yyyy", new Date(dataValues.getId())).toString() +
                " at " + Utility.convertTimeToAMPMWithSeconds(dataValues.getId()));


        //------------------------------------------------------------------------------------------
        // Set medicine name list
        //------------------------------------------------------------------------------------------
        viewHolder.medicineNameText.setText(dataValues.getMedicineName());


        //------------------------------------------------------------------------------------------
        // Set medicine taking time
        //------------------------------------------------------------------------------------------
        if (dataValues.getTime() != null) {
            Time time = dataValues.getTime();
            viewHolder.medicineTimeText.setText(Utility.convertTimeToAMPM(time.getHours(), time.getMinutes()));
        } else {
            viewHolder.medicineTimeText.setText("Time of alarm");
        }


        //------------------------------------------------------------------------------------------
        // Alarm Setting ON/OFF
        //------------------------------------------------------------------------------------------
        if (dataValues.isAlarmSetting()) {
            viewHolder.reminderBoolText.setText(mContext.getResources().getString(R.string.alarm_on));
            viewHolder.reminderBoolText.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        } else {
            viewHolder.reminderBoolText.setText(mContext.getResources().getString(R.string.alarm_off));
        }


        //------------------------------------------------------------------------------------------
        // Alarm Repeats on Every
        //------------------------------------------------------------------------------------------
        viewHolder.repeatingDaysText.setText(getAlarmRepeatingDays(dataValues));


        //------------------------------------------------------------------------------------------
        //  SET CLICK LISTENERS
        //------------------------------------------------------------------------------------------
        viewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMedicineViewAdapterInterface.modifyButtonClick(position);
            }
        });
    }

    private String getAlarmRepeatingDays(MedicineViewItems dataValues) {
        String repeatingDays = "";
        if (dataValues.isMonAlarmOn()) {
            repeatingDays += mContext.getResources().getString(R.string.monday_text) + " ";
        }
        if (dataValues.isTueAlarmOn()) {
            repeatingDays += mContext.getResources().getString(R.string.tuesday_text) + " ";
        }
        if (dataValues.isWedAlarmOn()) {
            repeatingDays += mContext.getResources().getString(R.string.wednesday_text) + " ";
        }
        if (dataValues.isThuAlarmOn()) {
            repeatingDays += mContext.getResources().getString(R.string.thursday_text) + " ";
        }
        if (dataValues.isFriAlarmOn()) {
            repeatingDays += mContext.getResources().getString(R.string.friday_text) + " ";
        }
        if (dataValues.isSatAlarmOn()) {
            repeatingDays += mContext.getResources().getString(R.string.saturday_text) + " ";
        }
        if (dataValues.isSunAlarmOn()) {
            repeatingDays += mContext.getResources().getString(R.string.sunday_text) + " ";
        }

        if (repeatingDays.length() < 1) {
            return mContext.getResources().getString(R.string.none);
        } else {
            return mContext.getResources().getString(R.string.repeats_on) + repeatingDays;
        }

    }

    @Override
    public int getItemCount() {
        return dataset_.size();
    }

}
