package com.svs.myprojects.medicinereminder;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by snehalsutar on 2/16/16.
 */


public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private long mMinDate;
    private long mMaxDate;

    public void setMinDate(long date) {
        mMinDate = date;
    }

    public void setMaxDate(long date) {
        mMaxDate = date;
    }

    //region On Date selected listener
    private OnDateSelected mOnDateSelected;

    public interface OnDateSelected {
        public void onDateSelected(String date);
    }

    public void setOnDateSelected(OnDateSelected onDateSelected) {
        mOnDateSelected = onDateSelected;
    }

    // For getting year month day separately.
    private OnYearMonthDaySelect mGetYearMonthDay;

    public interface OnYearMonthDaySelect {
        void onYearMonthDaySelected(int year, int month, int day);
    }

    public void setOnYearMonthDaySelected(OnYearMonthDaySelect getYearMonthDay) {
        mGetYearMonthDay = getYearMonthDay;
    }

    //region

    private SimpleDateFormat mDateFormatt;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        mDateFormatt = new SimpleDateFormat("MMM d yyyy", Locale.US);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        DatePicker datePicker = datePickerDialog.getDatePicker();
        if (mMinDate > 0)
            datePicker.setMinDate(mMinDate);//get the current day
        if (mMaxDate > 0)
            datePicker.setMaxDate(mMaxDate);
        return datePickerDialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        if (mOnDateSelected != null) {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, month, day);
            String dateStr = mDateFormatt.format(newDate.getTime());
            mOnDateSelected.onDateSelected(dateStr);
        }
        if (mGetYearMonthDay != null) {
            mGetYearMonthDay.onYearMonthDaySelected(year, month, day);
        }
    }
}

/*com.syw.personalshopper.fragments.DatePickerFragment datePickerFragment = new com.syw.personalshopper.fragments.DatePickerFragment();
datePickerFragment.setMinDate(System.currentTimeMillis());
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
        datePickerFragment.setOnYearMonthDaySelected(new com.syw.personalshopper.fragments.DatePickerFragment.OnYearMonthDaySelect() {
@Override
public void onYearMonthDaySelected(int year, int month, int day) {
        textView.setText(AppHelper.formatDateToDisplay(AppHelper.formatDateToStore(year, month, day)));
        mCustomOccDateToSave = AppHelper.formatDateToStore(year, month, day);
        }
        });*/

//public class DatePickerFragment extends DialogFragment
//        implements DatePickerDialog.OnDateSetListener {
//
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        // Use the current date as the default date in the picker
//        final Calendar c = Calendar.getInstance();
//        int year = c.get(Calendar.YEAR);
//        int month = c.get(Calendar.MONTH);
//        int day = c.get(Calendar.DAY_OF_MONTH);
//
//        // Create a new instance of DatePickerDialog and return it
//        return new DatePickerDialog(getActivity(), this, year, month, day);
//    }
//
//    public void onDateSet(DatePicker view, int year, int month, int day) {
//        // Do something with the date chosen by the user
//    }
//}
