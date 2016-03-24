package com.svs.myprojects.medicinereminder;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by snehalsutar on 2/16/16.
 */
public class FragmentAddMedicineReminder extends Fragment implements View.OnClickListener {

    private String LOG_TAG = "svs_me";
    private Button mSelectTimeButton, mSaveMedButton;
    private Context mContext;
    private Switch mAlarmOnOffSwitch;
    private View mRootView;
    private FloatingActionButton fab;
    private EditText mMedListText;
    private CheckBox mMonAlarm, mTueAlarm, mWedAlarm, mThuAlarm, mFriAlarm, mSatAlarm, mSunAlarm;
    private int mHourOfDay, mMinute;
    private long mMillisec;
    private long mAlarmId;
    private int alarmItemNum = -1;
    ArrayList<MedicineViewItems> sharedPrefMedAlarmArr;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        String jsonArray = bundle.getString(Constants.INTERFACE_BUNDLE_STR);
        Type type = new TypeToken<ArrayList<MedicineViewItems>>() {
        }.getType();
        Gson gson = new Gson();
        sharedPrefMedAlarmArr = gson.fromJson(jsonArray, type);

        alarmItemNum = -1;
        alarmItemNum = bundle.getInt(Constants.INTERFACE_ITEM_POSITION);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_med_reminder, container, false);
        mRootView = rootView;
        mContext = getActivity();
        mSelectTimeButton = (Button) rootView.findViewById(R.id.select_time_button);
        mSaveMedButton = (Button) rootView.findViewById(R.id.save_med_button);
        mAlarmOnOffSwitch = (Switch) rootView.findViewById(R.id.alarm_on_off_switch);
        mMedListText = (EditText) rootView.findViewById(R.id.med_list_text);
        mMonAlarm = (CheckBox) rootView.findViewById(R.id.mon_alarm);
        mTueAlarm = (CheckBox) rootView.findViewById(R.id.tue_alarm);
        mWedAlarm = (CheckBox) rootView.findViewById(R.id.wed_alarm);
        mThuAlarm = (CheckBox) rootView.findViewById(R.id.thu_alarm);
        mFriAlarm = (CheckBox) rootView.findViewById(R.id.fri_alarm);
        mSatAlarm = (CheckBox) rootView.findViewById(R.id.sat_alarm);
        mSunAlarm = (CheckBox) rootView.findViewById(R.id.sun_alarm);

        mSelectTimeButton.setOnClickListener(this);
        mSaveMedButton.setOnClickListener(this);

        if (alarmItemNum != -1) {
            setValuesInLayout();
        }
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab_pill);
        fab.setVisibility(View.GONE);
        return rootView;
    }

    /***********************************************************************************************
     * If we clicked to modify the view, then alarmItemNum will have valid position from the
     * listview. Using this position update the values on ADD MEDICINE screen. Called from onCreate.
     */
    public void setValuesInLayout() {
        MedicineViewItems medicineViewItems = sharedPrefMedAlarmArr.get(alarmItemNum);
        mMedListText.setText(medicineViewItems.getMedicineName());
        mSelectTimeButton.setText(Utility.convertTimeToAMPM(medicineViewItems.getTime().getHours(), medicineViewItems.getTime().getMinutes()));
        mAlarmOnOffSwitch.setChecked(medicineViewItems.isAlarmSetting());
        mMonAlarm.setChecked(medicineViewItems.isMonAlarmOn());
        mTueAlarm.setChecked(medicineViewItems.isThuAlarmOn());
        mWedAlarm.setChecked(medicineViewItems.isWedAlarmOn());
        mThuAlarm.setChecked(medicineViewItems.isThuAlarmOn());
        mFriAlarm.setChecked(medicineViewItems.isFriAlarmOn());
        mSatAlarm.setChecked(medicineViewItems.isSatAlarmOn());
        mSunAlarm.setChecked(medicineViewItems.isSunAlarmOn());
    }

    /***********************************************************************************************
     * On Click Listeners for saving the medicine reminder button and selecting time.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_time_button:
                Log.i(LOG_TAG, "select_time_button");
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getChildFragmentManager(), "timePicker");
                break;
            case R.id.save_med_button:
                mAlarmId = System.currentTimeMillis();
                saveAlarm();

                FragmentManager fm = getFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    Log.i("MainActivity", "popping backstack");
                    fm.popBackStack();
                } else {
                    Log.i("MainActivity", "nothing on backstack, calling super");
                }
                break;
        }
    }

    /***********************************************************************************************
     * Save alarm will perform functionality to remove the existing alarm and set new alarm.
     */
    public void saveAlarm() {
        removePreviousAlarmsFromSystem();
        //Update UI for medicine reminder list and save the same values in Shared Preferences.
        updateUIAndSharedPrefWithAlarmValues();

    }

    /***********************************************************************************************
     * While modifying the existing alarm delete all the previously set alarm in the system one by
     * one. Do this after checking if the alarm switch was set on for this particular alarm.
     */
    public void removePreviousAlarmsFromSystem() {
        // Check if alarmItemNum is not -1 i.e. invalid position in the list view. If it is a valid
        // value then remove the existing values and replace it with modified values.
        if (alarmItemNum != -1) {
            // Get the alarm reminder object to be deleted.
            MedicineViewItems medToBeDeleted = sharedPrefMedAlarmArr.get(alarmItemNum);
            Utility.removeAlarmFromSystem(mContext,medToBeDeleted);

            // Remove the previous values of alarm from the shared preferences so that new values can
            // be stored. Works as an update = delete + insert.
            sharedPrefMedAlarmArr.remove(alarmItemNum);
            //Toast.makeText(mContext, "alarmItemNum" + alarmItemNum, Toast.LENGTH_LONG).show();
        }
    }



    /***********************************************************************************************
     * Update UI for medicine reminder list and save the same values in Shared Preferences.
     * Add the medicine reminder item to the list and Adapter will take care of updating the UI.
     */
    private void updateUIAndSharedPrefWithAlarmValues() {
        ArrayList<MedicineViewItems> arrayList = new ArrayList<>();
        MedicineViewItems medicineViewItems = new MedicineViewItems();
        medicineViewItems.setId(mAlarmId);
        medicineViewItems.setAlarmSetting(mAlarmOnOffSwitch.isChecked());
        medicineViewItems.setMedicineName(mMedListText.getText().toString());
        medicineViewItems.setTime(new Time(mMillisec));
        medicineViewItems.setMonAlarmOn(mMonAlarm.isChecked());
        medicineViewItems.setTueAlarmOn(mTueAlarm.isChecked());
        medicineViewItems.setWedAlarmOn(mWedAlarm.isChecked());
        medicineViewItems.setThuAlarmOn(mThuAlarm.isChecked());
        medicineViewItems.setFriAlarmOn(mFriAlarm.isChecked());
        medicineViewItems.setSatAlarmOn(mSatAlarm.isChecked());
        medicineViewItems.setSunAlarmOn(mSunAlarm.isChecked());

        arrayList.add(medicineViewItems);
        if (sharedPrefMedAlarmArr != null) {
            arrayList.addAll(sharedPrefMedAlarmArr);
        }

        Utility.saveAlarmListToSharedPref(mContext, arrayList);

        // Check if Alarm switch is ON. If yes then set the alarm in the system.
        if (mAlarmOnOffSwitch.isChecked()) {
            ArrayList<Integer> selectedDays = Utility.getDaysSelected(medicineViewItems);
            if (selectedDays.size() > 0) {
                for (int i = 0; i < selectedDays.size(); i++) {
                    Utility.setAlarmInDevice(mHourOfDay, mMinute, mContext, mMedListText.getText().toString(), mAlarmId, selectedDays.get(i));
                }
            } else {
                Utility.setAlarmInDevice(mHourOfDay, mMinute, mContext, mMedListText.getText().toString(), mAlarmId, 0);
            }
        }
    }

    /***********************************************************************************************
     * Time Picker Dialog Fragment to set the time for medicine.
     */
    public class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            mSelectTimeButton.setText(Utility.convertTimeToAMPM(hourOfDay, minute));
            mHourOfDay = hourOfDay;
            mMinute = minute;

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            mMillisec = calendar.getTimeInMillis();
        }
    }
}


