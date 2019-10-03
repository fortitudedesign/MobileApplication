package com.example.mb16320080.volumeschedule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

public class SetSilent extends BroadcastReceiver {

    // Broadcast receiver for when the user selected alarm is reached, onReceive is activated
    @Override
    public void onReceive(Context context, Intent intent) {
        // Instantiating AudioManager variable with a system service
        AudioManager mAudio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        // The following silences the Ringtone and sets the Media and Notification to volume level zero
        mAudio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        mAudio.setStreamVolume(AudioManager.STREAM_MUSIC,0,0);
        mAudio.setStreamVolume(AudioManager.STREAM_NOTIFICATION,0,0);
    }
}
