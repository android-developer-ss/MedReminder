package com.svs.myprojects.medicinereminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by snehalsutar on 2/16/16.
 */
public class Utility {

    // Used to convert 24hr format to 12hr format with AM/PM values
    public static String convertTimeToAMPM(int hours, int mins) {

        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";


        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        // Append in a StringBuilder
        String aTime = new StringBuilder().append(hours).append(':')
                .append(minutes).append(" ").append(timeSet).toString();

        return aTime;
    }


    /***********************************************************************************************
     * Save all alarms in Shared Preferences to display it on the list on UI.
     */
    public static void saveAlarmListToSharedPref(Context context, ArrayList<MedicineViewItems> arrayList) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString(Constants.PREF_NAME_ALARM, json);
        editor.commit();
    }

    /***********************************************************************************************
     * Convert the Shared Preferences Alarm List stored in JSON Format to arraylist.
     */
    public static ArrayList<MedicineViewItems> getAlarmListArrayList(Context context) {
        ArrayList<MedicineViewItems> sharedPrefMedAlarmArr = null;
        String json = getAlarmListArrayListStringFormat(context);
        Gson gson = new Gson();
        if (json != null) {
            Type type = new TypeToken<ArrayList<MedicineViewItems>>() {
            }.getType();
            sharedPrefMedAlarmArr = gson.fromJson(json, type);
        }
        return sharedPrefMedAlarmArr;
    }

    /***********************************************************************************************
     * Get the shared Preference JSON format string of all the saved alarm list.
     */
    public static String getAlarmListArrayListStringFormat(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = sharedPrefs.getString(Constants.PREF_NAME_ALARM, null);
        return json;
    }


    public static void setAlarmInDevice(int hourOfDay, int minute, Context context, String message, long alarmId, int day) {
        AlarmManager alarmManager;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent myIntent = new Intent(context, AlarmReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.MED_NAME_LIST, message);
        bundle.putLong(Constants.ALARM_IDENTIFIER, alarmId); //long timestamp
        bundle.putInt(Constants.ALARM_DAY, day); //Set the day for alarm

        myIntent.putExtra(Constants.ALARM_BUNDLE, bundle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
        if (day > 0) {
            //Repeat Alarm
            alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 7 * 1440 * 60000, pendingIntent);
        }
    }

    public static void cancelAlarmInDevice(Context context, String message, long alarmId, int day) {
        AlarmManager alarmManager;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent myIntent = new Intent(context, AlarmReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.MED_NAME_LIST, message);
        bundle.putLong(Constants.ALARM_IDENTIFIER, alarmId); //long timestamp
        bundle.putInt(Constants.ALARM_DAY, day); //Set the day for alarm
        myIntent.putExtra(Constants.ALARM_BUNDLE, bundle);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

    public static void removeAlarmFromSystem(Context context, MedicineViewItems medToBeDeleted) {
        // Get the alarm reminder object to be deleted.
        if (medToBeDeleted.isAlarmSetting()) {
            // Check if the alarm was set for the reminder to be deleted. If alarm was set on
            // then delete all the alarms one by one, by getting list of alarm days set.
            ArrayList<Integer> selectedDays = getDaysSelected(medToBeDeleted);
            if (selectedDays.size() > 0) {
                for (int i = 0; i < selectedDays.size(); i++) {
                    Utility.cancelAlarmInDevice(context, medToBeDeleted.getMedicineName(), medToBeDeleted.getId(), selectedDays.get(i));
                }
            } else {
                Utility.cancelAlarmInDevice(context, medToBeDeleted.getMedicineName(), medToBeDeleted.getId(), 0);
            }
        }
    }

    /***********************************************************************************************
     * Convert Long millisecond time to AM/PM format.
     */
    public static String convertTimeToAMPMWithSeconds(long milisec) {
        Time time1 = new Time(milisec);

        int hours = time1.getHours();
        int mins = time1.getMinutes();

        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";


        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        long seconds = milisec % 60;

        // Append in a StringBuilder
        String aTime = new StringBuilder()
                .append(hours).append(':')
                .append(minutes).append(":")
                .append(String.format("%02d", seconds)).append(" ")
                .append(timeSet).toString();

        return aTime;

    }

    /***********************************************************************************************
     * Get selected days on the UI. For each day we need to set repeating alarm.
     */
    public static ArrayList<Integer> getDaysSelected(MedicineViewItems medicineViewItems) {
        ArrayList<Integer> daysSelected = new ArrayList<>();
        if (medicineViewItems.isMonAlarmOn()) {
            daysSelected.add(Calendar.MONDAY);
        }
        if (medicineViewItems.isTueAlarmOn()) {
            daysSelected.add(Calendar.TUESDAY);
        }
        if (medicineViewItems.isWedAlarmOn()) {
            daysSelected.add(Calendar.WEDNESDAY);
        }
        if (medicineViewItems.isThuAlarmOn()) {
            daysSelected.add(Calendar.THURSDAY);
        }
        if (medicineViewItems.isFriAlarmOn()) {
            daysSelected.add(Calendar.FRIDAY);
        }
        if (medicineViewItems.isSatAlarmOn()) {
            daysSelected.add(Calendar.SATURDAY);
        }
        if (medicineViewItems.isSunAlarmOn()) {
            daysSelected.add(Calendar.SUNDAY);
        }
        return daysSelected;
    }

    public static void setSnoozeAlarm(long mili, Context context, String message) {
        AlarmManager alarmManager;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent myIntent = new Intent(context, AlarmReceiver.class);
        myIntent.putExtra(Constants.MED_NAME_LIST, message);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);

       /* Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);*/

        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + mili, pendingIntent);
    }

    /***********************************************************************************************
     * Load and start the animation.
     */

}
