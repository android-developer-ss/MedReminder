package com.svs.myprojects.medicinereminder;

import java.sql.Time;
import java.util.Date;

/**
 * Created by snehalsutar on 2/15/16.
 */
public class MedicineViewItems {


    long id;
    String medicineName;
    Date date;
    Time time;
    boolean alarmSetting;
    boolean monAlarmOn;
    boolean tueAlarmOn;
    boolean wedAlarmOn;
    boolean thuAlarmOn;
    boolean friAlarmOn;
    boolean satAlarmOn;
    boolean sunAlarmOn;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    public boolean isSunAlarmOn() {
        return sunAlarmOn;
    }

    public void setSunAlarmOn(boolean sunAlarmOn) {
        this.sunAlarmOn = sunAlarmOn;
    }

    public boolean isSatAlarmOn() {
        return satAlarmOn;
    }

    public void setSatAlarmOn(boolean satAlarmOn) {
        this.satAlarmOn = satAlarmOn;
    }

    public boolean isFriAlarmOn() {
        return friAlarmOn;
    }

    public void setFriAlarmOn(boolean friAlarmOn) {
        this.friAlarmOn = friAlarmOn;
    }

    public boolean isThuAlarmOn() {
        return thuAlarmOn;
    }

    public void setThuAlarmOn(boolean thuAlarmOn) {
        this.thuAlarmOn = thuAlarmOn;
    }

    public boolean isWedAlarmOn() {
        return wedAlarmOn;
    }

    public void setWedAlarmOn(boolean wedAlarmOn) {
        this.wedAlarmOn = wedAlarmOn;
    }

    public boolean isTueAlarmOn() {
        return tueAlarmOn;
    }

    public void setTueAlarmOn(boolean tueAlarmOn) {
        this.tueAlarmOn = tueAlarmOn;
    }

    public boolean isMonAlarmOn() {
        return monAlarmOn;
    }

    public void setMonAlarmOn(boolean monAlarmOn) {
        this.monAlarmOn = monAlarmOn;
    }


    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public boolean isAlarmSetting() {
        return alarmSetting;
    }

    public void setAlarmSetting(boolean alarmSetting) {
        this.alarmSetting = alarmSetting;
    }
}
