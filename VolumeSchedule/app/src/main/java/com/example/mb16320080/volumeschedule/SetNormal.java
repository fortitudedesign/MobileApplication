package com.example.mb16320080.volumeschedule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

public class SetNormal extends BroadcastReceiver {

    // Broadcast receiver for when the user selected alarm is reached, onReceive is activated
    @Override
    public void onReceive(Context context, Intent intent) {
        // Instantiating AudioManager variable with a system service
        AudioManager mAudioNormal = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        // The following sets the sound for Ringtone, Media and Notifications
        mAudioNormal.setStreamVolume(AudioManager.STREAM_RING, 8, 0);
        mAudioNormal.setStreamVolume(AudioManager.STREAM_MUSIC,8,0);
        mAudioNormal.setStreamVolume(AudioManager.STREAM_NOTIFICATION,4,0);
    }
}
