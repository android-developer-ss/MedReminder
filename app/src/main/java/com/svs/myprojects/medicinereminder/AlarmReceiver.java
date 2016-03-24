package com.svs.myprojects.medicinereminder;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;


/**
 * Created by snehalsutar on 2/16/16.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        //this will update the UI with message
//            AlarmActivity inst = AlarmActivity.instance();
//            inst.setAlarmText("Alarm! Wake up! Wake up!");

//        String message = intent.getExtras().getString(Constants.MED_NAME_LIST);
        Bundle bundle = intent.getExtras().getBundle(Constants.ALARM_BUNDLE);
        //Log.i("svsme", message);

        Intent myIntent = new Intent(context,AlarmNotification.class);
//        myIntent.setClassName("com.svs.myprojects.mymedicalrecords.patientrecord.medicinereminder",
//                ".AlarmNotification");
        myIntent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        myIntent.putExtra(Constants.ALARM_BUNDLE, bundle);
        context.startActivity(myIntent);
//        //this will sound the alarm tone
//        //this will sound the alarm once, if you wish to
//        //raise alarm in loop continuously then use MediaPlayer and setLooping(true)
//        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//        if (alarmUri == null) {
//            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        }
//        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
//        ringtone.play();
//
////        sendNotification();
//        //this will send a notification message
//        ComponentName comp = new ComponentName(context.getPackageName(),
//                AlarmService.class.getName());
//        startWakefulService(context, (intent.setComponent(comp)));
//        setResultCode(Activity.RESULT_OK);
    }

    /*public void sendNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.pill_blue)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this.g, MainActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(mId, mBuilder.build());
    }*/

    class AlarmService extends IntentService {
        private NotificationManager alarmNotificationManager;

        public AlarmService() {
            super("AlarmService");
        }

        @Override
        public void onHandleIntent(Intent intent) {
            sendNotification("Wake Up! Wake Up!");
        }

        private void sendNotification(String msg) {
            Log.d("AlarmService", "Preparing to send notification...: " + msg);
            alarmNotificationManager = (NotificationManager) this
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, MainActivity.class), 0);

            NotificationCompat.Builder alamNotificationBuilder = new NotificationCompat.Builder(
                    this).setContentTitle("Alarm").setSmallIcon(R.drawable.pill_baw)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                    .setContentText(msg);


            alamNotificationBuilder.setContentIntent(contentIntent);
            alarmNotificationManager.notify(1, alamNotificationBuilder.build());
//            Toast.makeText()
            Log.d("AlarmService", "Notification sent.");
        }
    }
}