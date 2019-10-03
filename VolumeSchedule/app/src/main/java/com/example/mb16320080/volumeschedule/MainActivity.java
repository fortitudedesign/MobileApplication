package com.example.mb16320080.volumeschedule;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import java.text.DateFormat;
import java.util.Calendar;

/*
    Assignment 3
    Matthew Buckwell
    16320080

    Version:    1.0
    Description:
                This android application allows for the user to set a time on the clock that's displayed,
                dependant on the button the user presses 'Set Silent' or 'Set Normal', the phone will
                react accordingly on that specific time - either by going completely silent, or setting
                the volumes of Ring, Media and Notification to a pre-determined volume.

                If the user wishes to reset the setting before the request then pressing the 'Reset'
                button will do just that and keep the phone at its current state, if the user picks a
                time before the current time then this will be set for that time on the following day.

                Layout changes when switching from Portrait to Landscape, this allows for the app
                to continue to have a user-friendly display and no issues with placement of buttons
                 or text view.

    Reason:
                This idea was decided upon from personal reasons, I would set my phone to silent going
                into lectures but mostly would forget to set it back, this would result in messages
                and phone calls being missed.

    Potential Features:
                - Allow the user to set the volumes for Ring, Media and Notification when setting
                  the phone to normal
                - Allow user to set multiple alarms and display them on screen
                - Allow user to pick the days and if they want it reoccurring
 */

public class MainActivity extends Activity {

    // Class wide view of the TimePicker and TextViews variables
    TimePicker timePicker;
    TextView silentText;
    TextView normalText;

    // Class wide view of necessary variables
    private int hour = 0;
    private int min = 0;
    private String HOUR;
    private String MIN;
    boolean status = true;

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        // Saves the state of hour and min if going away from application
        savedInstanceState.putInt(HOUR, hour);
        savedInstanceState.putInt(MIN, min);
        // Call to base class
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Call to base class
        super.onCreate(savedInstanceState);

        // If savedInstance isn't null then reload the values for hour and min from previous state
        if (savedInstanceState != null) {
            hour = savedInstanceState.getInt(HOUR);
            min = savedInstanceState.getInt(MIN);
        }

        // Set content view with the main activity
        setContentView(R.layout.activity_main);

        // Set the id's to the TextViews and TimePicker
        timePicker = findViewById(R.id.timePicker);
        silentText = findViewById(R.id.setSilent);
        normalText = findViewById(R.id.setNormal);

        // Request the DO NOT DISTURB permission, requires user to manual switch on for app
        NotificationManager mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !mNotificationManager.isNotificationPolicyAccessGranted()) {

            Intent mIntentNotification = new Intent(android.provider.Settings
                    .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

            startActivity(mIntentNotification);
        }

        // Setting onCLickListener to "Set Silent" button
        findViewById(R.id.btnSilent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Boolean status set to true to identify 'Set Silent' button pressed
                status = false;
                setTime(status);
            }
        });

        // Setting onClickListener to "Set Normal" button
        findViewById(R.id.btnNormal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Boolean status set to true to identify 'Set Normal' button pressed
                status = true;
                setTime(status);
            }
        });

        // Setting onClickListener to "Reset" button
        findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetAlarm();
            }
        });
    }

    public void setTime(boolean mBool) {
        // Instantiating variable for Calendar
        Calendar setTimeCalendar = Calendar.getInstance();

        // Variables to hold the hour and min to be used to set time and to be passed to savedInstanceState
        // SDK >= 23 uses getHour() and getMinute() - SDK < 23 uses getCurrentHour() and getCurrentMinute()
        if (Build.VERSION.SDK_INT >= 23) {
            hour = timePicker.getHour();
            min = timePicker.getMinute();
        } else {
            hour = timePicker.getCurrentHour();
            min = timePicker.getCurrentMinute();
        }

        // Get and set the details the user has selected from the TimePicker
        setTimeCalendar.set(
                    setTimeCalendar.get(Calendar.YEAR),
                    setTimeCalendar.get(Calendar.MONTH),
                    setTimeCalendar.get(Calendar.DAY_OF_MONTH),
                    hour,
                    min,
                    0);

        // Calling functions and passing required parameters
        updateScheduleText(setTimeCalendar, mBool);
        setVolume(setTimeCalendar, mBool);
    }

    // Function to update the text view when the time set by the user
    private void updateScheduleText(Calendar updateCalendar, Boolean bool) {
        // Boolean used to determine which alarm has been set and setText appropriately
        // String to hold the display text in the text view, passed when setting text
        String textDisplay;
        if (!bool) {
            textDisplay = getString(R.string.SilentText) +
                    " " + DateFormat.getTimeInstance(DateFormat.SHORT).format(updateCalendar.getTime());
            silentText.setText(textDisplay);
        } else if (bool) {
            textDisplay = getString(R.string.NormalText) +
                    " " + DateFormat.getTimeInstance(DateFormat.SHORT).format(updateCalendar.getTime());
            normalText.setText(textDisplay);
        }
    }

    // Private function to set the time and activate the right class dependant on boolean value
    private void setVolume(Calendar setVolumeCalendar, Boolean bool) {
        // Instantiating AlarmManager variable with system service
        AlarmManager volumeAlarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Instantiating a new intent
        Intent volumeIntent = new Intent();

        // Dependant on the boolean value, Intent will be passed to the correct class to broadcast
        if (!bool) {
            volumeIntent = new Intent(this, SetSilent.class);
        } else if (bool) {
            volumeIntent = new Intent (this, SetNormal.class);
        }

        // Instantiating PendingIntent and passing parameter to broadcast to
        PendingIntent alarmPending = PendingIntent.getBroadcast(this, 0,
                volumeIntent, 0);

        // Checks to see if time isn't before current time, if so it will be scheduled for the next day
        if (setVolumeCalendar.before(Calendar.getInstance())) {

            setVolumeCalendar.add(Calendar.DATE, 1);
        }

        // Setting the alarm, SDK 19 and higher uses setExact() while previous SDK use set()
        if (Build.VERSION.SDK_INT >= 19) {
            volumeAlarm.setExact(AlarmManager.RTC_WAKEUP, setVolumeCalendar.getTimeInMillis(), alarmPending);
        } else {
            volumeAlarm.set(AlarmManager.RTC_WAKEUP, setVolumeCalendar.getTimeInMillis(), alarmPending);
        }
    }

    private void resetAlarm() {
        // Instantiating AlarmManager variable with system service
        AlarmManager resetAlarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Instantiating an object array of new intent and setting the classes
        Intent[] resetIntent = { new Intent(this, SetSilent.class),
                new Intent(this, SetNormal.class) };

        // Instantiating PendingIntent and passing parameter to cancel broadcast to
        for (int i = 0; i < 2; i++) {
            PendingIntent resetPending = PendingIntent.getBroadcast(this, 0,
                    resetIntent[i], 0);

            // Calling to cancel the passing pending parameter after checking if pending doesn't equal null
            if (resetPending != null) {
                resetAlarms.cancel(resetPending);
            }
        }

        // Update the text fields to clear the times
        silentText.setText("");
        normalText.setText("");
    }
}
