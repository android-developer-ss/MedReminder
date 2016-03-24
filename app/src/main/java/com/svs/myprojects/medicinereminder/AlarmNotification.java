package com.svs.myprojects.medicinereminder;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class AlarmNotification extends AppCompatActivity implements View.OnClickListener,
        TextToSpeech.OnInitListener {

    /***********************************************************************************************
     * Member Variable Declaration.
     */
    private Button stopButton, snoozeButton;
    private TextToSpeech textToSpeech;
    private TextView textTakeMed;
    private Spinner spinnerSnoozeTime;
    private String message;
    private Vibrator vibrator;
    private AudioManager audioManager;
    private Bundle bundle;
    private int mSnoozeTimeSelected;

    /***********************************************************************************************
     * On Create method for Alarm notification.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_notification);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        stopButton = (Button) findViewById(R.id.stop_button);
        snoozeButton = (Button) findViewById(R.id.button_snooze);
        textTakeMed = (TextView) findViewById(R.id.text_take_ur_med);
        stopButton.setOnClickListener(this);
        snoozeButton.setOnClickListener(this);

        textToSpeech = new TextToSpeech(getApplicationContext(), AlarmNotification.this);
        bundle = getIntent().getExtras().getBundle(Constants.ALARM_BUNDLE);
        message = bundle.getString(Constants.MED_NAME_LIST);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        spinnerSnoozeTime = (Spinner)findViewById(R.id.spinner_snooze_mins);
        String[] snoozeTimeArray = getResources().getStringArray(R.array.snooze_timing);
        ArrayAdapter<String> adapterET = new ArrayAdapter<>(AlarmNotification.this, R.layout.spinner_list_item, snoozeTimeArray);
        spinnerSnoozeTime.setAdapter(adapterET);
//        spinnerSnoozeTime.setSelection(5);
        spinnerSnoozeTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSnoozeTimeSelected = Integer.parseInt(spinnerSnoozeTime.getSelectedItem().toString());
                Toast.makeText(AlarmNotification.this,
                        "You have selected " + spinnerSnoozeTime.getSelectedItem().toString(),
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    /***********************************************************************************************
     * On Start method for Alarm notification.
     */
    @Override
    protected void onStart() {
        super.onStart();
        long[] pattern = createPattern();
        textTakeMed.setText(message);
        if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
            vibrator.vibrate(pattern, 3);
        }
    }

    /***********************************************************************************************
     * On Resume method for Alarm notification.
     */
    @Override
    protected void onResume() {
        super.onResume();
        //Start animation for Medicine List.
        Animation myFadeInAnimation = AnimationUtils.loadAnimation(AlarmNotification.this, R.anim.tween_fade);
        textTakeMed.startAnimation(myFadeInAnimation);
    }

    /***********************************************************************************************
     * On Pause method for Alarm notification.
     */
    @Override
    protected void onPause() {
        super.onPause();
    }

    /***********************************************************************************************
     * OnClickListener implemented method.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.stop_button:
                switchOffAlarmButtonIfAlarmSetOnce();
                stopButtonFunction();
                break;
            case R.id.button_snooze:
                long tempMili = mSnoozeTimeSelected * 60000;
                Toast.makeText(AlarmNotification.this, tempMili+"", Toast.LENGTH_LONG).show();
                Utility.setSnoozeAlarm(tempMili, this, message);
                stopButtonFunction();
                break;
        }
    }

    /***********************************************************************************************
     * TextToSpeech.OnInitListener implemented method.
     */
    @Override
    public void onInit(int status) {
        //Start audio only if mobile is on SOUND mode else go for vibration.
        if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US);
                Toast.makeText(AlarmNotification.this, "OK", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AlarmNotification.this, "NOT OK", Toast.LENGTH_SHORT).show();
            }
            String constText = getResources().getString(R.string.take_your_med_text) + "  " + message;
            String textToRead = "";
            for (int i = 0; i < 3; i++) {
                textToRead += constText + ".  ";
            }
            textToSpeech.setSpeechRate(0.8f);
            textToSpeech.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    /***********************************************************************************************
     * Create vibration pattern to vibrate while mobile is in vibration mode.
     */
    private long[] createPattern() {

        int dot = 200;      // Length of a Morse Code "dot" in milliseconds
        int dash = 500;     // Length of a Morse Code "dash" in milliseconds
        int short_gap = 200;    // Length of Gap Between dots/dashes
        int medium_gap = 500;   // Length of Gap Between Letters
        int long_gap = 1000;    // Length of Gap Between Words
        long[] pattern = {
                0,  // Start immediately
                dot, short_gap, dot, short_gap, dot,    // s
                medium_gap,
                dash, short_gap, dash, short_gap, dash, // o
                medium_gap,
                dot, short_gap, dot, short_gap, dot,    // s
                long_gap
        };

        return pattern;
    }

    /***********************************************************************************************
     * When stop button is clicked check the system state and stop the respective vibration/voice.
     */
    public void stopButtonFunction() {
        if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
            textToSpeech.stop();
        }
        if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
            vibrator.cancel();
        }
        if(vibrator!=null){
            vibrator.cancel();
        }

        finish();
    }

    public void switchOffAlarmButtonIfAlarmSetOnce(){
        // Check if the Alarm is supposed to ring just for 1 time. If yes then change the alarm
        // ON-OFF the button to OFF.
        int day = bundle.getInt(Constants.ALARM_DAY);
        long key = bundle.getLong(Constants.ALARM_IDENTIFIER);
        if (day == 0) {
            // Switch OFF the alarm button.
            ArrayList<MedicineViewItems> alarmArrayList = Utility.getAlarmListArrayList(AlarmNotification.this);
            for (int i = 0; i < alarmArrayList.size(); i++) {
                MedicineViewItems medicineViewItems = alarmArrayList.get(i);
                if(medicineViewItems.getId()==key){
                    medicineViewItems.setAlarmSetting(false);
                    break;
                }
            }
            Utility.saveAlarmListToSharedPref(AlarmNotification.this,alarmArrayList);
        }
    }

}
